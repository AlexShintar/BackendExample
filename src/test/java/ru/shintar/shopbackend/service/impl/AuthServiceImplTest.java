package ru.shintar.shopbackend.service.impl;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.shintar.shopbackend.entity.User;
import ru.shintar.shopbackend.exception.UserNotFoundException;
import ru.shintar.shopbackend.repository.UserRepository;
import ru.shintar.shopbackend.dto.Login;
import ru.shintar.shopbackend.dto.Register;
import ru.shintar.shopbackend.dto.Role;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthServiceImplTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private UserRepository userRepository;
    private UsernamePasswordAuthenticationToken principal;
    private final Faker faker = new Faker();

    @BeforeEach
    public void beforeEach() {
        User user = registerUser(fakeUser());
        principal = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());
        SecurityContextHolder.getContext().setAuthentication(principal);
    }

    @AfterEach
    public void afterEach() {
        userRepository.deleteAll();
    }

    @ParameterizedTest
    @MethodSource("usersForLogin")
    public void loginTest(String email, String password, HttpStatus httpStatus) {
        Register register = fakeUser();
        register.setUsername("user1@ash.com");
        register.setPassword("12345678");
        User user = registerUser(register);
        assertThat(user.getEmail()).isEqualTo(register.getUsername());
        principal = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());
        SecurityContextHolder.getContext().setAuthentication(principal);
        Login login = new Login();
        login.setUsername(email);
        login.setPassword(password);
        ResponseEntity<User> loginResponseEntity = testRestTemplate.postForEntity("http://localhost:" + port + "/login", login, User.class);
        assertThat(loginResponseEntity.getStatusCode()).isEqualTo(httpStatus);
    }

    @Test
    public void registerTest() {
        Register register = fakeUser();
        ResponseEntity<User> registerResponseEntity = testRestTemplate.postForEntity("http://localhost:" + port + "/register", register, User.class);
        assertThat(registerResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        register = fakeUser();
        registerUser(register);
        registerResponseEntity = testRestTemplate.postForEntity("http://localhost:" + port + "/register", register, User.class);
        assertThat(registerResponseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    private Register fakeUser() {
        Register register = new Register();
        register.setUsername(faker.internet().emailAddress());
        register.setPassword(faker.internet().password());
        register.setFirstName(faker.name().firstName());
        register.setLastName(faker.name().lastName());
        register.setPhone(faker.phoneNumber().phoneNumber());
        register.setRole(Role.USER);
        return register;
    }

    private User registerUser(Register register) {
        ResponseEntity<User> registerResponseEntity = testRestTemplate.postForEntity("http://localhost:" + port + "/register", register, User.class);
        assertThat(registerResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return userRepository.findUserByEmail(register.getUsername())
                .orElseThrow(UserNotFoundException::new);
    }

    public static Stream<Arguments> usersForLogin() {
        return Stream.of(
                Arguments.of("user1@ash.com", "12345678", HttpStatus.OK),
                Arguments.of("user1@ash.com", "87654321", HttpStatus.UNAUTHORIZED),
                Arguments.of("user2@ash.com", "87654321", HttpStatus.UNAUTHORIZED)
        );
    }
}