package com.iss.renterscore.authentication.payloads;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class VerifyTokenRequest {

    @NotNull(message = "Login Email can be null but not blank")
    private String email;

    @NotNull(message = "Verify token can be null but not blank")
    private String token;
}
