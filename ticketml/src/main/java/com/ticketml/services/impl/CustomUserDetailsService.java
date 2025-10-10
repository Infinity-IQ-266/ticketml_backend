package com.ticketml.services.impl;

import com.ticketml.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String googleId) throws UsernameNotFoundException {
        com.ticketml.common.entity.User user = userRepository.findByGoogleId(googleId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with googleId: " + googleId));

        return new User(
                user.getGoogleId(),
                "",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }
}
