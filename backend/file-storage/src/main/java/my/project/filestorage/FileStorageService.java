package my.project.filestorage;

import lombok.RequiredArgsConstructor;
import my.project.clients.filestorage.*;
import my.project.filestorage.exception.ResourceNotFoundException;
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

    public GetPhotoResponse getPhoto(GetPhotoRequest getPhotoRequest) {
        try {
            byte[] file =  Files.readAllBytes(Path.of(getPhotoRequest.fileDirectoryAndName()));
            return new GetPhotoResponse(file);
        } catch (IOException e) {
            throw new RuntimeException(messageSource.getMessage(
                    "exception.fileStorage.getPhoto.cannotRead", null, Locale.getDefault()));
        }
    }

    public PutProfilePhotoResponse putProfilePhoto(PutProfilePhotoRequest putProfilePhotoRequest) {
        Long applicationUserId = putProfilePhotoRequest.applicationUserId();
        byte[] fileBytes = putProfilePhotoRequest.fileBytes();
        String originalFileName = putProfilePhotoRequest.originalFileName();

        String profilePhotoDirectory = fileStorageProperties.getProfilePhotoDirectory().formatted(applicationUserId);
        String profilePhotoName = fileStorageProperties.getProfilePhotoName().formatted(applicationUserId, getFileExtension(originalFileName));

        putPhoto(profilePhotoDirectory, profilePhotoName, fileBytes);

        return new PutProfilePhotoResponse(profilePhotoDirectory + profilePhotoName);
    }

    public void deletePhoto(DeletePhotoRequest deletePhotoRequest) {
        if (deletePhotoRequest.photo() != null) {
            try {
                Path oldFileNameAndPath = Paths.get(deletePhotoRequest.photo());
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
