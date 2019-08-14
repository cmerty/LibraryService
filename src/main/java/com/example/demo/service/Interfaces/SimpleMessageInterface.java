package com.example.demo.service.Interfaces;

import com.example.demo.model.Mail;
import freemarker.template.TemplateException;

import javax.mail.MessagingException;
import java.io.IOException;

public interface SimpleMessageInterface {

    void sendSimpleMessage(Mail mail) throws MessagingException, IOException, TemplateException;
}
