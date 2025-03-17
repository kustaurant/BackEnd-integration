package com.kustaurant.kustaurant.common.restaurant.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.kustaurant.kustaurant.global.exception.exception.ServerException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3Service {
    private final AmazonS3Client amazonS3Client;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String uploadFile(MultipartFile file) {
        String folderPath = "restaurant-comment/"; // 새 폴더 경로
        String originalFileName = file.getOriginalFilename();
        String fileName = folderPath + UUID.randomUUID() + (originalFileName != null && !originalFileName.isEmpty() ? "-" + originalFileName.trim() : "");
        String fileUrl = "https://" + bucket + ".s3.ap-northeast-2.amazonaws.com/" + fileName;
        log.info("새로 추가된 평가 이미지: {}", fileUrl);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());
        try {
            // s3에 저장
            amazonS3Client.putObject(bucket, fileName, file.getInputStream(), metadata);
        } catch (IOException e) {
            throw new ServerException("이미지 저장 과정에서 문제가 발생했습니다.");
        } catch (AmazonServiceException e) {
            throw new ServerException("s3 관련 문제");
        } catch (SdkClientException e) {
            throw new ServerException("s3 관련 문제");
        }

        return fileUrl;
    }

    public void deleteFile(String imgUrl) {
        if (imgUrl == null || imgUrl.isEmpty()) {
            return;
        }

        // ".com/"의 위치를 찾습니다.
        int index = imgUrl.indexOf(".com/");

        // ".com/" 이후의 문자열을 추출합니다.
        if (index != -1) {
            String fileName = imgUrl.substring(index + 5); // 5는 ".com/"의 길이
            amazonS3Client.deleteObject(bucket, fileName);
        }
    }
}
