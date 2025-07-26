package my.project.dailylexika.flashcard.model.mappers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import my.project.dailylexika.config.I18nUtil;
import my.project.dailylexika.flashcard.model.entities.Review;
import my.project.dailylexika.flashcard.model.entities.WordPack;
import my.project.dailylexika.flashcard.persistence.ReviewRepository;
import my.project.dailylexika.flashcard.persistence.WordDataRepository;
import my.project.dailylexika.flashcard.persistence.WordRepository;
import my.project.dailylexika.user._public.PublicUserService;
import my.project.dailylexika.user.service.RoleService;
import my.project.library.dailylexika.dtos.user.UserDto;
import my.project.library.dailylexika.enumerations.Platform;
import my.project.library.dailylexika.enumerations.Status;
import my.project.library.util.exception.InternalServerErrorException;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@AllArgsConstructor
public class WordDataMapperHelper {

    @Named("mapExamples")
    public List<Map<String, String>> mapExamples(String examples) {
        if (examples.equals("[TODO]")) return null;

        try {
            return new ObjectMapper().readValue(examples, List.class);
        } catch (Exception e) {
            throw new InternalServerErrorException(I18nUtil.getMessage("dailylexika-exceptions.excel.parse", examples));
        }
    }

    @Named("mapListOfWordPackNames")
    public List<String> mapListOfWordPacks(List<WordPack> wordPacks) {
        return wordPacks.stream()
                .map(WordPack::getName)
                .toList();
    }
}
