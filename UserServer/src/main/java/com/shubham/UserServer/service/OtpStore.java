package com.shubham.UserServer.service;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class OtpStore implements DisposableBean {
    private final Map<String, String> otpMap;
    private final ScheduledExecutorService scheduler;

    public OtpStore() {
        this.otpMap = new HashMap<>();
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
        return storedOtp != null && storedOtp.equals(providedOtp);
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
