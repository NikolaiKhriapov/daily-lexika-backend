package my.project.models.dto.filestorage;

public record PutProfilePhotoRequest(
        Long userId,
        byte[] fileBytes,
        String originalFileName
) {
}