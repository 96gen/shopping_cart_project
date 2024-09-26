package com.shopping_cart_project.shopping_cart_project.Service;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender javaMailSender;

    public EmailService(JavaMailSender javaMailSender){
        this.javaMailSender = javaMailSender;
    }

    //處理送出Email的細節
    public void sendSimpleEmail(String to) {
        Dotenv dotenv = Dotenv.load();

        //編寫郵件內容
        SimpleMailMessage message = new SimpleMailMessage();
        //寄到哪邊
        message.setTo(to);
        //Email主旨
        message.setSubject("Welcome to Shopping Cart, " + to + " !");
        //Email內容
        message.setText("Thank you for choosing Shopping Cart. We look forward to serving you and making your shopping experience enjoyable.\n" +
                "Best regards,\n" +
                "The Shopping Cart Team");
        //寄件人是誰
        message.setFrom(dotenv.get("GMAIL_ADDRESS"));
        //送出Email
        javaMailSender.send(message);
    }
}
