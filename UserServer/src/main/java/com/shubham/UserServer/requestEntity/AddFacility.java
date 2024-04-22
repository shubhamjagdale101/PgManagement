package com.shubham.UserServer.requestEntity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.apache.kafka.common.protocol.types.Field;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddFacility {
    @NotBlank(message = "facility name is required")
    private String name;

    @NotNull(message = "charges for facility should not null")
    private Integer perMonthCharge;
}
