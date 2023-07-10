package com.moodmemo.office.service;

import com.moodmemo.office.exception.OfficeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.moodmemo.office.code.OfficeCode.SEASON_3_FOLDER;
import static com.moodmemo.office.code.OfficeErrorCode.FAIL_FILE_DOWNLOAD;

@Log4j2
@RequiredArgsConstructor
@Service
public class S3UploaderService {
    @Autowired
    private S3Client s3Client;
    @Value("${cloud.aws.s3.bucket.name}")
    private String bucketName;

    private final DateTimeFormatter imageFormat =
            DateTimeFormatter.ofPattern("YYYY-MM-dd-HH-mm-ss");

    public String uploadImageFromUrl(String imageUrl, String kakaoId, LocalDateTime localDateTime) {
        try {
            String imageName = SEASON_3_FOLDER.getDescription() + "/" +
                    kakaoId + localDateTime.format(imageFormat);
            // 이미지 URL에서 이미지 데이터를 가져옵니다.
            URL url = new URL(imageUrl);
            Path tempFile = Files.createTempFile("temp", ".jpg");
            Files.copy(url.openStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);

            // S3에 이미지를 업로드합니다.
            PutObjectResponse response = s3Client.putObject(PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(imageName)
                    .build(), tempFile);

            // 업로드된 이미지의 URL을 반환합니다.
            return s3Client.utilities().getUrl(builder -> builder.bucket(bucketName).key(imageName)).toExternalForm();
        } catch (IOException e) {
            e.printStackTrace();
            // 업로드 실패 시 예외 처리
        }

        return null;
    }

    /*------ 사용 안함 -------*/
    public String uploadFileToS3(File file) {
        String key = SEASON_3_FOLDER.getDescription() + "/" + file.getName();
        // UUID.randomUUID() -> 너무 길어서 제거
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        log.info("** upload file to s3");
        log.info(file.getName());
        log.info(file.getAbsolutePath());
        s3Client.putObject(request, RequestBody.fromFile(file));

        log.info("** start to delete local file!");
        deleteLocalImage(file.getName());
        log.info("** finish to delete local file!");

        log.info("** get url from s3");
        return s3Client.utilities()
                .getUrl(builder -> builder.bucket(bucketName).key(key))
                .toExternalForm();
    }

    public File kakaoImageUrlSaveToLocal(String imageUrl,
                                         String kakaoId,
                                         LocalDateTime localDateTime) {

        uploadImageFromUrl(imageUrl, kakaoId, localDateTime);
        try {
            log.info("** start to create local file!");

//            File directory = new File("/home/ubuntu/path/to/photo/directory");
//            if (!directory.exists())
//                directory.mkdirs();
//
//            File file = new File(directory,
//                    kakaoId + localDateTime.format(imageFormat) + ".jpg");

            File file = new File(
                    kakaoId + localDateTime.format(imageFormat) + ".jpg");
            // not file

            ImageIO.write(ImageIO.read(new URL(imageUrl)),
                    "jpg", file); // image를 file에 저장

            log.info("** finish to create local file!");
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            throw new OfficeException(FAIL_FILE_DOWNLOAD);
        }
    }

    public void deleteLocalImage(String imageName) {
        File file = new File(imageName);
        if (file.exists())
            if (file.delete())
                log.info("success to delete file");
            else
                log.info("fail to delete file");
        else
            log.info("file not exists");
    }

}