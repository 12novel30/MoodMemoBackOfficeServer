package com.moodmemo.office;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
@EnableMongoRepositories
public class OfficeApplication {

//    public static final String APPLICATION_LOCATIONS = "spring.config.location="
//            + "classpath:application.yml,"
//            + "classpath:application-s3.yml";

    @PostConstruct
    public void started() {
        // timezone UTC 셋팅
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
    }

    public static void main(String[] args) {
        SpringApplication.run(OfficeApplication.class, args);
//        new SpringApplicationBuilder(OfficeApplication.class)
//                .properties(APPLICATION_LOCATIONS)
//                .run(args);
        System.out.println("fight!");
    }

    @Bean // TODO - FE 에서 필요 없으면 삭제할 것
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("*")
                        .allowedMethods("*")
                        .allowedHeaders("*")
                        .allowCredentials(false)
                        .maxAge(3600);
            }
        };
    }

}
