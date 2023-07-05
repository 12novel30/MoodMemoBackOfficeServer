package com.moodmemo.office.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3Config {
    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;

    @Value("${cloud.aws.region.static}")
    private String region;

    @Bean
    public AmazonS3Client amazonS3Client() {
        BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(accessKey, secretKey);
        return (AmazonS3Client) AmazonS3ClientBuilder.standard().withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(basicAWSCredentials)).build();
    }

//    @Bean
//    public S3Client s3Client() {
//        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
////        Region awsRegion = Region.of(region);
////        Region awsRegion = Region.of("ap-northeast-2");
//        Region awsRegion = Region.AP_NORTHEAST_2;
//
//        return S3Client.builder()
//                .region(awsRegion)
//                .credentialsProvider(StaticCredentialsProvider.create(credentials))
//                .build();
//    }
}
