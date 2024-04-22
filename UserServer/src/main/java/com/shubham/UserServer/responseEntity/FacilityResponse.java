package com.shubham.UserServer.responseEntity;

import lombok.*;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FacilityResponse {
    private String name;
    private Integer perMonthCharge;
}
