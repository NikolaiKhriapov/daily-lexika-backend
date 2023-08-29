package my.project.user.user;

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
public class UserDTOMapper implements Function<User, UserDTO> {

    private final FileStorageClient fileStorageClient;

    @Override
    public UserDTO apply(User user) {
        byte[] profilePhoto = "".getBytes();
        if (user.getProfilePhoto() != null) {
            GetPhotoResponse getPhotoResponse = fileStorageClient.getPhoto(new GetPhotoRequest(user.getProfilePhoto()));
            profilePhoto = getPhotoResponse.photo();
        }

        return new UserDTO(
                user.getId(),
                user.getName(),
                user.getSurname(),
                user.getEmail(),
                null,
                user.getGender(),
                profilePhoto,
                user.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList())
        );
    }
}
