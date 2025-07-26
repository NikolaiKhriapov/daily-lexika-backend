package my.project.dailylexika.flashcard.model.mappers;

import lombok.AllArgsConstructor;
import my.project.dailylexika.flashcard.model.entities.Review;
import my.project.dailylexika.flashcard.model.entities.WordPack;
import my.project.dailylexika.flashcard.persistence.ReviewRepository;
import my.project.dailylexika.flashcard.persistence.WordDataRepository;
import my.project.dailylexika.flashcard.persistence.WordRepository;
import my.project.dailylexika.user._public.PublicRoleService;
import my.project.dailylexika.user._public.PublicUserService;
import my.project.dailylexika.user.service.RoleService;
import my.project.library.dailylexika.dtos.user.UserDto;
import my.project.library.dailylexika.enumerations.Platform;
import my.project.library.dailylexika.enumerations.Status;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
public class WordPackMapperHelper {

    private final PublicUserService userService;
    private final PublicRoleService roleService;
    private final ReviewRepository reviewRepository; // TODO::: refactor
    private final WordDataRepository wordDataRepository; // TODO::: refactor
    private final WordRepository wordRepository; // TODO::: refactor

    @Named("mapReviewId")
    public Long mapReviewId(WordPack entity) {
        Integer userId = userService.getUser().id();
        Optional<Review> review = reviewRepository.findByUserIdAndWordPack_Name(userId, entity.getName());
        return review.map(Review::getId).orElse(null);
    }

    @Named("mapWordsTotal")
    public Long mapWordsTotal(WordPack entity) {
        UserDto user = userService.getUser();
        Platform platform = roleService.getPlatformByRoleName(user.role());
        return wordDataRepository.countByListOfWordPacks_NameAndPlatform(entity.getName(), platform);
    }

    @Named("mapWordsNew")
    public Long mapWordsNew(WordPack entity) {
        UserDto user = userService.getUser();
        Platform platform = roleService.getPlatformByRoleName(user.role());
        List<Integer> wordDataIds = wordDataRepository.findAllWordDataIdsByWordPackNameAndPlatform(entity.getName(), platform);
        return (long) wordRepository.countByUserIdAndWordData_IdInAndStatus(user.id(), wordDataIds, Status.NEW);
    }
}
