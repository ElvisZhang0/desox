package com.polymer.desox.repository;

import com.polymer.desox.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        log.info("当前用户为：{}", s);
        List<User> users = userRepository.findByUserName(s);
        if (users == null || users.isEmpty()) {
            throw new UsernameNotFoundException("admin: " + s + " do not exist!");
        }
        return users.get(0);
    }
}
