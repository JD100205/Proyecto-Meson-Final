package com.restaurant.MesondelDesierto.service.impl;

import com.restaurant.MesondelDesierto.dto.LoginRequest;
import com.restaurant.MesondelDesierto.dto.Response;
import com.restaurant.MesondelDesierto.dto.UserDto;
import com.restaurant.MesondelDesierto.entity.User;
import com.restaurant.MesondelDesierto.enums.UserRole;
import com.restaurant.MesondelDesierto.exception.InvalidCredentialsException;
import com.restaurant.MesondelDesierto.exception.NotFoundException;
import com.restaurant.MesondelDesierto.mapper.EntityDtoMapper;
import com.restaurant.MesondelDesierto.repository.UserRepo;
import com.restaurant.MesondelDesierto.security.JwtUtils;
import com.restaurant.MesondelDesierto.service.interf.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final EntityDtoMapper entityDtoMapper;


    @Override
    public Response registerUser(UserDto registrationRequest) {
        UserRole role = UserRole.USER;

        if (registrationRequest.getRole() != null && registrationRequest.getRole().equalsIgnoreCase("admin")) {
            role = UserRole.ADMIN;
        }

        User user = User.builder()
                .name(registrationRequest.getName())
                .email(registrationRequest.getEmail())
                .password(passwordEncoder.encode(registrationRequest.getPassword()))
                .phoneNumber(registrationRequest.getPhoneNumber())
                .role(role)
                .build();

        User savedUser = userRepo.save(user);
        System.out.println(savedUser);

        UserDto userDto = entityDtoMapper.mapUserToDtoBasic(savedUser);
        return Response.builder()
                .status(200)
                .message("El usuario se ha añadido correctamente")
                .user(userDto)
                .build();
    }



    @Override
    public Response loginUser(LoginRequest loginRequest) {

        User user = userRepo.findByEmail(loginRequest.getEmail()).orElseThrow(()-> new NotFoundException("Email no encontrado"));
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())){
            throw new InvalidCredentialsException("La contraseña no coincide");
        }
        String token = jwtUtils.generateToken(user);

        return Response.builder()
                .status(200)
                .message("El usuario ha iniciado sesion correctamente")
                .token(token)
                .expirationTime("6 meses")
                .role(user.getRole().name())
                .build();
    }

    @Override
    public Response getAllUsers() {

        List<User> users = userRepo.findAll();
        List<UserDto> userDtos = users.stream()
                .map(entityDtoMapper::mapUserToDtoBasic)
                .toList();

        return Response.builder()
                .status(200)
                .userList(userDtos)
                .build();
    }

    @Override
    public User getLoginUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String  email = authentication.getName();
        log.info("El email del usuario es: " + email);
        return userRepo.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("Usuario no encontrado"));
    }

    @Override
    public Response getUserInfoAndOrderHistory() {
        User user = getLoginUser();
        UserDto userDto = entityDtoMapper.mapUserToDtoPlusAddressAndOrderHistory(user);

        return Response.builder()
                .status(200)
                .user(userDto)
                .build();
    }
}
