package aws.example.service;

import aws.example.configuration.properties.ImageBucketProperties;
import aws.example.exception.S3ObjectNotFoundException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class S3Service {

    private final ImageBucketProperties imageBucketProperties;
    private final AmazonS3 amazonS3;

    private final FileService fileService;

    @Autowired
    public S3Service(ImageBucketProperties imageBucketProperties,
                     AmazonS3 amazonS3,
                     FileService fileService) {
        this.imageBucketProperties = imageBucketProperties;
        this.amazonS3 = amazonS3;
        this.fileService = fileService;
    }

    public byte[] downloadObject(String objectName) {
        checkBucketExists();
        checkObjectExits(objectName);

        S3Object o = amazonS3.getObject(imageBucketProperties.getBucketName(), objectName);
        return fileService.readBitmap(o);
    }

    public void uploadObject(InputStream file, String filename, String customName) {
        checkBucketExists();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.addUserMetadata("Name", filename);
        metadata.setContentType("image/jpg");
        PutObjectRequest request = new PutObjectRequest(imageBucketProperties.getBucketName(), customName, file, metadata);
        request.setMetadata(metadata);
        amazonS3.putObject(request);
    }

    public void deleteObject(String objectName) {
        checkBucketExists();
        checkObjectExits(objectName);
        amazonS3.deleteObject(imageBucketProperties.getBucketName(), objectName);
    }

    private void checkBucketExists() {
        if (!amazonS3.doesBucketExistV2(imageBucketProperties.getBucketName())) {
            amazonS3.createBucket(imageBucketProperties.getBucketName());
        }
    }

    private void checkObjectExits(String objectName) {
        if (!amazonS3.doesObjectExist(imageBucketProperties.getBucketName(),objectName)) {
            throw new S3ObjectNotFoundException("Object not found in S3 bucket");
        }
    }
}
