package com.farmdora.farmdorabuyer.orders.service;

import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.farmdora.farmdorabuyer.common.exception.FileException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

@Service
@Slf4j
public class NCPObjectStorageService implements StorageService{

    private final AmazonS3 s3;
    private final String bucketName;
    private final String cdnDomain;
    private final String projectId;

    private final String defaultOptions = "?type=h&h=192&ttype=png";
//    private final String defaultOptions = "?type=f_auto&quality=90&ttype=png";

    public NCPObjectStorageService(
            @Value("${ncp.object-storage.endpoint}") String endpoint,
            @Value("${ncp.object-storage.region}") String region,
            @Value("${ncp.object-storage.bucket}") String bucketName,
            @Value("${ncp.image-optimizer.cdn-domain}") String cdnDomain,
            @Value("${ncp.image-optimizer.project-id}") String projectId,
            @Value("${ncp.access-key}") String accessKey,
            @Value("${ncp.secret-key}") String secretKey) {

        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);

        this.s3 = AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint, region))
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();

        this.bucketName = bucketName;
        this.cdnDomain = cdnDomain;
        this.projectId = projectId;

    }

    @Override
    public void upload(String filePath, InputStream fileIn) {

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType("application/x-directory");

        PutObjectRequest putObjectRequest = new PutObjectRequest(
                bucketName, // 버킷 이름
                filePath, // 업로드 파일의 이름 및 디렉토리 경로
                fileIn, // 업로드 할 파일의 InputStream
                objectMetadata // 업로드에 필요한 부가 정보
        );

        try {
            s3.putObject(putObjectRequest);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void download(String filePath, OutputStream fileOut) {
        try {
            S3Object s3Object = s3.getObject(bucketName, filePath);
            S3ObjectInputStream s3ObjectInputStream = s3Object.getObjectContent();


            byte[] bytesArray = new byte[4096];
            int bytesRead = -1;
            while ((bytesRead = s3ObjectInputStream.read(bytesArray)) != -1) {
                fileOut.write(bytesArray, 0, bytesRead);
            }
            s3ObjectInputStream.close();
            fileOut.close();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String uploadImage(MultipartFile file, String folderPath) throws IOException {
        String originalFilename = file.getOriginalFilename();

        // 저장할 파일명 생성 (UUID + 확장자)
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String savedFilename = UUID.randomUUID() + fileExtension;

        // 폴더 경로가 있으면 추가
        String fullPath = savedFilename;
        if (folderPath != null && !folderPath.isEmpty()) {
            // 폴더 경로 끝에 슬래시가 있으면 제거
            if (folderPath.endsWith("/")) {
                folderPath = folderPath.substring(0, folderPath.length() - 1);
            }
            fullPath = folderPath + "/" + savedFilename;
        }

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        return uploadToS3(file.getInputStream(), fullPath, metadata);
    }

    private String uploadToS3(InputStream inputStream, String savedFilename, ObjectMetadata metadata) throws IOException {
        try {
            log.info("파일 업로드 시도: {}", savedFilename);

            s3.putObject(new PutObjectRequest(
                    bucketName,
                    savedFilename,
                    inputStream,
                    metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead)); // 파일을 공개로 설정

            log.info("파일 업로드 성공: {}", savedFilename);
            return savedFilename;

        } catch (Exception e) {
            log.error("파일 업로드 실패: {}", savedFilename, e);
            throw new IOException("NCP Object Storage 업로드 실패: " + e.getMessage(), e);
        }
    }

    public String getObjectStorageImageUrl(String objectName) {
        // 실제 이미지 URL 생성 // ImageOptimizer 사용.
        return String.format("%s%s%s%s", "https://" + cdnDomain + "/", projectId + "/", objectName, defaultOptions);
    }

    public void delete(String filePath) {
        try {
            s3.deleteObject(bucketName, filePath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}