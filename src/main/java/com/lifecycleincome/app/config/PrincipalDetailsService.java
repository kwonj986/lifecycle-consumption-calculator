package com.lifecycleincome.app.config;

import com.lifecycleincome.app.entity.SiteUser;
import com.lifecycleincome.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService{
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SiteUser userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Could not find: " + username));

        return new PrincipalDetails(userEntity);
    }
}
