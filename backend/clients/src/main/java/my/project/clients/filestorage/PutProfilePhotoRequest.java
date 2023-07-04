package my.project.clients.filestorage;

public record PutProfilePhotoRequest(
        Long applicationUserId,
        byte[] fileBytes,
        String originalFileName
) {
}