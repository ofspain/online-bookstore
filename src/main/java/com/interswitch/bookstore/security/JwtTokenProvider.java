package com.interswitch.bookstore.security;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.interswitch.bookstore.exceptions.AuthenticationException;
import com.interswitch.bookstore.models.User;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;

import static com.interswitch.bookstore.exceptions.Messages.*;


@Component
public class JwtTokenProvider {

  @Autowired
  private JwtUserDetailsService jwtUserDetailsService;



  @Value("${encryption.jwt.secret}")
  private String secretKey;

  @Value("${encryption.jwt.ttl.seconds}")
  private long validityInSeconds;

  private Long validityInMilliSeconds;

  @PostConstruct
  protected void init() {

    secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    validityInMilliSeconds = validityInSeconds * 1000;
  }

  public String generateToken(String username) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + validityInMilliSeconds);

    return JWT.create()
            .withSubject(username)
            .withExpiresAt(expiryDate)
            .sign(Algorithm.HMAC512(secretKey));
  }


  public Authentication getAuthentication(String token, HttpServletRequest request) {
    User user = jwtUserDetailsService.loadUserByUsername(getUsernameFromToken(token));
    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

    /**
     * We can access this details later using this snippet
      WebAuthenticationDetails webDetails = (WebAuthenticationDetails) auth.getDetails();

      String remoteAddress = webDetails.getRemoteAddress();
      String sessionId = webDetails.getSessionId();
    */

    return auth;
  }

  public boolean validateToken(String token) {
    try {
      JWT.require(Algorithm.HMAC512(secretKey))
              .build()
              .verify(token);
      return true;
    } catch (JWTVerificationException ex) {
      return false;
    }
  }

  public String getUsernameFromToken(String token) {
    DecodedJWT decodedJWT = JWT.decode(token);
    return decodedJWT.getSubject();
  }

  public String resolveToken(HttpServletRequest req) {
    String bearerToken = req.getHeader("Authorization");
    if(bearerToken == null){
      throw new AuthenticationException(MISSING_AUTH_TOKEN);
    }

    if(!bearerToken.startsWith("Bearer ")){
      throw new AuthenticationException(INVALID_AUTH_TOKEN);
    }

    try{
      return bearerToken.substring(7);
    }catch(Exception ex){
      throw new AuthenticationException(UNKNOW_AUTH_ERROR);
    }

  }

}
