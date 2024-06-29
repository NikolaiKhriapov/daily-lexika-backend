package my.project.dailybudget.config.security;

import lombok.RequiredArgsConstructor;
import my.project.dailybudget.config.i18n.I18nUtil;
import my.project.dailybudget.repositories.user.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findUserByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(I18nUtil.getMessage("dailybudget-exceptions.authentication.usernameNotFound")));
        //TODO::: add all exception messages
    }
}
