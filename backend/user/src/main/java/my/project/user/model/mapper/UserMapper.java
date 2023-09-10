package my.project.user.model.mapper;

import lombok.RequiredArgsConstructor;
import my.project.clients.filestorage.FileStorageClient;
import my.project.clients.filestorage.GetPhotoRequest;
import my.project.clients.filestorage.GetPhotoResponse;
import my.project.user.model.entity.User;
import my.project.user.model.dto.UserDTO;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserMapper implements Mapper<User, UserDTO> {

    private final FileStorageClient fileStorageClient;

    @Override
    public UserDTO toDTO(User user) {
        byte[] profilePhoto = null;
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
                        .collect(Collectors.toList()),
                user.getCurrentStreak(),
                user.getDateOfLastStreak(),
                user.getRecordStreak()
        );
    }

    public UserDTO toDTOStatistics(User user) {
        return new UserDTO(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                user.getCurrentStreak(),
                null,
                user.getRecordStreak()
        );
    }
}
