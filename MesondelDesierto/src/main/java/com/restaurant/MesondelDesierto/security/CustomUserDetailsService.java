package com.restaurant.MesondelDesierto.security;

import com.restaurant.MesondelDesierto.entity.User;
import com.restaurant.MesondelDesierto.exception.NotFoundException;
import com.restaurant.MesondelDesierto.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepo userRepo;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepo.findByEmail(username)
                .orElseThrow(()-> new NotFoundException("User/email no encontrado"));

        return AuthUser.builder()
                .user(user)
                .build();
    }
}
