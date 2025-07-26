package my.project.dailylexika.security.service;

import lombok.RequiredArgsConstructor;
import my.project.dailylexika.user._public.PublicUserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserUserDetailsService implements UserDetailsService {

    private final PublicUserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userService.getUserEntityByEmail(username);
    }
}
