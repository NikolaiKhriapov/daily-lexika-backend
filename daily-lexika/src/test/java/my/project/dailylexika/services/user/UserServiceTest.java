package my.project.dailylexika.services.user;

import my.project.dailylexika.config.AbstractUnitTest;
import my.project.dailylexika.mappers.user.UserMapper;
import my.project.dailylexika.repositories.user.UserRepository;
import my.project.dailylexika.services.flashcards.ReviewService;
import my.project.dailylexika.services.flashcards.WordPackService;
import my.project.dailylexika.services.flashcards.WordService;
import my.project.dailylexika.services.log.LogService;
import my.project.dailylexika.services.notification.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;

class UserServiceTest extends AbstractUnitTest {

    private UserService underTest;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private ReviewService reviewService;
    @Mock
    private WordService wordService;
    @Mock
    private WordPackService wordPackService;
    @Mock
    private RoleService roleService;
    @Mock
    private NotificationService notificationService;
    @Mock
    private LogService logService;

    @BeforeEach
    void setUp() {
        underTest = new UserService(
                userRepository,
                userMapper,
                passwordEncoder,
                reviewService,
                wordService,
                wordPackService,
                roleService,
                notificationService,
                logService
        );
    }

//    @Test
//    void updateUserInfo() {
//        // Given
//        User user = generateUser(RoleName.USER_CHINESE);
//        mockAuthentication(user);
//
//        UserDto userDTO = willCallRealMethod().given(userMapper.toDTO(user));
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
