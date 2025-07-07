package com.persou.prontosus.config.security;

import com.persou.prontosus.gateway.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
            .map(user -> {
                log.info("Carregando usuário: {} com role: {}", username, user.role());

                var authority = new SimpleGrantedAuthority("ROLE_" + user.role());

                log.info("Authority criada: {}", authority.getAuthority());

                return User.builder()
                    .username(user.username())
                    .password(user.password())
                    .authorities(List.of(authority))
                    .accountExpired(false)
                    .accountLocked(false)
                    .credentialsExpired(false)
                    .disabled(!user.active())
                    .build();
            })
            .orElseThrow(() -> {
                log.error("Usuário não encontrado: {}", username);
                return new UsernameNotFoundException("Usuário não encontrado: " + username);
            });
    }
}