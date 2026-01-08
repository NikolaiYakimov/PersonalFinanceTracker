package FinanceTracker.security;

import FinanceTracker.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private int jwtExpiration;

    private SecretKey key;
    @PostConstruct
    public void init(){
        this.key= Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject);
    }

    public String generateToken(User user){

        List<String> roles=user.getRoles().stream()
                .map(role->role.getName().toString())
                .toList();

        Map<String,Object> extraClaims=new HashMap<>();
        extraClaims.put("roles",roles);
        extraClaims.put("userId",user.getId());

        return generateToken(extraClaims, user);

    }
    public String generateToken(Map<String,Object > extraClaims, User user ){
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date((new Date()).getTime()+ jwtExpiration))
                .signWith(key)
                .compact();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims= extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isTokenValid(String token, UserDetails userDetails){
        final String username=extractUsername(token);
        return (username.equals(userDetails.getUsername()) &&!isTokenExpired(token));
    }

    private boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token){
        return extractClaim(token,Claims::getExpiration);
    }
}
