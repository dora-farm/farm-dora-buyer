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
import com.farmdora.farmdorabuyer.common.exception.FileException;
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
        String originalFilename = file.getOriginalFilename();

        // 저장할 파일명 생성 (UUID + 확장자)
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String savedFilename = UUID.randomUUID() + fileExtension;
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        return uploadToS3(file.getInputStream(), savedFilename, metadata);
    }

    private String uploadToS3(InputStream inputStream, String savedFilename, ObjectMetadata metadata) throws IOException {
        try {
            s3Client.putObject(new PutObjectRequest(
                bucketName,
                savedFilename,
                inputStream,
                metadata)
                .withCannedAcl(CannedAccessControlList.PublicRead)); // 파일을 공개로 설정

            return savedFilename;

        } catch (Exception e) {
            throw new IOException("NCP Object Storage 업로드 실패: " + e.getMessage(), e);
        }
    }

    // url 생성
    public String getObjectUrl(String filename) {
        return s3Client.getUrl(bucketName, filename).toString();
    }

    public void deleteObjectToNCP(String objectName) {
        try {
            s3Client.deleteObject(new DeleteObjectRequest(bucketName, objectName));
            log.info("ncp 스토리지에서 삭제 성공 : {}", objectName);
        } catch (SdkClientException e) {
            throw new RuntimeException("파일 삭제 중 오류", e);
        }
    }
}