package my.project.applicationuser.applicationuser;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ApplicationUserUserDetailsService implements UserDetailsService {

    private final ApplicationUserRepository applicationUserRepository;
    private final MessageSource messageSource;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return applicationUserRepository.findApplicationUserByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(messageSource.getMessage(
                        "exception.authentication.usernameNotFound", null, Locale.getDefault())));
    }
}
