package my.project.services.filestorage;

import lombok.RequiredArgsConstructor;
import my.project.exception.ResourceNotFoundException;
import my.project.models.dto.filestorage.FileStorageProperties;
import my.project.models.dto.filestorage.*;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
@RequiredArgsConstructor
public class FileStorageService {

    private final FileStorageProperties fileStorageProperties;
    private final MessageSource messageSource;

    public byte[] getPhoto(String fileDirectoryAndName) {
        try {
            return Files.readAllBytes(Path.of(fileDirectoryAndName));
        } catch (IOException e) {
            throw new RuntimeException(messageSource.getMessage(
                    "exception.fileStorage.getPhoto.cannotRead", null, Locale.getDefault()));
        }
    }

    public String putProfilePhoto(PutProfilePhotoRequest putProfilePhotoRequest) {
        Long userId = putProfilePhotoRequest.userId();
        byte[] fileBytes = putProfilePhotoRequest.fileBytes();
        String originalFileName = putProfilePhotoRequest.originalFileName();

        String profilePhotoDirectory = fileStorageProperties.getProfilePhotoDirectory().formatted(userId);
        String profilePhotoName = fileStorageProperties.getProfilePhotoName().formatted(userId, getFileExtension(originalFileName));

        putPhoto(profilePhotoDirectory, profilePhotoName, fileBytes);

        return profilePhotoDirectory + profilePhotoName;
    }

    public void deletePhoto(String photo) {
        if (photo != null) {
            try {
                Path oldFileNameAndPath = Paths.get(photo);
                Files.delete(oldFileNameAndPath);
            } catch (IOException e) {
                throw new RuntimeException(messageSource.getMessage(
                        "exception.fileStorage.deletePhoto.notDeleted", null, Locale.getDefault()));
            }
        }
    }


    private void putPhoto(String photoDirectory, String photoName, byte[] fileBytes) {
        try {
            Files.createDirectories(Path.of(photoDirectory));
            Files.write(Path.of(photoDirectory + photoName), fileBytes);
        } catch (IOException e) {
            throw new ResourceNotFoundException(messageSource.getMessage(
                    "exception.fileStorage.putPhoto.notFound", null, Locale.getDefault()));
        }
    }

    private String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }
}
