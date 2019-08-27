package com.polymer.desox.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.polymer.desox.bean.ResponseBean;
import com.polymer.desox.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Date;

public class JwtLoginFilter extends AbstractAuthenticationProcessingFilter {

    protected JwtLoginFilter(String defaultFilterProcessesUrl, AuthenticationManager authenticationManager) {
        super(new AntPathRequestMatcher(defaultFilterProcessesUrl));
        setAuthenticationManager(authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws AuthenticationException, IOException, ServletException {
        User user = new ObjectMapper().readValue(httpServletRequest.getInputStream(), User.class);
        return getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));

    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        Collection<? extends GrantedAuthority> authorities = authResult.getAuthorities();
        if (authorities.size() == 0) {
            response.setContentType("application/json;charset=utf-8");
            ResponseBean responseBean = ResponseBean.builder()
                    .status(HttpStatus.FORBIDDEN.value())
                    .message("用户暂未分配角色")
                    .build();
            PrintWriter out = response.getWriter();
            out.write(new ObjectMapper().writeValueAsString(responseBean));
            out.flush();
            out.close();
        } else {
            StringBuffer as = new StringBuffer();
            for (GrantedAuthority authority : authorities) {
                as.append(authority.getAuthority())
                        .append(",");
            }
            String jwtToken = Jwts.builder()
                    .claim("Authorization", as)
                    .setSubject(authResult.getName())
                    .setExpiration(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                    .signWith(SignatureAlgorithm.HS512, "polymer")
                    .compact();

            response.setContentType("application/json;charset=utf-8");
            ResponseBean responseBean = ResponseBean.builder()
                    .status(HttpStatus.OK.value())
                    .message("登录成功")
                    .data(jwtToken)
                    .build();
            PrintWriter out = response.getWriter();
            out.write(new ObjectMapper().writeValueAsString(responseBean));
            out.flush();
            out.close();
        }
    }


    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        response.setContentType("application/json;charset=utf-8");
        ResponseBean responseBean = ResponseBean.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .message("用户名或密码错误")
                .build();
        PrintWriter out = response.getWriter();
        out.write(new ObjectMapper().writeValueAsString(responseBean));
        out.flush();
        out.close();
    }
}
