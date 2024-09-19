package com.shubham.UserServer.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@Service
public class PublicService {
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private OtpStore otpStore;

    @Autowired
    private HttpServletRequest request;

    public ResponseEntity<String> sendOtpOnMail(String email) throws MessagingException {
        String otp = otpStore.generateAndStoreOtp(email);
        System.out.println(otp);

        String htmlContent = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>OTP Email</title>\n" +
                "    <style>\n" +
                "        body {\n" +
                "            font-family: Arial, sans-serif;\n" +
                "            background-color: #f5f5f5;\n" +
                "            margin: 0;\n" +
                "            padding: 0;\n" +
                "        }\n" +
                "\n" +
                "        .container {\n" +
                "            max-width: 600px;\n" +
                "            margin: 50px auto;\n" +
                "            background-color: #ffffff;\n" +
                "            border-radius: 10px;\n" +
                "            box-shadow: 0px 0px 20px rgba(0, 0, 0, 0.1);\n" +
                "            padding: 40px;\n" +
                "        }\n" +
                "\n" +
                "        h2 {\n" +
                "            text-align: center;\n" +
                "            color: #333333;\n" +
                "            margin-bottom: 20px;\n" +
                "            text-shadow: 1px 1px 1px rgba(0, 0, 0, 0.1);\n" +
                "        }\n" +
                "\n" +
                "        .otp-box {\n" +
                "            text-align: center;\n" +
                "            margin-top: 20px;\n" +
                "        }\n" +
                "\n" +
                "        .otp {\n" +
                "            display: inline-block;\n" +
                "            padding: 15px 30px;\n" +
                "            background-color: #ff6b6b;\n" +
                "            color: #ffffff;\n" +
                "            font-size: 28px;\n" +
                "            border-radius: 5px;\n" +
                "            box-shadow: 0px 4px 10px rgba(255, 107, 107, 0.3);\n" +
                "            text-shadow: 1px 1px 1px rgba(0, 0, 0, 0.2);\n" +
                "            transition: all 0.3s ease-in-out;\n" +
                "        }\n" +
                "\n" +
                "        .otp:hover {\n" +
                "            transform: translateY(-3px);\n" +
                "            box-shadow: 0px 6px 15px rgba(255, 107, 107, 0.5);\n" +
                "        }\n" +
                "\n" +
                "        .info-text {\n" +
                "            text-align: center;\n" +
                "            font-size: 16px;\n" +
                "            color: #666666;\n" +
                "            margin-top: 20px;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "<div class=\"container\">\n" +
                "    <h2>OTP Verification</h2>\n" +
                "    <div class=\"otp-box\">\n" +
                "        <span class=\"otp\">+" + String.valueOf(otp) +
                "</span>\n" +
                "    </div>\n" +
                "    <p class=\"info-text\">This OTP is valid for a single use and should not be shared with anyone.</p>\n" +
                "    <p class=\"info-text\">If you did not request this OTP, please ignore this email.</p>\n" +
                "</div>\n" +
                "</body>\n" +
                "</html>";

        // Create a SimpleMailMessage with HTML content
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom("shubhamjagdalerxl@gmail.com");
        helper.setTo(email);
        helper.setSubject("Account Created Successfully");
        helper.setText(htmlContent, true); // Set HTML content to true

//        mailSender.send(message);
        return new ResponseEntity<>("otp sent to email", HttpStatus.OK);
    }

    public ResponseEntity<String> matchOtp(String key, String otp){
        Boolean res = otpStore.compareOtp(key, otp);
        String resMsg = (res? "correct otp" : "incorrect otp");

        return new ResponseEntity<>(resMsg, HttpStatus.OK);
    }
}
