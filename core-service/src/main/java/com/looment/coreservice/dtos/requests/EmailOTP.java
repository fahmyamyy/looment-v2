package com.looment.coreservice.dtos.requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmailOTP implements Serializable {
    @JsonProperty("email")
    private String email;
    @JsonProperty("username")
    private String username;
    @JsonProperty("otp")
    private String otp;

    @Override
    public String toString() {
        return "{" +
                "\"email\": \"" + email + "\"," +
                "\"username\": \"" + username + "\"," +
                "\"otp\": \"" + otp + "\"" +
                "}";
    }

}
