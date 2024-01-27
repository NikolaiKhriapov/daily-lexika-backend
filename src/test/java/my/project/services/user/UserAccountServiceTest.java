package my.project.services.user;

import my.project.config.AbstractUnitTest;
import my.project.repositories.user.UserRepository;
import my.project.services.flashcards.ReviewService;
import my.project.services.flashcards.WordService;
import my.project.services.notification.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;

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