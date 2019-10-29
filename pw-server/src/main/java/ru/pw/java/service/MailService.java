package ru.pw.java.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import ru.pw.java.model.enums.NotificationMessage;
import ru.pw.java.tables.pojos.Users;
import ru.pw.java.tables.pojos.WordGroup;

/**
 * @author Lev_S
 */

@Service
public class MailService {

    @Autowired
    private JavaMailSender jms;

    public void sendRegisteredEmail(Users users) {
        String email = users.getUserEmail();
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("8.888.888evs@gmail.com");
        message.setText("Thank you for registration in our app, it is your ref to confirm your email: " + email);
        message.setSubject("Registration in PW");
        message.setTo(email);

        jms.send(message);
    }

    public void sendNotificationMessage(Users user, WordGroup wordGroup) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("8.888.888evs@gmail.com");
        message.setText(
                String.format(NotificationMessage.MAIL.getMessage(), wordGroup.getName())
        );
        message.setSubject("REPEAT YOUR WORDS " + wordGroup.getName());
        message.setTo(user.getUserEmail());

        jms.send(message);
    }

}
