package aws.example.configuration;

import aws.example.configuration.properties.ImageBucketProperties;
import aws.example.configuration.properties.SNSClientProperties;
import aws.example.configuration.properties.SQSClientProperties;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class AWSConfiguration {

    private final ImageBucketProperties imageBucketProperties;
    private final SNSClientProperties snsClientProperties;
    private final SQSClientProperties sqsClientProperties;

    @Autowired
    public AWSConfiguration(ImageBucketProperties imageBucketProperties, SNSClientProperties snsClientProperties, SQSClientProperties sqsClientProperties) {
        this.imageBucketProperties = imageBucketProperties;
        this.snsClientProperties = snsClientProperties;
        this.sqsClientProperties = sqsClientProperties;
    }

    @Bean
    public AmazonS3 amazonS3() {
        AWSCredentials credentials = getCredentials();
        return AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(imageBucketProperties.getRegion())
                .build();
    }

    @Bean
    public AmazonSNS snsClient(){
        AWSCredentials credentials = getCredentials();
        return AmazonSNSClient
                .builder()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(snsClientProperties.getRegion())
                .build();
    }

    @Bean
    public AmazonSQS sqsClient(){
        AWSCredentials credentials = getCredentials();
        return AmazonSQSClient
                .builder()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(sqsClientProperties.getRegion())
                .build();
    }

    @Bean
    public AWSLambda awsLambda(){
        AWSCredentials credentials = getCredentials();
        return AWSLambdaClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(sqsClientProperties.getRegion()).build();
    }

    private AWSCredentials getCredentials(){
        return new BasicAWSCredentials(imageBucketProperties.getAccessKey(), imageBucketProperties.getSecretKey());
    }
}
