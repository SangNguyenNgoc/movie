package com.example.movieofficial.utils.services;

import com.example.movieofficial.utils.exceptions.AppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Log4j2
public class S3Service {

    @Value("${s3client.endpoint_url}")
    private String endpointUrl;
    @Value("${s3client.bucket_name}")
    private String bucketName;

    private final S3Client s3Client;

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }
    private String getFileExtension(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null && originalFilename.contains(".")) {
            return originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        } else {
            return "";
        }
    }


    private String generateFileName(String slug, String folder, String extension) {
        return String.format("%s/%d-%s.%s", folder, System.currentTimeMillis(), slug, extension);
    }

    private void uploadFileTos3bucket(String fileName, File file) throws S3Exception {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .acl("public-read")
                .build();
        PutObjectResponse response = s3Client.putObject(putObjectRequest, RequestBody.fromFile(file));
        log.info("File uploaded successfully. ETag: {}", response.eTag());
    }

    public String uploadFile(MultipartFile multipartFile, String slug, String folder) {
        String fileUrl = "";
        try {
            File file = convertMultiPartToFile(multipartFile);
            String extension = getFileExtension(multipartFile);
            String fileName = generateFileName(slug, folder, extension);
            fileUrl = endpointUrl.replace("//", "//" + bucketName + ".") + "/" + fileName;
            uploadFileTos3bucket(fileName, file);
            file.delete();
        } catch (Exception e) {
            log.error("Uploading image is failure");
            throw new AppException("Server error", HttpStatus.INTERNAL_SERVER_ERROR, List.of(e.getMessage()));
        }
        return fileUrl;
    }


}
