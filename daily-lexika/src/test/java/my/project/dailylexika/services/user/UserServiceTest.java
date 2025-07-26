package my.project.dailylexika.services.user;

import my.project.dailylexika.config.AbstractUnitTest;
import my.project.dailylexika.user.model.mappers.UserMapper;
import my.project.dailylexika.user.persistence.UserRepository;
import my.project.dailylexika.flashcard.service.ReviewService;
import my.project.dailylexika.flashcard.service.WordPackService;
import my.project.dailylexika.flashcard.service.WordService;
import my.project.dailylexika.log.service.LogService;
import my.project.dailylexika.notification.service.NotificationService;
import my.project.dailylexika.user.service.RoleService;
import my.project.dailylexika.user.service.UserService;
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
