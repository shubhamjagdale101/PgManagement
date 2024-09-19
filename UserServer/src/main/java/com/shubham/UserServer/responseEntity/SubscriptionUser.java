package com.shubham.UserServer.responseEntity;

import com.shubham.UserServer.model.UserStatus;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class SubscriptionUser {
    private String userName;
    private String firstName;
    private String lastName;
    private String phNo;
    private String panId;
    private String adharId;
    private String email;
    private UserStatus status;
}
