package foodmap.V2.jwt;

import foodmap.V2.exception.jwt.AccessTokenExpired;
import foodmap.V2.exception.user.Unauthorized;
import foodmap.V2.jwt.RefreshToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Component
@Slf4j
public class JwtService {
    public static final String SECRET = "357638792F423F4428472B4B6250655368566D597133743677397A2443264629";
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    public String extractUserid(String token){
        Claims body = extractAllClaims(token);
        return (String)body.get("user_id");
    }
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    private Claims extractAllClaims(String token) {

            return Jwts
                    .parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

    }
    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
    public String generateToken(String username,String id){
        Map<String, Object> claims = new HashMap<>();
        claims.put("user_id",id);
        return createToken(claims, username);
    }
    private static String createToken(Map<String, Object> claims, String username) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+ 1000 * 60))
                .signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
    }

    private static Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    public RefreshToken createRefreshToken(){
        return RefreshToken.builder()
                .refresh(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(1000*60*60)) // set expiry of refresh token to 10 minutes - you can configure it application.properties file
                .build();
    }
    public Boolean verifyRefreshTokenExpiration(RefreshToken token){
        return token.getExpiryDate().compareTo(Instant.now()) >= 0;
    }
}
