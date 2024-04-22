package com.shubham.UserServer.responseEntity;

import lombok.*;
import org.apache.kafka.common.protocol.types.Field;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Response {
    private Boolean error;
    private String message;
}
