package com.example.fullstack.auth;

import com.example.fullstack.user.User;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.security.AuthenticationFailedException;
import io.smallrye.jwt.build.Jwt;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.Duration;
import java.util.HashSet;

@ApplicationScoped
public class AuthService {

    @Inject
    @ConfigProperty(name = "mp.jwt.verify.issuer")
    private String issuer;

    @WithSession
    public Uni<String> authenticate(AuthRequest authRequest) {
        return User.findByName(authRequest.name())
                .onItem()
                .transform(user -> {
                    if (user == null || !User.matches(user, authRequest.password())) {
                        throw new AuthenticationFailedException("Invalid credentials");
                    }
                    return Jwt.issuer(issuer)
                            .upn(user.name)
                            .groups(new HashSet<>(user.roles))
                            .expiresIn(Duration.ofHours(1L))
                            .sign();
                });
    }

}