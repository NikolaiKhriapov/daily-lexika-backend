package my.project.application_user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApplicationUserService {

    private final ApplicationUserRepository applicationUserRepository;

    public void registerUser(ApplicationUserRegistrationRequest applicationUserRegistrationRequest) {
        ApplicationUser applicationUser = new ApplicationUser(
                applicationUserRegistrationRequest.name(),
                applicationUserRegistrationRequest.surname(),
                applicationUserRegistrationRequest.email()
        );

        // TODO: check if email valid
        // TODO: check if email not taken
        applicationUserRepository.save(applicationUser);
    }
}
