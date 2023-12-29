package my.project.models.dto.filestorage;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "file-storage")
@Getter
@Setter
public class FileStorageProperties {

    private String profilePhotoDirectory;
    private String profilePhotoName;
}
