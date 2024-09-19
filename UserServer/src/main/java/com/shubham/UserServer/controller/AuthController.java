package com.shubham.UserServer.controller;

import com.shubham.UserServer.constants.RoleConstants;
import com.shubham.UserServer.service.PublicService;
import com.shubham.UserServer.service.TokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

@RestController
public class AuthController {
    @Autowired
    private TokenService tokenService;
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Value("${github.client-id}")
    private String clientId;

    @Value("${github.client-secret}")
    private String clientSecret;
    private final String tokenEndpoint = "https://github.com/login/oauth/access_token";
    private final String redirectUri = "http://localhost:8080/oauth2/callback";
    private final String userEndpoint = "https://api.github.com/user";

    @GetMapping("/oauth2/callback")
    public ModelAndView handleOAuth2Callback(@RequestParam("code") String code, HttpServletResponse httpResponse) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");

        // Create a Map to hold the request parameters
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("code", code);
        body.add("redirect_uri", redirectUri);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(tokenEndpoint, HttpMethod.POST, requestEntity, String.class);
        JSONObject object = new JSONObject(response.getBody());
        String accessToken = object.getString("access_token");

        HttpHeaders userHeaders = new HttpHeaders();
        userHeaders.set("Authorization", "Bearer " + accessToken);
        userHeaders.set("Accept", "application/json");

        HttpEntity<String> userRequestEntity = new HttpEntity<>(userHeaders);
        ResponseEntity<String> userResponse = restTemplate.exchange(userEndpoint, HttpMethod.GET, userRequestEntity, String.class);
        String userResponseBody = userResponse.getBody();

        // Parse user information
        JSONObject userJsonObject = new JSONObject(userResponseBody);
        String id = userJsonObject.optString("id");
        String email = userJsonObject.optString("email");
        String name = userJsonObject.optString("name");

        UserDetails userDetails = new User(id, "", Collections.singletonList(new SimpleGrantedAuthority(RoleConstants.USER)));
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        String token = tokenService.generateToken(authentication);
        Cookie cookie = new Cookie("Bearer-token", token);
        httpResponse.addCookie(cookie);
        return new ModelAndView("redirect:" + "http://localhost:3000/profile");
    }

    @GetMapping("/token")
    public String getToken(@RequestHeader("username") String username, @RequestHeader("password") String password, HttpServletResponse response){
        Authentication authentication = authenticateUser(username, password);
        String token = tokenService.generateToken(authentication);
        Cookie cookie = new Cookie("Bearer-token", token);
        response.addCookie(cookie);

        return token;
    }

    private Authentication authenticateUser(String username, String password) {
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);
        return authenticationManager.authenticate(authRequest);
    }

    @GetMapping("/oauth/github")
    public ModelAndView oauthByGithub(){
        String authorizationUri = UriComponentsBuilder.fromHttpUrl("https://github.com/login/oauth/authorize")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("scope", "user")
                .queryParam("state", generateState())
                .toUriString();

        return new ModelAndView("redirect:" + authorizationUri);
    }

    private String generateState() {
        return UUID.randomUUID().toString();
    }
}
