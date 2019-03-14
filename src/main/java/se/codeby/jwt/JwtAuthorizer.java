package se.codeby.jwt;

import io.dropwizard.auth.Authorizer;

class JwtAuthorizer implements Authorizer<JwtUser> {

    @Override
    public boolean authorize(JwtUser user, String requiredRole) {
        if (user == null || user.roles == null) {
            return false;
        }
        return user.roles.contains(requiredRole);
    }
}
