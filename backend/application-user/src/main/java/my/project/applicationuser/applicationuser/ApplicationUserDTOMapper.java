package my.project.applicationuser.applicationuser;

import lombok.RequiredArgsConstructor;
import my.project.clients.filestorage.FileStorageClient;
import my.project.clients.filestorage.GetPhotoRequest;
import my.project.clients.filestorage.GetPhotoResponse;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationUserDTOMapper implements Function<ApplicationUser, ApplicationUserDTO> {

    private final FileStorageClient fileStorageClient;

    @Override
    public ApplicationUserDTO apply(ApplicationUser applicationUser) {
        byte[] profilePhoto = "".getBytes();
        if (applicationUser.getProfilePhoto() != null) {
            GetPhotoResponse getPhotoResponse = fileStorageClient.getPhoto(new GetPhotoRequest(applicationUser.getProfilePhoto()));
            profilePhoto = getPhotoResponse.photo();
        }

        return new ApplicationUserDTO(
                applicationUser.getId(),
                applicationUser.getName(),
                applicationUser.getSurname(),
                applicationUser.getEmail(),
                null,
                applicationUser.getGender(),
                profilePhoto,
                applicationUser.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList())
        );
    }
}
