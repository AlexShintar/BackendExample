package ru.shintar.shopbackend.controller;

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
import ru.shintar.shopbackend.dto.Login;
import ru.shintar.shopbackend.dto.Register;
import ru.shintar.shopbackend.dto.Role;
import ru.shintar.shopbackend.entity.User;
import ru.shintar.shopbackend.exception.UserNotFoundException;
import ru.shintar.shopbackend.repository.AdRepository;
import ru.shintar.shopbackend.repository.CommentRepository;
import ru.shintar.shopbackend.repository.UserRepository;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UsersControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdRepository adsRepository;

    @Autowired
    private CommentRepository commentRepository;
    private UsernamePasswordAuthenticationToken principal;
    private final Faker faker = new Faker();

    @BeforeEach
    public void beforeEach() {
        User user = register(generateUser());
        principal = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());
        SecurityContextHolder.getContext().setAuthentication(principal);
    }

    @AfterEach
    public void afterEach() {
        commentRepository.deleteAll();
        adsRepository.deleteAll();
        userRepository.deleteAll();
    }

    @ParameterizedTest
    @MethodSource("provideParamsForLogin")
    public void loginTest(String login, String password, HttpStatus httpStatus) {
        Register registerReq = generateUser();
        registerReq.setUsername("user2@gmail.com");
        registerReq.setPassword("password");

        User user = register(registerReq);

        principal = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());
        SecurityContextHolder.getContext().setAuthentication(principal);

        Login loginReq = new Login();
        loginReq.setUsername(login);
        loginReq.setPassword(password);

        ResponseEntity<User> loginReqResponseEntity = testRestTemplate.postForEntity("http://localhost:" + port + "/login", loginReq, User.class);

        assertThat(loginReqResponseEntity.getStatusCode()).isEqualTo(httpStatus);
    }

    @Test
    public void registerNegativeTest() {
        Register register = generateUser();
        register.setRole(Role.USER);
        register.setUsername("user2@gmail.com");
        register.setPassword("password");

        register(register);

        ResponseEntity<User> registerNegativeReqResponseEntity = testRestTemplate.postForEntity("http://localhost:" + port + "/register", register, User.class);

        assertThat(registerNegativeReqResponseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }


    private Register generateUser() {
        Register registerReq = new Register();
        registerReq.setUsername(faker.internet().emailAddress());
        registerReq.setPassword(faker.internet().password());
        registerReq.setRole(Role.USER);
        registerReq.setFirstName(faker.name().firstName());
        registerReq.setLastName(faker.name().lastName());
        registerReq.setPhone(faker.phoneNumber().phoneNumber());
        return registerReq;
    }

    private User register(Register registerReq) {
        ResponseEntity<User> registerReqResponseEntity = testRestTemplate.postForEntity("http://localhost:" + port + "/register", registerReq, User.class);
        assertThat(registerReqResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        User user = userRepository.findUserByEmail(registerReq.getUsername())
                .orElseThrow(UserNotFoundException::new);
        assertThat(user).isNotNull();
        assertThat(user.getEmail()).isEqualTo(registerReq.getUsername());
        return user;
    }

    public static Stream<Arguments> provideParamsForLogin() {
        return Stream.of(
                Arguments.of("user2@gmail.com", "password", HttpStatus.OK),
                Arguments.of("user3@gmail.com", "password", HttpStatus.UNAUTHORIZED),
                Arguments.of("user2@gmail.com", "password10", HttpStatus.UNAUTHORIZED)
        );
    }

}
