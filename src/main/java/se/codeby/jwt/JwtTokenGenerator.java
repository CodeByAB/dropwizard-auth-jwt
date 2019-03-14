package se.codeby.jwt;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringUtils;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.keys.HmacKey;
import org.jose4j.lang.JoseException;

import static org.jose4j.jws.AlgorithmIdentifiers.HMAC_SHA256;

public class JwtTokenGenerator {

    private final JwtConfiguration configuration;

    public JwtTokenGenerator(JwtConfiguration configuration) {
        this.configuration = configuration;
    }

    public String generate(String id, String name, ImmutableList<String> roles) {
        try {
            final JwtClaims claims = new JwtClaims();
            claims.setSubject(id);
            claims.setStringClaim("roles", StringUtils.join(roles, ","));
            claims.setStringClaim("user", name);
            claims.setIssuedAtToNow();
            claims.setGeneratedJwtId();

            final JsonWebSignature jws = new JsonWebSignature();
            jws.setPayload(claims.toJson());
            jws.setAlgorithmHeaderValue(HMAC_SHA256);
            jws.setKey(new HmacKey(configuration.key.getBytes()));

            return jws.getCompactSerialization();
        } catch (JoseException e) {
            throw new RuntimeException(e);
        }
    }
}
