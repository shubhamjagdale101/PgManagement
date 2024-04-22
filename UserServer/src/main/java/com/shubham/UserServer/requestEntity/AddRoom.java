package com.shubham.UserServer.requestEntity;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddRoom {
    @NotNull
    private Integer roomNo;

    @NotBlank
    private String location;

    @NotNull
    private Integer extraChargePerMonth;

    @NotNull
    private Integer availableSeats;
}
