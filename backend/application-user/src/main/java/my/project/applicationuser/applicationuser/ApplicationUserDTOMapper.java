package my.project.applicationuser.applicationuser;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ApplicationUserDTOMapper implements Function<ApplicationUser, ApplicationUserDTO> {

    @Override
    public ApplicationUserDTO apply(ApplicationUser applicationUser) {
        return new ApplicationUserDTO(
                applicationUser.getId(),
                applicationUser.getName(),
                applicationUser.getSurname(),
                applicationUser.getEmail(),
                null,
                applicationUser.getGender(),
                applicationUser.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList())
        );
    }
}
