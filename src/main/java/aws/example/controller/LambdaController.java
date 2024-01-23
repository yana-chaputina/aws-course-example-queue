package aws.example.controller;

import aws.example.exception.LambdaException;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.model.InvokeRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/lambda")
public class LambdaController {

    private final AWSLambda awsLambda;

    @Value("${amazon.lambda.function.arn}")
    private String functionArn;

    @Autowired
    public LambdaController(AWSLambda awsLambda) {
        this.awsLambda = awsLambda;
    }

    @GetMapping()
    public void triggerLambda() {
        try {
            InvokeRequest invokeRequest= new InvokeRequest()
                    .withFunctionName(functionArn)
                    .withPayload("{\"detail-type\":\"web application\"}");
            awsLambda.invoke(invokeRequest);
        } catch (Exception e)
        {
            throw new LambdaException("Can not trigger lambda: "+e.getMessage());
        }
    }
}
