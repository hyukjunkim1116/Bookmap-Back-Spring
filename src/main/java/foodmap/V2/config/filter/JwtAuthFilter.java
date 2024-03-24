package foodmap.V2.config.filter;

import foodmap.V2.exception.ErrorResponse;
import foodmap.V2.exception.user.InvalidRequest;
import foodmap.V2.exception.user.Unauthorized;
import foodmap.V2.service.JwtService;
import foodmap.V2.service.user.UserDetailsServiceImpl;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Slf4j
@Component
@AllArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private JwtService jwtService;
    private UserDetailsServiceImpl userDetailsServiceImpl;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String email;
        if(authHeader != null && authHeader.startsWith("Bearer ")){
            String token = authHeader.substring(7);
            email = jwtService.extractUsername(token);
            if(email != null && SecurityContextHolder.getContext().getAuthentication() == null){
                UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(email);
                if(jwtService.validateToken(token, userDetails)){
                        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        }

            filterChain.doFilter(request, response);

    }
}