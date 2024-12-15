package bg.softuni.gamestore.service;

import bg.softuni.gamestore.data.entities.User;
import bg.softuni.gamestore.service.dtos.UserCreateDto;
import bg.softuni.gamestore.service.dtos.UserLoginDto;

public interface UserService {
    String registerUser(UserCreateDto userCreateDto);

    String loginUser(UserLoginDto userLoginDto);

    User getUser();

    boolean isLoggedIn();

    boolean isAdmin();

    String logout();
}
