package com.moodmemo.office.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
@Log4j2
@RequiredArgsConstructor
@Service
public class S3Uploader2 {
    @Value("${cloud.aws.s3.bucket.name}")
    private String bucket;
    // Amazon S3 클라이언트 생성
//    private S3Client s3Client = S3Client.create();
//    // S3에 파일 업로드 메서드
//
//
//    public void uploadFileToS3(File file) {
//
//        String uploadFileName = "tmp.jpg";
//        // PutObjectRequest 생성
//        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
//                .bucket(bucket)
//                .key(uploadFileName)
//                .build();
//        // 파일 업로드
//        s3Client.putObject(putObjectRequest, file.toPath());
//        // 파일 URL
//        String fileUrl = s3Client.utilities().getUrl(builder -> builder.bucket(bucket).key(uploadFileName)).toExternalForm();
//        System.out.println(fileUrl);
//        // 파일 삭제
////        file.delete();
//    }
}
