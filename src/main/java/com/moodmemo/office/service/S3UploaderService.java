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
import org.springframework.http.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.imageio.ImageIO;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
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


//    public MultipartFile downloadPhoto(String photoUrl) throws URISyntaxException, IOException {
//        RestTemplate restTemplate = new RestTemplate();
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.set(HttpHeaders.ACCEPT, MediaType.IMAGE_JPEG_VALUE);
//
//        RequestEntity<Void> requestEntity = new RequestEntity<>(headers, HttpMethod.GET, new URI(photoUrl));
//        ResponseEntity<byte[]> responseEntity = restTemplate.exchange(requestEntity, byte[].class);
//
//        byte[] imageBytes = responseEntity.getBody();
//
//        ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
//
//        return new CommonsMultipartFile((FileItem) new ByteArrayInputStream(imageBytes));
//    }




    public String kakaoImageUrlSaveToLocal(String imageUrl, String imageName) {
        try {
            File file = new File(LOCAL_FOLDER.getDescription()
                    + "/" + imageName + ".jpg");
            if (!file.exists()) { // 해당 경로의 폴더가 존재하지 않을 경우
                file.mkdirs(); // 해당 경로의 폴더 생성
            }
            ImageIO.write(ImageIO.read(new URL(imageUrl)),
                    "jpg", file); // image를 file로 업로드
            log.info("finish to create local file!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imageName;
    }

    public void store() {
        File file = new File(LOCAL_FOLDER.getDescription()
                + "/testImageName.jpg");
//        String storedFilePath = "test" + "/" + UUID.randomUUID() + file.getName();
        String storedFilePath = UUID.randomUUID() + file.getName();
        if (file.exists())
            log.info("file exists");
        else
            log.info("file not exists");
//        File file = new File(MultipartUtil.getLocalHomeDirectory(), fullPath);
        try {
//            multipartFile.transferTo(file);
            log.info(file.length());
            amazonS3Client.putObject(new PutObjectRequest(bucket, storedFilePath, file)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (Exception e) {
            throw new RuntimeException();
        } finally {
            if (file.exists()) {
                file.delete();
            }
        }
    }
























    public MultipartFile convertFileToMultipartFile(String fileName) throws IOException {
        // get local file
        File file = new File(LOCAL_FOLDER.getDescription() + "/" + fileName + ".jpg");
        if (file.exists())
            log.info("file exists");
        else
            log.info("file not exists");

        // convert file to MultipartFile
        log.info(file.getAbsolutePath());
        FileItem fileItem = new DiskFileItem("file", Files.probeContentType(file.toPath()), false, file.getName(), (int) file.length(), file.getParentFile());
        try (InputStream input = new FileInputStream(file);
             OutputStream os = fileItem.getOutputStream()) {
            IOUtils.copy(input, os);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid file: " + e, e);
        }
        System.out.println("6");

        return new CommonsMultipartFile(fileItem);

//        return FileUtil.convertToMultipartFile(file);
    }
//    CommonsMultipartFile multipartFile = new CommonsMultipartFile(fileItem);
//    ByteArrayMultipartFileEditor multipartFileEditor = new ByteArrayMultipartFileEditor();
//        multipartFileEditor.setAsText(multipartFile.getName());
//        return (MultipartFile) multipartFileEditor.getValue();

    //
//        System.out.println("1");
//        FileInputStream input = new FileInputStream(file);
//        System.out.println("12");
//        return new MockMultipartFile("file.jpg",
//                file.getName(), "text/plain", IOUtils.toByteArray(input));
//        return new CommonsMultipartFile(fileItem);

    public void deleteLocalImage(String imageName) {
        File file = new File(LOCAL_FOLDER.getDescription() + "/" + imageName + ".jpg");
        if (file.exists())
            if (file.delete())
                log.info("success to delete file");
            else
                log.info("fail to delete file");
        else
            log.info("file not exists");
    }

    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(mapper) {
        };
        return converter;
    } // -> date 타입을 미치게 해서 포기함 ... 짜증나 돌아버리겠다











}//    public void uploadFileToS3(File file, String bucket, String key) {
//        // PutObjectRequest를 사용하여 파일을 S3에 업로드
//        PutObjectRequest request = new PutObjectRequest(bucket, "testImageName.jpg", file);
//        // 파일의 내용을 S3에 업로드
//        s3Client.putObject(request, file.toPath());
//    }
//
//    public void uploadFileToS3(File file) {
//        // Amazon S3 버킷 이름과 파일이 저장될 위치를 지정
//        String bucketName = "your-s3-bucket-name";
//        String key = "testImageName.jpg"; // 원하는 파일 경로와 파일명 지정
//
//        // 파일을 S3에 업로드
//        uploadFileToS3(file, bucketName, key);
//    }