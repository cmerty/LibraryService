package com.example.demo.service.MailSender;


import com.example.demo.model.Mail;
import com.example.demo.service.Interfaces.SimpleMessageInterface;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class MailServiceImpl implements SimpleMessageInterface {

    private final JavaMailSender emailSender;

    private final Configuration freemarkerConfig;

    public MailServiceImpl(JavaMailSender emailSender, Configuration freemarkerConfig) {
        this.emailSender = emailSender;
        this.freemarkerConfig = freemarkerConfig;
    }

    @Override
    public void sendSimpleMessage(Mail mail) throws MessagingException, IOException, TemplateException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());

        Template t = freemarkerConfig.getTemplate("email-template.html");
        String html = FreeMarkerTemplateUtils.processTemplateIntoString(t, mail.getModel());


        helper.setTo(mail.getTo());
        helper.setText(html, true);
        helper.setSubject(mail.getSubject());
        helper.setFrom(mail.getFrom());

        emailSender.send(message);
    }
}
