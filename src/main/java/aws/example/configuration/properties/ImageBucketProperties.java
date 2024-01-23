package aws.example.configuration.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;

@Data
@ConfigurationProperties(prefix = "s3")
@Component
public class ImageBucketProperties {

    @NotBlank
    private String bucketName;

    @NotBlank
    private String region;

    @NotBlank
    private String accessKey;

    @NotBlank
    private String secretKey;
}
