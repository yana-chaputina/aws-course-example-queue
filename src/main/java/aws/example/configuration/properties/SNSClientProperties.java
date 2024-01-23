package aws.example.configuration.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;

@Data
@ConfigurationProperties(prefix = "sns")
@Component
public class SNSClientProperties {

    @NotBlank
    private String region;

    @NotBlank
    private String topicArn;
}
