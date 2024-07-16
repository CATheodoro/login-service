package com.theodoro.loginservice.infra.securities;

import com.theodoro.loginservice.domains.entities.UserAccount;
import com.theodoro.loginservice.domains.exceptions.ForbiddenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.theodoro.loginservice.domains.enumerations.ExceptionMessagesEnum.JWT_NOT_VALIDITY;

@Service
public class JwtService {

    @Value("${application.security.jwt.secret-key}")
    private String secreteKey;

    @Value("${application.security.jwt.expiration}")
    private Long jwtExpiration;

    @Value("${application.security.jwt.refresh-token.expiration}")
    private Long jwtRefreshExpiration;

    public String extractUsername(String token) {
        try {
            return extractClaim(token, Claims::getSubject);
        } catch (Exception e){
            throw new ForbiddenException(JWT_NOT_VALIDITY);
        }
    }

    public String extractLastUpdateDatePassword(String token) {
        return extractClaim(token, claims -> claims.get("lastUpdateDatePassword", String.class));
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSignIngKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String generateToken(UserAccount userAccount) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", userAccount.getId());
        claims.put("name", userAccount.getName());
        return buildToken(claims, userAccount, jwtExpiration);
    }

    public String generateRefreshToken(UserAccount userAccount) {
        return buildToken(new HashMap<>(), userAccount, jwtRefreshExpiration);
    }

    public String buildToken(Map<String, Object> extraClaims, UserAccount userAccount, Long expirationJwt){
        List<String> authorities = userAccount.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        return Jwts
                .builder()
                .claims(extraClaims)
                .subject(userAccount.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationJwt))
                .claim("authorities", authorities)
                .claim("lastUpdateDatePassword", userAccount.getLastUpdateDatePassword().toString())
                .signWith(getSignIngKey())
                .compact();
    }

    public boolean isTokenValid(String token, UserAccount userAccount){
        final String userEmail = extractUsername(token);
        final String userLastUpdateDatePassword = extractLastUpdateDatePassword(token);
        return (userEmail.equals(userAccount.getUsername())) && userLastUpdateDatePassword.equals(userAccount.getLastUpdateDatePassword().toString()) && isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return !extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private SecretKey getSignIngKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secreteKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}