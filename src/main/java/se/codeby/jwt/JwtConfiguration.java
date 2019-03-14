package se.codeby.jwt;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtConfiguration {

    @Size(min = 32)
    @JsonProperty("key")
    public String key;
    @JsonProperty("allowedClockSkewInSeconds")
    public int allowedClockSkewInSeconds = 300;
}
