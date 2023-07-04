package my.project.applicationuser.applicationuser.account;

import lombok.RequiredArgsConstructor;
import my.project.applicationuser.applicationuser.ApplicationUser;
import my.project.applicationuser.applicationuser.ApplicationUserDTO;
import my.project.applicationuser.applicationuser.ApplicationUserDTOMapper;
import my.project.applicationuser.applicationuser.ApplicationUserRepository;
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
public class ApplicationUserAccountService {

    private final ApplicationUserRepository applicationUserRepository;
    private final FileStorageClient fileStorageClient;
    private final ApplicationUserDTOMapper applicationUserDTOMapper;
    private final MessageSource messageSource;

    public ApplicationUserDTO getApplicationUser() {
        ApplicationUser applicationUser =
                (ApplicationUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return applicationUserDTOMapper.apply(applicationUser);
    }

    public byte[] getProfilePhoto() {
        ApplicationUser applicationUser =
                (ApplicationUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (StringUtils.isBlank(applicationUser.getProfilePhoto())) {
            return new byte[]{};
        }

        GetPhotoResponse getPhotoResponse = fileStorageClient.getPhoto(new GetPhotoRequest(applicationUser.getProfilePhoto()));
        return getPhotoResponse.photo();
    }

    public void updateProfilePhoto(MultipartFile file) {
        ApplicationUser applicationUser =
                (ApplicationUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        validatePhoto(file);

        try {
            deleteProfilePhoto();

            PutProfilePhotoResponse putProfilePhotoResponse = fileStorageClient.putProfilePhoto(
                    new PutProfilePhotoRequest(applicationUser.getId(), file.getBytes(), file.getOriginalFilename()));
            String profilePhotoPath = putProfilePhotoResponse.profilePhotoDirectoryAndName();

            applicationUser.setProfilePhoto(profilePhotoPath);
        } catch (IOException e) {
            throw new RuntimeException(messageSource.getMessage(
                    "exception.applicationUserAccount.updateProfilePhoto.notUploaded", null, Locale.getDefault()));
        }

        applicationUserRepository.save(applicationUser);
    }

    public void deleteProfilePhoto() {
        ApplicationUser applicationUser =
                (ApplicationUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        fileStorageClient.deletePhoto(new DeletePhotoRequest(applicationUser.getProfilePhoto()));

        applicationUser.setProfilePhoto(null);

        applicationUserRepository.save(applicationUser);
    }

    private void validatePhoto(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException(messageSource.getMessage(
                    "exception.applicationUserAccount.validatePhoto.emptyFile", null, Locale.getDefault()));
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
                    "exception.applicationUserAccount.validatePhoto.invalidExtension", null, Locale.getDefault());
            throw new IllegalArgumentException(message + " " + supportedExtensions);
        }
    }
}
