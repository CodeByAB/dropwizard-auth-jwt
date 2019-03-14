package se.codeby.jwt;

import com.google.common.collect.ImmutableList;
import io.dropwizard.auth.Authenticator;
import lombok.extern.log4j.Log4j;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.JwtContext;

import java.util.Arrays;
import java.util.Optional;

@Log4j
class JwtAuthenticator implements Authenticator<JwtContext, JwtUser> {

    @Override
    public Optional<JwtUser> authenticate(JwtContext context) {
        try {
            JwtClaims claims = context.getJwtClaims();
            return Optional.of(JwtUser.builder()
                .id(claims.getSubject())
                .name((String) claims.getClaimValue("user"))
                .roles(ImmutableList.copyOf(Arrays.asList(String.valueOf(claims.getClaimValue("roles")).split(",")))).build());
        } catch (Exception e) {
            log.warn("Authorization failed: " + e.getMessage(), e);
            return Optional.empty();
        }
    }
}
