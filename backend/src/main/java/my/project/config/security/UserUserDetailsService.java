package my.project.config.security;

import lombok.RequiredArgsConstructor;
import my.project.repositories.user.UserRepository;
import org.springframework.context.MessageSource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class UserUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final MessageSource messageSource;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findUserByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(messageSource.getMessage(
                        "exception.authentication.usernameNotFound", null, Locale.getDefault())));
    }
}
