package my.project.dailylexika.user.service;

import my.project.dailylexika.config.AbstractUnitTest;
import my.project.dailylexika.user._public.PublicRoleService;
import my.project.dailylexika.user.model.mappers.UserMapper;
import my.project.dailylexika.user.persistence.UserRepository;
import my.project.dailylexika.user.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;

class UserServiceImplTest extends AbstractUnitTest {

    private UserServiceImpl underTest;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private RoleService roleService;
    @Mock
    private PublicRoleService publicRoleService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    @BeforeEach
    void setUp() {
        underTest = new UserServiceImpl(
                userRepository,
                userMapper,
                roleService,
                publicRoleService,
                passwordEncoder,
                eventPublisher
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
