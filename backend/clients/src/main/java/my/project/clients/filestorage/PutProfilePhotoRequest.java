package my.project.clients.filestorage;

public record PutProfilePhotoRequest(
        Long userId,
        byte[] fileBytes,
        String originalFileName
) {
}