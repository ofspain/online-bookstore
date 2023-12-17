package com.interswitch.bookstore.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtTokenFilter extends OncePerRequestFilter {

  private JwtTokenProvider jwtTokenProvider;

  public JwtTokenFilter(JwtTokenProvider jwtTokenProvider) {
    this.jwtTokenProvider = jwtTokenProvider;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    //System.out.println(request.getRequestURI() + " <<<>>> "+request.getRequestURL());
    String token = jwtTokenProvider.resolveToken(request);
    try {
      if (jwtTokenProvider.validateToken(token)) {
        Authentication auth = jwtTokenProvider.getAuthentication(token, request);

        SecurityContextHolder.getContext().setAuthentication(auth);
      }
    } catch (Exception ex) {
      SecurityContextHolder.clearContext();
      response.sendError(HttpStatus.FORBIDDEN.value(), ex.getMessage());
      return;
    }

    filterChain.doFilter(request, response);
  }
}
