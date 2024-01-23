package aws.example.controller;

import aws.example.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/subscriptions/{email}")
    public void subscribeEmail(@PathVariable String email) {
        notificationService.subscribeEmail(email);
    }

    @DeleteMapping("/subscriptions/{email}")
    public void unsubscribeEmail(@PathVariable String email) {
        notificationService.unsubscribeEmail(email);
    }
}
