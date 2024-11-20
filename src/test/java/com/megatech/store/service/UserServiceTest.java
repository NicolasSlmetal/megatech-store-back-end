package com.megatech.store.service;

import com.megatech.store.domain.Role;
import com.megatech.store.dtos.login.LoginDTO;
import com.megatech.store.dtos.login.TokenDTO;
import com.megatech.store.exceptions.InvalidUserFieldException;
import com.megatech.store.exceptions.TokenErrorException;
import com.megatech.store.model.UserModel;
import com.megatech.store.repository.UserRepository;
import org.junit.jupiter.api.*;

import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.event.annotation.AfterTestClass;

import java.util.Optional;


@ActiveProfiles("test")
public class UserServiceTest {

    private static MockedStatic<BCrypt> bcryptMock;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private UserService userService;

    @BeforeAll
    public static void initStaticMocks() {
        bcryptMock = mockStatic(BCrypt.class);
    }

    @BeforeEach
    public void initMocks() {
        bcryptMock.clearInvocations();
        MockitoAnnotations.openMocks(this);
    }

    @AfterAll
    public static void unregisterStaticMocks() {
        bcryptMock.close();
    }

    @Test
    @DisplayName("Should retrieve a Token DTO when login credentials are valid")
    public void testShouldRetrieveATokenDTOWhenLoginCredentialsAreValid() {
        LoginDTO loginDTO = new LoginDTO("email", "password");
        UserModel userModel = mock(UserModel.class);
        TokenDTO expected = new TokenDTO("token", Role.CUSTOMER);

        when(userRepository.findByEmail(loginDTO.email())).thenReturn(Optional.of(userModel));
        when(userModel.getPassword()).thenReturn("password");
        when(userModel.getRole()).thenReturn(Role.CUSTOMER);
        bcryptMock.when(() -> BCrypt.checkpw(anyString(), anyString())).thenReturn(true);
        when(tokenService.generateToken(userModel)).thenReturn("token");

        TokenDTO result = userService.login(loginDTO);

        Assertions.assertEquals(expected, result);
        verify(userRepository, times(1)).findByEmail(loginDTO.email());
        bcryptMock.verify(() -> BCrypt.checkpw(loginDTO.password(), userModel.getPassword()), times(1));
        verify(tokenService, times(1)).generateToken(userModel);
    }

    @Test
    @DisplayName("Should throw an exception when a email does not exist")
    public void testShouldThrowAnExceptionWhenEmailDoesNotExist() {
        LoginDTO loginDTO = new LoginDTO("email", "password");
        UserModel userModel = mock(UserModel.class);

        when(userRepository.findByEmail(loginDTO.email())).thenReturn(Optional.empty());
        when(userModel.getPassword()).thenReturn("password");
        when(userModel.getRole()).thenReturn(Role.CUSTOMER);
        bcryptMock.when(() -> BCrypt.checkpw(anyString(), anyString())).thenReturn(true);
        when(tokenService.generateToken(userModel)).thenReturn("token");

        Assertions.assertThrows(InvalidUserFieldException.class, () -> userService.login(loginDTO));
        verify(userRepository, times(1)).findByEmail(loginDTO.email());
        verify(tokenService, never()).generateToken(userModel);
        bcryptMock.verify(() -> BCrypt.checkpw(loginDTO.password(), userModel.getPassword()), never());
    }

    @Test
    @DisplayName("Should throw an exception when hash does not match")
    public void testShouldThrowAnExceptionWhenHashDoesNotMatch() {
        LoginDTO loginDTO = new LoginDTO("email", "password");
        UserModel userModel = mock(UserModel.class);
        when(userRepository.findByEmail(loginDTO.email())).thenReturn(Optional.of(userModel));
        when(userModel.getPassword()).thenReturn("password");
        when(userModel.getRole()).thenReturn(Role.CUSTOMER);
        bcryptMock.when(() -> BCrypt.checkpw(anyString(), anyString())).thenReturn(false);

        Assertions.assertThrows(InvalidUserFieldException.class, () -> userService.login(loginDTO));
        verify(userRepository, times(1)).findByEmail(loginDTO.email());
        bcryptMock.verify(() -> BCrypt.checkpw(loginDTO.password(), userModel.getPassword()),
                times(1));
        verify(tokenService, never()).generateToken(userModel);
    }

    @Test
    @DisplayName("Should throw an exception when a error while generating the token occurs")
    public void testShouldThrowAnExceptionWhenErrorWhileGeneratingToken() {
        LoginDTO loginDTO = new LoginDTO("email", "password");
        UserModel userModel = mock(UserModel.class);
        when(userRepository.findByEmail(loginDTO.email())).thenReturn(Optional.of(userModel));
        when(userModel.getPassword()).thenReturn("password");
        when(userModel.getRole()).thenReturn(Role.CUSTOMER);
        bcryptMock.when(() -> BCrypt.checkpw(anyString(), anyString())).thenReturn(true);
        when(tokenService.generateToken(userModel)).thenThrow(new TokenErrorException("error"));

        Assertions.assertThrows(TokenErrorException.class, () -> userService.login(loginDTO));
        verify(tokenService, times(1)).generateToken(userModel);
        bcryptMock.verify(() -> BCrypt.checkpw(loginDTO.password(), userModel.getPassword()), times(1));
        verify(userRepository, times(1)).findByEmail(loginDTO.email());
    }

    @Test
    @DisplayName("Should accept a email that not exists")
    public void testShouldAcceptAnEmailThatNotExists() {
        String email = "email";
        when(userRepository.existsByEmail(email)).thenReturn(false);

        Assertions.assertDoesNotThrow(() -> userService.validateIfEmailExists(email));
        verify(userRepository, times(1)).existsByEmail(email);
    }

    @Test
    @DisplayName("Should throw an error when is provided an email that exists")
    public void testShouldThrowAnErrorWhenIsProvidedAnEmailThatExists() {
        String email = "email";
        when(userRepository.existsByEmail(email)).thenReturn(true);

        Assertions.assertDoesNotThrow(() -> userService.validateIfEmailExists(email));
        verify(userRepository, times(1)).existsByEmail(email);
    }
}
