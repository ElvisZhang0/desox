package com.polymer.desox.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.polymer.desox.bean.ResponseBean;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Slf4j
public class JwtFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String jwtToken = request.getHeader("Authorization");
        log.info("请求的Token为:{}", jwtToken);
        if (StringUtils.isEmpty(jwtToken)) {
            servletResponse.setContentType("application/json;charset=utf-8");
            ResponseBean responseBean = ResponseBean.builder()
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .message("Authorization" + "为空")
                    .build();
            PrintWriter out = servletResponse.getWriter();
            out.write(new ObjectMapper().writeValueAsString(responseBean));
            out.flush();
            out.close();
            return;
        }
        Claims claims = null;
        try {
            claims = Jwts.parser().setSigningKey("polymer").parseClaimsJws(jwtToken).getBody();
        }catch (MalformedJwtException e){
            servletResponse.setContentType("application/json;charset=utf-8");
            ResponseBean responseBean = ResponseBean.builder()
                    .status(HttpStatus.FORBIDDEN.value())
                    .message("Authorization不能解析")
                    .data(jwtToken)
                    .build();
            PrintWriter out = servletResponse.getWriter();
            out.write(new ObjectMapper().writeValueAsString(responseBean));
            out.flush();
            out.close();
            return;
        }
        String username = claims.getSubject();//获取当前登录用户名
        if (StringUtils.isEmpty(username)) {
            servletResponse.setContentType("application/json;charset=utf-8");
            ResponseBean responseBean = ResponseBean.builder()
                    .status(HttpStatus.FORBIDDEN.value())
                    .message("Authorization不能解析")
                    .data(jwtToken)
                    .build();
            PrintWriter out = servletResponse.getWriter();
            out.write(new ObjectMapper().writeValueAsString(responseBean));
            out.flush();
            out.close();
            return;
        }

        List<? extends GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList((String) claims.get("Authorization"));
        if (authorities == null || authorities.size() == 0) {
            servletResponse.setContentType("application/json;charset=utf-8");
            ResponseBean responseBean = ResponseBean.builder()
                    .status(HttpStatus.FORBIDDEN.value())
                    .message("用户没有分配角色")
                    .data(jwtToken)
                    .build();
            PrintWriter out = servletResponse.getWriter();
            out.write(new ObjectMapper().writeValueAsString(responseBean));
            out.flush();
            out.close();
            return;
        }
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(token);
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
