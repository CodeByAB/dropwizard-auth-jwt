package se.codeby.jwt;

import com.github.toastshaman.dropwizard.auth.jwt.JwtAuthFilter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.auth.PolymorphicAuthDynamicFeature;
import io.dropwizard.auth.PolymorphicAuthValueFactoryProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.keys.HmacKey;

public class JwtBundle implements ConfiguredBundle<JwtConfig> {

    public void initialize(Bootstrap<?> bootstrap) {
        // empty by design
    }

    @Override
    public void run(JwtConfig configuration, Environment environment) {

        final PolymorphicAuthDynamicFeature feature = new PolymorphicAuthDynamicFeature<>(
            ImmutableMap.of(JwtUser.class, new JwtAuthFilter.Builder<JwtUser>()
                .setJwtConsumer(new JwtConsumerBuilder().setAllowedClockSkewInSeconds(configuration.getJwtConfiguration().allowedClockSkewInSeconds)
                    .setRequireSubject()
                    .setVerificationKey(new HmacKey(configuration.getJwtConfiguration().key.getBytes())).build()).setRealm("realm").setPrefix("Bearer")
                .setAuthenticator(new JwtAuthenticator()).setAuthorizer(new JwtAuthorizer()).buildAuthFilter()));
        final AbstractBinder binder = new PolymorphicAuthValueFactoryProvider.Binder<>(
            ImmutableSet.of(JwtUser.class));

        environment.jersey().register(feature);
        environment.jersey().register(binder);
        environment.jersey().register(RolesAllowedDynamicFeature.class);
    }
}
