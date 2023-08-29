package my.project.user.user.account;

import lombok.RequiredArgsConstructor;
import my.project.user.user.User;
import my.project.user.user.UserDTO;
import my.project.user.user.UserDTOMapper;
import my.project.user.user.UserRepository;
import my.project.clients.filestorage.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserAccountService {

    private final UserRepository userRepository;
    private final FileStorageClient fileStorageClient;
    private final UserDTOMapper userDTOMapper;
    private final MessageSource messageSource;

    public UserDTO getUser() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDTOMapper.apply(user);
    }

    public byte[] getProfilePhoto() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (StringUtils.isBlank(user.getProfilePhoto())) {
            return new byte[]{};
        }

        GetPhotoResponse getPhotoResponse = fileStorageClient.getPhoto(new GetPhotoRequest(user.getProfilePhoto()));
        return getPhotoResponse.photo();
    }

    public void updateProfilePhoto(MultipartFile file) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        validatePhoto(file);

        try {
            deleteProfilePhoto();

            PutProfilePhotoResponse putProfilePhotoResponse = fileStorageClient.putProfilePhoto(
                    new PutProfilePhotoRequest(user.getId(), file.getBytes(), file.getOriginalFilename()));
            String profilePhotoPath = putProfilePhotoResponse.profilePhotoDirectoryAndName();

            user.setProfilePhoto(profilePhotoPath);
        } catch (IOException e) {
            throw new RuntimeException(messageSource.getMessage(
                    "exception.userAccount.updateProfilePhoto.notUploaded", null, Locale.getDefault()));
        }

        userRepository.save(user);
    }

    public void deleteProfilePhoto() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        fileStorageClient.deletePhoto(new DeletePhotoRequest(user.getProfilePhoto()));

        user.setProfilePhoto(null);

        userRepository.save(user);
    }

    private void validatePhoto(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException(messageSource.getMessage(
                    "exception.userAccount.validatePhoto.emptyFile", null, Locale.getDefault()));
        }

        long maxSize = 5 * 1024 * 1024; // 5 MB
        if (file.getSize() > maxSize) {
            throw new MaxUploadSizeExceededException(maxSize);
        }

        Set<String> supportedExtensions = new HashSet<>(
                List.of(messageSource.getMessage("profilePhoto.supportedExtensions", null, Locale.getDefault()).split(","))
        );
        boolean isAllowedExtension = false;
        for (String extension : supportedExtensions) {
            if (Objects.requireNonNull(file.getOriginalFilename()).toLowerCase().endsWith(extension)) {
                isAllowedExtension = true;
                break;
            }
        }
        if (!isAllowedExtension) {
            String message = messageSource.getMessage(
                    "exception.userAccount.validatePhoto.invalidExtension", null, Locale.getDefault());
            throw new IllegalArgumentException(message + " " + supportedExtensions);
        }
    }
}
