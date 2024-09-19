package com.shubham.UserServer.service;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class OtpStore implements DisposableBean {
    private final Map<String, String> otpMap;
    private final Set<String> verified;
    private final ScheduledExecutorService scheduler;

    public OtpStore() {
        this.otpMap = new HashMap<>();
        this.verified = new HashSet<>();
        this.scheduler = Executors.newScheduledThreadPool(1);
    }

    public String generateAndStoreOtp(String key) {
        String otp = generateOtp();
        otpMap.put(key, otp);

        scheduler.schedule(() -> otpMap.remove(key), 120, TimeUnit.SECONDS);
        return otp;
    }

    public String getOtp(String key) {
        return otpMap.get(key);
    }

    public boolean compareOtp(String key, String providedOtp) {
        String storedOtp = otpMap.get(key);
        boolean res = storedOtp != null && storedOtp.equals(providedOtp);
        if(res) verified.add(key);
        return res;
    }

    public boolean isVerified(String key){
        return verified.contains(key);
    }

    public void clearVerified(String key){
        verified.remove(key);
    }

    private String generateOtp() {
        Random random = new Random();
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }

    @Override
    public void destroy() throws Exception {
        scheduler.shutdown();
    }
}
