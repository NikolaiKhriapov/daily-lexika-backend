package my.project.config.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import my.project.models.entity.user.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import static java.time.temporal.ChronoUnit.DAYS;

@Service
public class JwtService {

    private static final String SECRET_KEY = "foobar_123456789_foobar_123456789_foobar_123456789_foobar_123456789";
    private static final String ISSUER = "chinese-learning-app";

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setIssuer(ISSUER)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plus(1, DAYS)))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String verifyEmail(String verificationToken) {
        String email = Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(verificationToken)
                .getBody()
                .getSubject();

        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSignInKey())
                    .requireIssuer(ISSUER)
                    .requireSubject(email)
                    .build()
                    .parseClaimsJws(verificationToken);
            return email;
        } catch (JwtException ignored) {
        }
        return "";
    }

    public String generateToken(User user) {
        Map<String, Object> extraClaims = Map.of("userId", user.getId());
        return generateToken(extraClaims, user);
    }

    public boolean isTokenValid(String token, String username) {
        return extractUsername(token).equals(username) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        Date expirationDate = extractClaim(token, Claims::getExpiration);
        return expirationDate.before(new Date());
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }
}
