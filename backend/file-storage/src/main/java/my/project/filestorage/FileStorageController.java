package my.project.filestorage;

import lombok.RequiredArgsConstructor;
import my.project.clients.filestorage.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/file-storage")
public class FileStorageController {

    private final FileStorageService fileStorageService;

    @PostMapping("/get-photo")
    public GetPhotoResponse getPhoto(@RequestBody GetPhotoRequest getPhotoRequest) {
        return fileStorageService.getPhoto(getPhotoRequest);
    }

    @PostMapping("/put-profile-photo")
    public PutProfilePhotoResponse putProfilePhoto(@RequestBody PutProfilePhotoRequest putProfilePhotoRequest) {
        return fileStorageService.putProfilePhoto(putProfilePhotoRequest);
    }

    @PostMapping("/delete-photo")
    public void deletePhoto(@RequestBody DeletePhotoRequest deletePhotoRequest) {
        fileStorageService.deletePhoto(deletePhotoRequest);
    }
}
