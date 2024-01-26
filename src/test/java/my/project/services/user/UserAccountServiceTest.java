package my.project.services.user;

import my.project.config.AbstractUnitTest;
import my.project.models.dto.user.UserDTO;
import my.project.models.entity.user.RoleName;
import my.project.models.entity.user.User;
import my.project.models.mapper.user.RoleStatisticsMapper;
import my.project.models.mapper.user.UserMapper;
import my.project.repositories.user.UserRepository;
import my.project.services.flashcards.ReviewService;
import my.project.services.flashcards.WordService;
import my.project.services.notification.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import static my.project.util.data.TestDataUtil.generateUser;
import static my.project.util.data.TestDataUtil.mockAuthentication;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willCallRealMethod;

class UserAccountServiceTest extends AbstractUnitTest {

    private UserAccountService underTest;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private ReviewService reviewService;
    @Mock
    private WordService wordService;
    @Mock
    private RoleService roleService;
    @Mock
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        underTest = new UserAccountService(
                userRepository,
                passwordEncoder,
                reviewService,
                wordService,
                roleService,
                notificationService
        );
    }

//    @Test
//    void updateUserInfo() {
//        // Given
//        User user = generateUser(RoleName.USER_CHINESE);
//        mockAuthentication(user);
//
//        UserDTO userDTO = willCallRealMethod().given(userMapper.toDTO(user));
//
//        // When
//        underTest.updateUserInfo(userDTO);
//
//        // Then
//
//    }

//    @Disabled
//    @Test
//        //TODO:::
//    void deleteAccount() {
//        // Given
//
//        // When
//
//        // Then
//    }
}