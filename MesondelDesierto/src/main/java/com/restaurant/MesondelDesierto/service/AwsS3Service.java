package com.restaurant.MesondelDesierto.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
@Slf4j
public class AwsS3Service {
    private final String bucketName = "meson-proyecto";

    @Value("${aws.s3.access}")
    private String awsS3AccessKey;
    @Value("${aws.s3.secrete}")
    private String awsS3SecreteKey;


    public String saveImageToS3(MultipartFile photo){
        try {
            String s3FileName = photo.getOriginalFilename();
            //crea credenciales AES usando la clave de acceso y la clave secreta
            BasicAWSCredentials awsCredentials = new BasicAWSCredentials(awsS3AccessKey, awsS3SecreteKey);

            //Crea un cliente S3 con credenciales configuradas y region
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                    .withRegion(Regions.US_EAST_2)
                    .build();

            //Obtener el flujo de entrada desde una foto
            InputStream inputStream = photo.getInputStream();

            //establecer metadatos para el objeto
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("image/jpeg");

            //crear una solicitud PUT para subir la imagen a s3
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, s3FileName, inputStream, metadata);
            s3Client.putObject(putObjectRequest);

            return "https://" + bucketName + ".s3.us-east-2.amazonaws.com/" + s3FileName;

        }catch (IOException e){
            e.printStackTrace();
            throw new RuntimeException("No se puede subir la imagen al bucket de S3: " + e.getMessage());
        }
    }
}
