package com.iss.renterscore.authentication.securityconfig;

import com.iss.renterscore.authentication.service.CustomUserDetailService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {


    private final String tokenRequestHeader = "Authorization";
    private final String tokenRequestHeaderPrefix = "Bearer ";

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenFilter.class);
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailService userDetailService;

    private static final List<String> EXCLUDED_PATHS = List.of(
            "/v3/api-docs",
            "/api-docs",
            "/swagger-ui",
            "/swagger-ui.html",
            "api-docs/swagger-config",
            "/webjars",
            "/swagger-resources",
            "/actuator",
            "/images"
    );

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getRequestURI();
        if (EXCLUDED_PATHS.stream().anyMatch(path::startsWith)) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            String jwt = getJwtFromRequest(request);
            if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {
                String email = jwtTokenProvider.getEmailFromJwt(jwt);
                UserDetails userDetails = userDetailService.loadUserByUsername(email);
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, jwt, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }

        }catch (Exception e) {
            logger.error("Failed to set user authentication: {}", String.valueOf(e));
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String barerToken = request.getHeader(tokenRequestHeader);
        if (StringUtils.hasText(barerToken) && barerToken.startsWith(tokenRequestHeaderPrefix)) {
            logger.atDebug().log("Extracted Token:" + barerToken);
            return barerToken.replace(tokenRequestHeaderPrefix, "");
        }
        return null;
    }
}
