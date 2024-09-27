package com.shopping_cart_project.shopping_cart_project.Service;

import com.shopping_cart_project.shopping_cart_project.Config.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class EmailConsumer {
    private final EmailService emailService;

    public EmailConsumer(EmailService emailService){
        this.emailService = emailService;
    }

    //當指定的Queue接收到訊息時，會自動執行裡面的內容
    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void receiveMessage(String to) {
        //發送郵件
        emailService.sendSimpleEmail(to);
    }
}
