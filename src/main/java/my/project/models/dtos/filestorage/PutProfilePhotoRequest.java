package my.project.models.dtos.filestorage;

public record PutProfilePhotoRequest(
        Long userId,
        byte[] fileBytes,
        String originalFileName
) {
}