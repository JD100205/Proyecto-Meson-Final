package com.restaurant.MesondelDesierto.service.interf;

import com.restaurant.MesondelDesierto.dto.LoginRequest;
import com.restaurant.MesondelDesierto.dto.Response;
import com.restaurant.MesondelDesierto.dto.UserDto;
import com.restaurant.MesondelDesierto.entity.User;

public interface UserService {
    Response registerUser(UserDto registrationRequest);
    Response loginUser(LoginRequest loginRequest);
    Response getAllUsers();
    User getLoginUser();
    Response getUserInfoAndOrderHistory();
}
