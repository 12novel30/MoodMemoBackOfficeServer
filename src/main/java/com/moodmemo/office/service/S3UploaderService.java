package com.moodmemo.office.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.IOUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.util.UUID;

import static com.moodmemo.office.code.OfficeCode.LOCAL_FOLDER;

@Log4j2
@RequiredArgsConstructor
@Service
public class S3UploaderService {
    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket.name}")
    private String bucket;

    public String upload(MultipartFile file, String dirName) throws IOException {
        String storedFilePath = dirName + "/" + UUID.randomUUID() + file.getOriginalFilename();
        amazonS3Client.putObject(new PutObjectRequest(bucket, storedFilePath, file.getInputStream(), null)
                .withCannedAcl(CannedAccessControlList.PublicRead));
        return amazonS3Client.getUrl(bucket, storedFilePath).toString();
    }

    public void deleteImage(String fileName) {
        amazonS3Client.deleteObject(new DeleteObjectRequest(bucket, fileName));
    }

    public String getFileUrl(String path) {
        return amazonS3Client.getUrl(bucket, path).toString();
    }

    public String kakaoImageUrlSaveToLocal(String imageUrl, String imageName) {
        try {
            URL imgURL = new URL(imageUrl);
            String fileName = imageName; // 이미지 이름
            BufferedImage image = ImageIO.read(imgURL);
            File file = new File(
                    LOCAL_FOLDER.getDescription() + "/" + fileName + ".jpg");
            if (!file.exists()) { // 해당 경로의 폴더가 존재하지 않을 경우
                file.mkdirs(); // 해당 경로의 폴더 생성
            }

            ImageIO.write(image, "jpg", file); // image를 file로 업로드
            System.out.println("finish to create local file!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imageName;
    }

    public MultipartFile convertFileToMultipartFile(String fileName) throws IOException {
        // get local file
        File file = new File(LOCAL_FOLDER.getDescription() + "/" + fileName + ".jpg");
        if (file.exists()) {
            System.out.println("file exists");
        } else {
            System.out.println("file not exists");
        }

        // convert file to MultipartFile
        FileItem fileItem = new DiskFileItem("file", Files.probeContentType(file.toPath()), false, file.getName(), (int) file.length(), file.getParentFile());
        try (InputStream input = new FileInputStream(file);
             OutputStream os = fileItem.getOutputStream()) {
            IOUtils.copy(input, os);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid file: " + e, e);
        }
        System.out.println("6");
        // 이 밑에서 에러가 남

        System.out.println("1");
        FileInputStream input = new FileInputStream(file);
        System.out.println("12");
        return new MockMultipartFile("file.jpg",
                file.getName(), "text/plain", IOUtils.toByteArray(input));
//        return new CommonsMultipartFile(fileItem);
    }

    public void deleteLocalImage(String imageName) {
        File file = new File(LOCAL_FOLDER.getDescription() + "/" + imageName + ".jpg");
        if (file.exists()) {
            if (file.delete()) {
                System.out.println("success to delete file");
            } else {
                System.out.println("fail to delete file");
            }
        } else {
            System.out.println("file not exists");
        }
    }

//    @Bean
//    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
//        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(mapper);
//        return converter;
//    } -> date 타입을 미치게 해서 포기함 ... 짜증나 돌아버리겠다
}