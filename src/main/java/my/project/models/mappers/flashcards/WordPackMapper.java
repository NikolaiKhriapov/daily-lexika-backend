package my.project.models.mappers.flashcards;

import my.project.models.dtos.flashcards.WordPackDto;
import my.project.models.entities.flashcards.Review;
import my.project.models.entities.flashcards.WordPack;
import my.project.repositories.flashcards.ReviewRepository;
import my.project.services.flashcards.WordDataService;
import my.project.services.user.AuthenticationService;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class WordPackMapper {

    @Autowired
    protected WordDataService wordDataService;
    @Autowired
    protected AuthenticationService authenticationService;
    @Autowired
    protected ReviewRepository reviewRepository;

    @Mapping(target = "totalWords", source = "entity", qualifiedByName = "mapTotalWords")
    @Mapping(target = "reviewId", source = "entity", qualifiedByName = "mapReviewId")
    public abstract WordPackDto toDto(WordPack entity);

    public abstract List<WordPackDto> toDtoList(List<WordPack> entityList);

    @Named("mapReviewId")
    protected Long mapReviewId(WordPack entity) {
        Long userId = authenticationService.getAuthenticatedUser().getId();
        Optional<Review> review = reviewRepository.findByUserIdAndWordPack_Name(userId, entity.getName());
        return review.map(Review::getId).orElse(null);
    }

    @Named("mapTotalWords")
    protected long mapTotalWords(WordPack entity) {
        return wordDataService.getAllWordDataIdByWordPackName(entity.getName()).size();
    }
}
