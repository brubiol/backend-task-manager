package com.example.demo.practice;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Practice user details service with dummy user data.
 */
@Service
public class DummyUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // TODO: replace with real lookup and password handling
        return org.springframework.security.core.userdetails.User
            .withUsername(username)
            .password("N/A")
            .authorities(Collections.emptyList())
            .build();
    }
}
