package aws.example.service;

import aws.example.configuration.properties.SNSClientProperties;
import aws.example.configuration.properties.SQSClientProperties;
import aws.example.exception.SNSException;
import aws.example.exception.SQSException;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.*;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    private static final String SNS_PROTOCOL = "email";

    private final SNSClientProperties snsClientProperties;
    private final SQSClientProperties sqsClientProperties;
    private final AmazonSNS snsClient;
    private final AmazonSQS sqsClient;

    @Autowired
    public NotificationService(SNSClientProperties snsClientProperties,
                               SQSClientProperties sqsClientProperties,
                               AmazonSNS snsClient,
                               AmazonSQS sqsClient) {
        this.snsClientProperties = snsClientProperties;
        this.sqsClientProperties = sqsClientProperties;
        this.snsClient = snsClient;
        this.sqsClient = sqsClient;
    }

    public void subscribeEmail(String email) {
        try {
            SubscribeRequest request = new SubscribeRequest()
                    .withProtocol(SNS_PROTOCOL)
                    .withEndpoint(email)
                    .withTopicArn(snsClientProperties.getTopicArn());
            snsClient.subscribe(request);
        } catch (AmazonSNSException e) {
            throw new SNSException("Can not subscribe email: "+e.getMessage());
        }
    }

    public void unsubscribeEmail(String email) {
        try {
            ListSubscriptionsByTopicResult listResult = snsClient.listSubscriptionsByTopic(snsClientProperties.getTopicArn());
            List<Subscription> subscriptions = listResult.getSubscriptions();
            subscriptions.stream()
                    .filter(subscription -> email.equals(subscription.getEndpoint()))
                    .findAny()
                    .ifPresent(subscription -> unsubscribe(subscription.getSubscriptionArn()));
        } catch (AmazonSNSException e) {
            throw new SNSException("Can not unsubscribe email: "+e.getMessage());
        }
    }

    public void sendMessageToQueue(String message) {
        try {
            SendMessageRequest request = new SendMessageRequest()
                    .withQueueUrl(sqsClientProperties.getQueueUrl())
                    .withMessageBody(message)
                    .withDelaySeconds(5);
            sqsClient.sendMessage(request);
        } catch (AmazonSQSException e) {
            throw new SQSException("Can not send message to queue: "+ e.getMessage());
        }
    }

    public void sendMessageToTopic(String message) {
        try {
            PublishRequest publishRequest = new PublishRequest()
                    .withMessage(message)
                    .withTopicArn(snsClientProperties.getTopicArn());
            snsClient.publish(publishRequest);
        } catch (AmazonSNSException e) {
            throw new SQSException("Can not send message to topic: "+ e.getMessage());
        }
    }

    public List<Message> readMessages() {
        try {
            String queueUrl = sqsClientProperties.getQueueUrl();
            ReceiveMessageRequest request = new ReceiveMessageRequest()
                    .withQueueUrl(queueUrl)
                    .withWaitTimeSeconds(10)
                    .withMaxNumberOfMessages(10);
            List<Message> messages = sqsClient.receiveMessage(request).getMessages();
            messages.stream()
                    .map(Message::getReceiptHandle)
                    .forEach(receipt -> sqsClient.deleteMessage(queueUrl, receipt));
            return messages;
        } catch (AmazonSQSException e) {
            throw new SQSException("Can not read message from queue: "+ e.getMessage());
        }
    }

    private void unsubscribe(String subscriptionArn) {
        try {
           UnsubscribeRequest unsubscribeRequest = new UnsubscribeRequest()
                    .withSubscriptionArn(subscriptionArn);
            snsClient.unsubscribe(unsubscribeRequest);
        } catch (AmazonSNSException e) {
            throw new SNSException("Can not unsubscribe email: "+e.getMessage());
        }
    }
}

