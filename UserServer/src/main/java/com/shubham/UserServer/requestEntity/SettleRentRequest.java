package com.shubham.UserServer.requestEntity;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SettleRentRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String monthYear;
}
