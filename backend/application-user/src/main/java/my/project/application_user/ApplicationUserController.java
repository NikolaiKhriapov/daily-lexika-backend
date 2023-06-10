package my.project.application_user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/application-users")
@RequiredArgsConstructor
public class ApplicationUserController {

    private final ApplicationUserService applicationUserService;

    @PostMapping
    public void registerUser(@RequestBody ApplicationUserRegistrationRequest applicationUserRegistrationRequest) {
        applicationUserService.registerUser(applicationUserRegistrationRequest);
    }
}
