package my.project.apigateway.jwt;

import io.jsonwebtoken.Claims;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Objects;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private final RouteValidator routeValidator;
    private final JwtService jwtService;
    private final MessageSource messageSource;

    public AuthenticationFilter(RouteValidator routeValidator, JwtService jwtService, MessageSource messageSource) {
        super(Config.class);
        this.routeValidator = routeValidator;
        this.jwtService = jwtService;
        this.messageSource = messageSource;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            if (routeValidator.isSecured.test(exchange.getRequest())) {
                String authHeader = Objects.requireNonNull(
                        exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION)
                ).get(0);

                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    authHeader = authHeader.substring(7);
                }

                try {
                    jwtService.validateToken(authHeader);
                } catch (Exception e) {
                    throw new RuntimeException(messageSource.getMessage(
                            "exception.authenticationFilter.invalidToken", null, Locale.getDefault()));
                }

                Claims claims = jwtService.extractAllClaims(authHeader);
                exchange.getRequest().mutate().header("userId", String.valueOf(claims.get("userId"))).build();
            }
            return chain.filter(exchange);
        });
    }

    public static class Config {
    }
}
