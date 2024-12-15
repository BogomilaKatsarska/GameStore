package bg.softuni.gamestore.service.impls;

import bg.softuni.gamestore.data.entities.User;
import bg.softuni.gamestore.data.repositories.UserRepository;
import bg.softuni.gamestore.service.UserService;
import bg.softuni.gamestore.service.dtos.UserCreateDto;
import bg.softuni.gamestore.service.dtos.UserLoginDto;
import bg.softuni.gamestore.utils.ValidatorUtil;
import jakarta.validation.ConstraintViolation;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    public final ModelMapper modelMapper;
    private final ValidatorUtil validatorUtil;
    private User user;

    public UserServiceImpl(UserRepository userRepository, ModelMapper modelMapper, ValidatorUtil validatorUtil) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.validatorUtil = validatorUtil;
    }


    @Override
    public String registerUser(UserCreateDto userCreateDto) {
        if (!userCreateDto.getPassword().equals(userCreateDto.getConfirmPassword())) {
            return "Passwords don't match";
        }

        if (!validatorUtil.isValid(userCreateDto)) {
            return validatorUtil.validate(userCreateDto)
                    .stream().map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining("\n"));
        }
        if (this.userRepository.findByEmail(userCreateDto.getEmail()).isPresent()) {
            return "Email address already in use";
        }
        User user = this.modelMapper.map(userCreateDto, User.class);
        this.userRepository.saveAndFlush(user);
        setRootUserAdmin(user);
        return String.format("%s was registered %n", user.getFullName());
    }

    @Override
    public String loginUser(UserLoginDto userLoginDto) {
        Optional<User> user = this.userRepository.findByEmailAndPassword(userLoginDto.getEmail(), userLoginDto.getPassword());
        if (user.isEmpty()) {
            return "Invalid email or password";
        } else {
            this.user = user.get();
            return String.format("Successfully logged in %s%n", getUser().getFullName());
        }
    }

    private void setRootUserAdmin(User user) {
        if (this.userRepository.count() == 0){
            user.setAdmin(true);
        }
    }

    public User getUser() {
        return this.user;
    }

    @Override
    public boolean isLoggedIn() {
        return this.user != null;
    }

    @Override
    public boolean isAdmin() {
        return this.isLoggedIn() && this.user.isAdmin();
    }

    @Override
    public String logout() {
        if (this.isLoggedIn()) {
            String output = String.format("User %s successfully logged out%n", user.getFullName());
            this.user = null;
            return output;
        }
        return "No logged in user";
    }

    public void setUser(User user) {
        this.user = user;
    }
}
