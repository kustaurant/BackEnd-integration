//package com.kustaurant.kustaurant.post.post.service.api;
//
//import com.amazonaws.services.s3.AmazonS3Client;
//import com.amazonaws.services.s3.model.ObjectMetadata;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//
//@Service
//@RequiredArgsConstructor
//public class StorageApiService {
//    // 커뮤니티 이미지 저장 서비
//    @Value("${cloud.aws.s3.bucket}")
//    private String bucket;
//    @Autowired
//    private AmazonS3Client amazonS3Client;
//
//    // 아마존 s3에 이미지 저장하는 함수
//    public String storeImage(MultipartFile file) {
//        try {
//            String folderPath = "community/"; // 새 폴더 경로
//            String fileName = folderPath + file.getOriginalFilename();
//            String fileUrl = "https://" + bucket + ".s3.ap-northeast-2.amazonaws.com/" + fileName;
//            ObjectMetadata metadata = new ObjectMetadata();
//            metadata.setContentType(file.getContentType());
//            metadata.setContentLength(file.getSize());
//            // s3에 저장
//            amazonS3Client.putObject(bucket, fileName, file.getInputStream(), metadata);
//            return fileUrl;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return "이미지 저장 오류 발생";
//        }
//    }
//
//}
//
package com.kustaurant.kustaurant.post.post.service.api;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StorageApiService {

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    private final S3Client s3Client;

    public String storeImage(MultipartFile file) {
        try {
            String key = "community/" + UUID.randomUUID() + "_" + file.getOriginalFilename();

            PutObjectRequest putReq = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .build();

            s3Client.putObject(
                    putReq,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );

            String fileUrl = s3Client.utilities()
                    .getUrl(GetUrlRequest.builder().bucket(bucket).key(key).build())
                    .toString();

            return fileUrl;

        } catch (IOException e) {
            throw new RuntimeException("이미지 저장 오류", e);
        }
    }
}
