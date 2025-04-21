package com.farmdora.farmdorabuyer.orders.service;

import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
@Slf4j
public class NcpImageService {

    private final AmazonS3 s3Client;
    private final String bucketName;
    private final String cdnDomain;

    // 이미지 최적화 기본 옵션
    private final String defaultOptions = "type=f_auto&quality=90&autorotate=true";

    public NcpImageService(
            @Value("${ncp.object-storage.endpoint}") String endpoint,
            @Value("${ncp.object-storage.region}") String region,
            @Value("${ncp.object-storage.bucket}") String bucketName,
            @Value("${ncp.image-optimizer.cdn-domain}") String cdnDomain,
            @Value("${ncp.access-key}") String accessKey,
            @Value("${ncp.secret-key}") String secretKey) {

        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);

        this.s3Client = AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint, region))
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();

        this.bucketName = bucketName;
        this.cdnDomain = cdnDomain;

    }

    public String uploadImage(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어있습니다.");
        }

        // 원본 파일명 가져오기
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new IllegalArgumentException("파일명이 없습니다.");
        }

        // 저장할 파일명 생성 (UUID + 확장자)
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String savedFilename = UUID.randomUUID() + fileExtension;

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        try {
            s3Client.putObject(new PutObjectRequest(
                    bucketName,
                    savedFilename,
                    file.getInputStream(),
                    metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead)); // 파일을 공개로 설정

            return savedFilename;

        } catch (Exception e) {
            throw new IOException("NCP Object Storage 업로드 실패: " + e.getMessage(), e);
        }
    }

    /**
     * 파일명을 받아 전체 URL을 생성합니다.
     */
    public String getObjectUrl(String filename) {
        return s3Client.getUrl(bucketName, filename).toString();
    }

    /**
     * 파일명을 받아 Image Optimizer URL을 생성합니다.
     */
//    public String getImageOptimizerUrl(String filename) {
//        String objectUrl = getObjectUrl(filename);
//        return generateImageOptimizerUrl(objectUrl);
//    }

    /**
     * 외부 URL을 받아 Image Optimizer URL을 생성합니다.
     */
    public String generateImageOptimizerUrl(String externalUrl) {
        // URL 인코딩
        String encodedUrl = UriUtils.encode(externalUrl, StandardCharsets.UTF_8);

        // CDN URL 형식으로 구성
        return String.format("https://%s?src=\"%s\"&%s", cdnDomain, encodedUrl, defaultOptions);
    }

//    public String uploadObject(String objectName, InputStream inputStream) {
//        try {
//            // 객체 메타데이터 설정
//            ObjectMetadata objectMetadata = new ObjectMetadata();
//
//            // 파일 업로드
//            s3Client.putObject(bucketName, objectName, inputStream, objectMetadata);
//
//            // 업로드된 객체의 URL 반환
//            return getObjectUrl(objectName);
//        } catch (SdkClientException e) {
//            log.error("NCP 스토리지 업로드 오류: " + e.getMessage(), e);
//            throw new RuntimeException("파일 업로드 중 오류가 발생했습니다.", e);
//        }
//    }

    public boolean deleteObject(String objectName) {
        try {
            s3Client.deleteObject(new DeleteObjectRequest(bucketName, objectName));
            log.info("ncp 스토리지에서 삭제 성공 : {}", objectName);
            return true;
        } catch (SdkClientException e) {
            throw new RuntimeException("파일 삭제 중 오류", e);
        }
    }
}