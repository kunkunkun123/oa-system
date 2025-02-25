package com.office.security;

import com.office.entity.User;
import com.office.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.lang.NonNull;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    
    @Autowired
    private UserService userService;
    
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
            
        String token = request.getHeader("Authorization");
        
        // 添加调试日志
        System.out.println("Request URI: " + request.getRequestURI());
        System.out.println("Authorization header: " + token);
        
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            
            try {
                if (jwtTokenUtil.validateToken(token)) {
                    setAuthenticationContext(token, request);
                }
            } catch (Exception e) {
                System.out.println("Token validation failed: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        filterChain.doFilter(request, response);
    }

    private void setAuthenticationContext(String token, HttpServletRequest request) {
        try {
            Integer userId = jwtTokenUtil.getUserIdFromToken(token);
            User user = userService.getUserById(userId);
            
            List<GrantedAuthority> authorities = new ArrayList<>();
            // 根据用户角色添加对应的权限
            if (user.getRoleId() == 1) {
                authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            }
            // 所有用户都有基本权限
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            
            UsernamePasswordAuthenticationToken authentication = 
                new UsernamePasswordAuthenticationToken(
                    user,
                    null,
                    authorities
                );
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            request.setAttribute("currentUserId", userId);
            
            System.out.println("User authenticated: " + user.getUsername());
            System.out.println("User authorities: " + authorities);
        } catch (Exception e) {
            System.out.println("Token validation failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 