package my.project.dailylexika.flashcard.model.mappers;

import my.project.library.dailylexika.dtos.flashcards.WordPackDto;
import my.project.library.dailylexika.enumerations.Platform;
import my.project.library.dailylexika.enumerations.Status;
import my.project.dailylexika.flashcard.model.entities.Review;
import my.project.dailylexika.flashcard.model.entities.WordPack;
import my.project.dailylexika.user.model.entities.User;
import my.project.dailylexika.flashcard.persistence.ReviewRepository;
import my.project.dailylexika.flashcard.service.WordDataService;
import my.project.dailylexika.flashcard.service.WordService;
import my.project.dailylexika.user.service.RoleService;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class WordPackMapper {

    @Autowired
    protected WordDataService wordDataService;
    @Autowired
    protected WordService wordService;
    @Autowired
    protected RoleService roleService;
    @Autowired
    protected ReviewRepository reviewRepository;

    @Mapping(target = "wordsTotal", source = "entity", qualifiedByName = "mapWordsTotal")
    @Mapping(target = "wordsNew", source = "entity", qualifiedByName = "mapWordsNew")
    @Mapping(target = "reviewId", source = "entity", qualifiedByName = "mapReviewId")
    public abstract WordPackDto toDto(WordPack entity);

    public abstract List<WordPackDto> toDtoList(List<WordPack> entityList);

    @Named("mapReviewId")
    protected Long mapReviewId(WordPack entity) {
        Integer userId = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        Optional<Review> review = reviewRepository.findByUserIdAndWordPack_Name(userId, entity.getName());
        return review.map(Review::getId).orElse(null);
    }

    @Named("mapWordsTotal")
    protected Long mapWordsTotal(WordPack entity) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Platform platform = roleService.getPlatformByRoleName(user.getRole());

        return wordDataService.countByWordPackNameAndPlatform(entity.getName(), platform);
    }

    @Named("mapWordsNew")
    protected Long mapWordsNew(WordPack entity) {
        return wordService.countByWordPackNameAndStatusForUser(entity.getName(), Status.NEW);
    }
}
