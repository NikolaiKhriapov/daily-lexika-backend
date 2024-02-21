package my.project.services.user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import my.project.models.dto.user.PasswordUpdateRequest;
import my.project.models.entity.enumeration.Platform;
import my.project.models.entity.user.RoleStatistics;
import my.project.models.entity.user.User;
import my.project.models.dto.user.UserDTO;
import my.project.models.mapper.user.UserMapper;
import my.project.repositories.user.UserRepository;
import my.project.services.flashcards.ReviewService;
import my.project.services.flashcards.WordPackService;
import my.project.services.flashcards.WordService;
import my.project.services.notification.NotificationService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserAccountService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final ReviewService reviewService;
    private final WordService wordService;
    private final WordPackService wordPackService;
    private final RoleService roleService;
    private final NotificationService notificationService;

    public UserDTO getUserInfo() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userMapper.toDTO(user);
    }

    @Transactional
    public UserDTO updateUserInfo(UserDTO userDTO) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (userDTO.name() != null && !Objects.equals(userDTO.name(), user.getName())) {
            user.setName(userDTO.name());
        }
        if (userDTO.email() != null && !Objects.equals(userDTO.email(), user.getEmail())) {
            user.setEmail(userDTO.email());
        }

        return userMapper.toDTO(userRepository.save(user));
    }

    @Transactional
    public void updatePassword(PasswordUpdateRequest request) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        boolean passwordsMatch = passwordEncoder.matches(request.passwordCurrent(), user.getPassword());

        if (passwordsMatch) {
            user.setPassword(passwordEncoder.encode(request.passwordNew()));
            userRepository.save(user);
        } else {
            throw new IllegalStateException("Incorrect password");
        }
    }

    @Transactional
    public void deleteAccount() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        RoleStatistics currentRole = roleService.getRoleStatistics();
        Platform platform = roleService.getPlatformByRoleName(currentRole.getRoleName());

        deleteFlashcardsForUserByPlatform(user, platform);
        user.getRoleStatistics().remove(currentRole);

        if (user.getRoleStatistics().isEmpty()) {
            notificationService.deleteAllByUserId(user.getId());
            userRepository.delete(user);
        } else {
            userRepository.save(user);
        }
    }

    private void deleteFlashcardsForUserByPlatform(User user, Platform platform) {
        reviewService.deleteAllByUserIdAndPlatform(user.getId(), platform);
        wordService.deleteAllByUserIdAndPlatform(user.getId(), platform);
        wordPackService.deleteAllByUserIdAndPlatform(user.getId(), platform);
    }
}

//    public byte[] getProfilePhoto() {
//        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//
//        if (StringUtils.isBlank(user.getProfilePhoto())) {
//            return new byte[]{};
//        }
//
//        GetPhotoResponse getPhotoResponse = fileStorageClient.getPhoto(new GetPhotoRequest(user.getProfilePhoto()));
//        return getPhotoResponse.photo();
//    }

//    @Transactional
//    public void updateProfilePhoto(MultipartFile file) {
//        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//
//        validatePhoto(file);
//
//        try {
//            deleteProfilePhoto();
//
//            PutProfilePhotoResponse putProfilePhotoResponse = fileStorageClient.putProfilePhoto(
//                    new PutProfilePhotoRequest(user.getId(), file.getBytes(), file.getOriginalFilename()));
//            String profilePhotoPath = putProfilePhotoResponse.profilePhotoDirectoryAndName();
//
//            user.setProfilePhoto(profilePhotoPath);
//        } catch (IOException e) {
//            throw new RuntimeException(messageSource.getMessage(
//                    "exception.userAccount.updateProfilePhoto.notUploaded", null, Locale.getDefault()));
//        }
//
//        userRepository.save(user);
//    }

//    @Transactional
//    public void deleteProfilePhoto() {
//        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//
//        fileStorageClient.deletePhoto(new DeletePhotoRequest(user.getProfilePhoto()));
//
//        user.setProfilePhoto(null);
//
//        userRepository.save(user);
//    }

//    private void validatePhoto(MultipartFile file) {
//        if (file.isEmpty()) {
//            throw new IllegalArgumentException(messageSource.getMessage(
//                    "exception.userAccount.validatePhoto.emptyFile", null, Locale.getDefault()));
//        }
//
//        long maxSize = 5 * 1024 * 1024; // 5 MB
//        if (file.getSize() > maxSize) {
//            throw new MaxUploadSizeExceededException(maxSize);
//        }
//
//        Set<String> supportedExtensions = new HashSet<>(
//                List.of(messageSource.getMessage("profilePhoto.supportedExtensions", null, Locale.getDefault()).split(","))
//        );
//        boolean isAllowedExtension = false;
//        for (String extension : supportedExtensions) {
//            if (Objects.requireNonNull(file.getOriginalFilename()).toLowerCase().endsWith(extension)) {
//                isAllowedExtension = true;
//                break;
//            }
//        }
//        if (!isAllowedExtension) {
//            String message = messageSource.getMessage(
//                    "exception.userAccount.validatePhoto.invalidExtension", null, Locale.getDefault());
//            throw new IllegalArgumentException(message + " " + supportedExtensions);
//        }
//    }
