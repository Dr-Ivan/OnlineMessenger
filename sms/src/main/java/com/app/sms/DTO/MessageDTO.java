package com.app.sms.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MessageDTO {
    private Long id;

    @NotNull
    @NotBlank
    @NotEmpty
    private String fromUserName;

    @NotNull
    @NotBlank
    @NotEmpty
    private String fromUserPasswordHash;

    @NotNull
    @NotBlank
    @NotEmpty
    private String toUserName;

    @PastOrPresent
    private LocalDateTime time;


    private String content;
}
