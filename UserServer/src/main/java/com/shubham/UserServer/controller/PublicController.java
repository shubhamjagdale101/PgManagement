package com.shubham.UserServer.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.shubham.UserServer.constants.RoleConstants;
import com.shubham.UserServer.model.User;
import com.shubham.UserServer.requestEntity.SignUp;
import com.shubham.UserServer.responseEntity.SubscriptionUser;
import com.shubham.UserServer.service.PublicService;
import com.shubham.UserServer.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;

@RestController
public class PublicController {
    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PublicService publicService;

    @PostMapping("/signup")
    public SubscriptionUser handleSignUp(@Valid @RequestBody SignUp req) throws Exception {
        User user = userService.handleSignUp(req, RoleConstants.USER);
        return SubscriptionUser.builder()
                .panId(user.getPanId())
                .phNo(user.getPhNo())
                .userName(user.getUsername())
                .lastName(user.getLastName())
                .firstName(user.getFirstName())
                .adharId(user.getAdharId())
                .email(user.getEmail())
                .status(user.getStatus())
                .build();
    }

    @GetMapping("/sedOtpOnMAil")
    public ResponseEntity<String> sendOtpOnMail(@RequestParam("email") String email) throws MessagingException {
        return publicService.sendOtpOnMail(email);
    }

    @GetMapping("/matchOtp")
    public ResponseEntity<String> matchOtp(@RequestParam("key") String key, @RequestParam("otp") String otp){
        return publicService.matchOtp(key, otp);
    }
}
