//package com.kustaurant.kustaurant.evaluation.evaluation.service;
//
//import com.amazonaws.AmazonServiceException;
//import com.amazonaws.SdkClientException;
//import com.amazonaws.services.s3.AmazonS3Client;
//import com.amazonaws.services.s3.model.ObjectMetadata;
//import com.kustaurant.kustaurant.global.exception.exception.ServerException;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.util.UUID;
//
//@Service
//@Slf4j
//@RequiredArgsConstructor
//public class S3Service {
//    private final AmazonS3Client amazonS3Client;
//    @Value("${cloud.aws.s3.bucket}")
//    private String bucket;
//
//    public String uploadFile(MultipartFile file) {
//        String folderPath = "restaurant-comment/"; // 새 폴더 경로
//        String originalFileName = file.getOriginalFilename();
//        String fileName = folderPath + UUID.randomUUID() + (originalFileName != null && !originalFileName.isEmpty() ? "-" + originalFileName.trim() : "");
//        String fileUrl = "https://" + bucket + ".s3.ap-northeast-2.amazonaws.com/" + fileName;
//        log.info("새로 추가된 평가 이미지: {}", fileUrl);
//
//        ObjectMetadata metadata = new ObjectMetadata();
//        metadata.setContentType(file.getContentType());
//        metadata.setContentLength(file.getSize());
//        try {
//            // s3에 저장
//            amazonS3Client.putObject(bucket, fileName, file.getInputStream(), metadata);
//        } catch (IOException e) {
//            throw new ServerException("이미지 저장 과정에서 문제가 발생했습니다.");
//        } catch (AmazonServiceException e) {
//            throw new ServerException("s3 관련 문제");
//        } catch (SdkClientException e) {
//            throw new ServerException("s3 관련 문제");
//        }
//
//        return fileUrl;
//    }
//
//    public void deleteFile(String imgUrl) {
//        if (imgUrl == null || imgUrl.isEmpty()) {
//            return;
//        }
//
//        // ".com/"의 위치를 찾습니다.
//        int index = imgUrl.indexOf(".com/");
//
//        // ".com/" 이후의 문자열을 추출합니다.
//        if (index != -1) {
//            String fileName = imgUrl.substring(index + 5); // 5는 ".com/"의 길이
//            amazonS3Client.deleteObject(bucket, fileName);
//        }
//    }
//}

// TODO: 이전 implementation 'org.springframework.cloud:spring-cloud-starter-aws:2.2.6.RELEASE'
// TODO: 의존성을 없애고 최신버전으로 교체했는데 작동하는지는 모르겠음 확인필요

package com.kustaurant.kustaurant.evaluation.evaluation.service;

import com.kustaurant.kustaurant.global.exception.exception.ServerException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    public String uploadFile(MultipartFile file) {
        String folderPath = "restaurant-comment/";
        String originalName = file.getOriginalFilename();
        String key = folderPath + UUID.randomUUID()
                + (originalName != null && !originalName.isBlank() ? "-" + originalName.trim() : "");

        try {
            PutObjectRequest req = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(req,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            String url = "https://" + bucket + ".s3.ap-northeast-2.amazonaws.com/" + key;
            log.info("새로 추가된 평가 이미지: {}", url);
            return url;

        } catch (IOException e) {
            throw new ServerException("이미지 스트림 처리 중 오류가 발생했습니다.", e);
        } catch (S3Exception e) {
            throw new ServerException("S3 업로드 중 오류가 발생했습니다.", e);
        }
    }

    public void deleteFile(String imgUrl) {
        if (imgUrl == null || imgUrl.isBlank()) return;

        int idx = imgUrl.indexOf(".com/");
        if (idx == -1) return;

        String key = imgUrl.substring(idx + 5);   // ".com/" 이후
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build());
            log.info("삭제된 평가 이미지: {}", imgUrl);
        } catch (S3Exception e) {
            throw new ServerException("S3 삭제 중 오류가 발생했습니다.", e);
        }
    }
}
