package my.project.clients.filestorage;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("file-storage")
public interface FileStorageClient {

    @PostMapping("api/v1/file-storage/get-photo")
    GetPhotoResponse getPhoto(@RequestBody GetPhotoRequest getPhotoRequest);

    @PostMapping("api/v1/file-storage/put-profile-photo")
    PutProfilePhotoResponse putProfilePhoto(@RequestBody PutProfilePhotoRequest putProfilePhotoRequest);

    @PostMapping("api/v1/file-storage/delete-photo")
    void deletePhoto(@RequestBody DeletePhotoRequest deletePhotoRequest);
}
