package se.codeby.jwt;

import com.google.common.collect.ImmutableList;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.security.Principal;

@Builder
@ToString
@EqualsAndHashCode
public class JwtUser implements Principal {
    public final String id;
    public final String name;
    public final ImmutableList<String> roles;

    public String getName() {
        return name;
    }
}
