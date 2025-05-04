package com.farmdora.farmdorabuyer.auth;

import static com.farmdora.farmdorabuyer.auth.JwtConstants.*;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = request.getHeader(AUTHORIZATION_HEADER);
        if (token != null) {
            token = token.replace(TOKEN_PREFIX, "");
        }

        if (token != null && jwtUtil.validateToken(token)) {
            int userId = jwtUtil.getUserId(token);
            Collection<? extends GrantedAuthority> authorities = jwtUtil.getAuthorities(token);

            if (Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_KEY + token))) {
                log.warn("블랙리스트 토큰 접근 차단");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userId, null, jwtUtil.getAuthorities(token));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}