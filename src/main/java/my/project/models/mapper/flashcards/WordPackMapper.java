package my.project.models.mapper.flashcards;

import lombok.RequiredArgsConstructor;
import my.project.models.dto.flashcards.WordPackDTO;
import my.project.models.entity.flashcards.Review;
import my.project.models.entity.flashcards.WordPack;
import my.project.models.mapper.Mapper;
import my.project.repositories.flashcards.ReviewRepository;
import my.project.services.flashcards.WordDataService;
import my.project.services.user.AuthenticationService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WordPackMapper implements Mapper<WordPack, WordPackDTO> {

    private final WordDataService wordDataService;
    private final ReviewRepository reviewRepository;
    private final AuthenticationService authenticationService;

    @Override
    public WordPackDTO toDTO(WordPack entity) {
        Long userId = authenticationService.getAuthenticatedUser().getId();
        Optional<Review> review = reviewRepository.findByUserIdAndWordPackName(userId, entity.getName());
        Long reviewId = review.map(Review::getId).orElse(null);

        return new WordPackDTO(
                entity.getName(),
                entity.getDescription(),
                entity.getCategory(),
                (long) wordDataService.getListOfAllWordDataIdsByWordPackName(entity.getName()).size(),
                reviewId
        );
    }
}
