package com.shubham.UserServer.requestEntity;

import com.shubham.UserServer.model.MessageType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MessageRequest {
    private String sender;
    @NotBlank
    private String Message;
    @NotNull
    private MessageType type;
    private String Receiver;
}
