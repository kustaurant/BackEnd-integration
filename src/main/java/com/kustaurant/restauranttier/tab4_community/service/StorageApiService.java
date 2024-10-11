package com.kustaurant.restauranttier.tab4_community.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.kustaurant.restauranttier.tab4_community.entity.Post;
import com.kustaurant.restauranttier.tab4_community.entity.PostPhoto;
import com.kustaurant.restauranttier.tab4_community.repository.PostPhotoApiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class StorageApiService {
    // 커뮤니티 이미지 저장 서비
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    @Autowired
    private AmazonS3Client amazonS3Client;

    private final PostPhotoApiRepository postPhotoApiRepository;
    // 아마존 s3에 이미지 저장하는 함수
    public String storeImage(MultipartFile file) throws IOException {
        try {
            String folderPath = "community/"; // 새 폴더 경로
            String fileName = folderPath + file.getOriginalFilename();
            String fileUrl = "https://" + bucket + ".s3.ap-northeast-2.amazonaws.com/" + fileName;
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());
            // s3에 저장
            amazonS3Client.putObject(bucket, fileName, file.getInputStream(), metadata);
            return fileUrl;
        } catch (IOException e) {
            e.printStackTrace();
            return "이미지 저장 오류 발생";
        }
    }
    public void handleImageUpload(Post post, String imageUrl) throws IOException {
        if (imageUrl != null) {
            PostPhoto postPhoto = new PostPhoto(imageUrl, "ACTIVE");
            postPhoto.setPost(post);
            post.getPostPhotoList().clear();  // 기존 이미지를 제거하고 새로운 이미지를 추가
            post.getPostPhotoList().add(postPhoto);
            postPhotoApiRepository.save(postPhoto);
        }
    }
}

