package com.lifecycleincome.app.service;

import com.lifecycleincome.app.entity.SiteUser;
import com.lifecycleincome.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public SiteUser create(String username, String password) {
        SiteUser user = new SiteUser();
        user.setUsername(username);
        String encodedPassword = passwordEncoder.encode(password);
        user.setPassword(encodedPassword);
        return userRepository.save(user);
    }

    public void delete(String username) {
        SiteUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find username"));
        userRepository.delete(user);
    }

    public void update(String username, String password) {
        SiteUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find username"));
        String encodedPassword = passwordEncoder.encode(password);
        user.setPassword(encodedPassword);
        user.setUsername(username);
        userRepository.save(user);
    }
}
