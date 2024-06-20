package my.project.dailylexika.controllers.flashcards;

import com.fasterxml.jackson.core.type.TypeReference;
import my.project.library.dailylexika.dtos.flashcards.ReviewDto;
import my.project.dailylexika.repositories.flashcards.ReviewRepository;
import my.project.dailylexika.config.AbstractIntegrationTest;
import my.project.dailylexika.util.MockMvcService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;

import java.util.List;

import static my.project.library.dailylexika.enumerations.Platform.CHINESE;
import static my.project.dailylexika.util.CommonConstants.TEST_EMAIL_CHINESE;
import static my.project.dailylexika.util.CommonConstants.URI_REVIEWS;
import static my.project.dailylexika.util.data.TestDataUtil.generateReviewDTO;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithUserDetails(TEST_EMAIL_CHINESE)
class ReviewControllerIT extends AbstractIntegrationTest {

    @Autowired
    private MockMvcService mockMvcService;
    @Autowired
    private ReviewRepository reviewRepository;

    @AfterEach
    void clean() {
        reviewRepository.deleteAllInBatch();
    }

    @Test
    void getAllReviews_shouldReturnZeroUponRegistration() {
        // Given
        // When
        List<ReviewDto> actual = mockMvcService
                .performGet(URI_REVIEWS, status().isOk())
                .getResponse(new TypeReference<>() {});

        // Then
        assertThat(actual).asList().hasSize(0);
    }

    @Test
    void getAllReviews_shouldReturnOneUponRegistrationAndCreatingReview() {
        // Given
        mockMvcService.performPost(URI_REVIEWS, generateReviewDTO(CHINESE), status().isCreated());

        // When
        List<ReviewDto> actual = mockMvcService
                .performGet(URI_REVIEWS, status().isOk())
                .getResponse(new TypeReference<>() {});

        // Then
        assertThat(actual).asList().hasSize(1);
    }

    @Test
    void createReview() {
        // Given
        ReviewDto reviewDTO = generateReviewDTO(CHINESE);
        List<ReviewDto> allReviewsBefore = mockMvcService
                .performGet(URI_REVIEWS, status().isOk())
                .getResponse(new TypeReference<>() {});

        // When
        mockMvcService.performPost(URI_REVIEWS, reviewDTO, status().isCreated());

        // Then
        List<ReviewDto> allReviewsAfter = mockMvcService
                .performGet(URI_REVIEWS, status().isOk())
                .getResponse(new TypeReference<>() {});

        assertThat(allReviewsAfter).asList().hasSize(allReviewsBefore.size() + 1);
    }

    @Test
    void refreshReview() {
        // Given
        ReviewDto reviewDTO = mockMvcService
                .performPost(URI_REVIEWS, generateReviewDTO(CHINESE), status().isCreated())
                .getResponse(ReviewDto.class);

        // When
        mockMvcService.performPatch(URI_REVIEWS + "/refresh/" + reviewDTO.id(), status().isOk());

        // Then
        ReviewDto reviewDtoAfterRefresh = mockMvcService
                .performGet(URI_REVIEWS + "/" + reviewDTO.id() + "/action?answer=true", status().isOk()) // TODO::: fix
                .getResponse(ReviewDto.class);

        assertThat(reviewDtoAfterRefresh.id()).isEqualTo(reviewDTO.id());
        assertThat(reviewDtoAfterRefresh.listOfWordDto()).isNotEqualTo(reviewDTO.listOfWordDto());
    }

    @Test
    void deleteReview() {
        // Given
        ReviewDto reviewDTO = mockMvcService
                .performPost(URI_REVIEWS, generateReviewDTO(CHINESE), status().isCreated())
                .getResponse(ReviewDto.class);
        List<ReviewDto> allReviewsBefore = mockMvcService
                .performGet(URI_REVIEWS, status().isOk())
                .getResponse(new TypeReference<>() {});

        // When
        mockMvcService.performDelete(URI_REVIEWS + "/" + reviewDTO.id(), status().isNoContent());

        // Then
        List<ReviewDto> allReviewsAfter = mockMvcService
                .performGet(URI_REVIEWS, status().isOk())
                .getResponse(new TypeReference<>() {});

        assertThat(allReviewsAfter).asList().hasSize(allReviewsBefore.size() - 1);
        assertThat(allReviewsAfter).asList().doesNotContain(reviewDTO);
    }

//    @ParameterizedTest
//    @CsvSource({"true, 1", "false, 0"})
//    void processReviewAction(Boolean answer, int reducedBy) {
//        // Given
//        ReviewDto reviewDTO = mockMvcService
//                .performPost(URI_REVIEWS, generateReviewDTO(CHINESE), status().isCreated())
//                .getResponse(ReviewDto.class);
//
//        // When
//        Map<String, Object> response = mockMvcService
//                .performGet(URI_REVIEWS + "/" + reviewDTO.id() + "/action?answer=" + answer, status().isOk())
//                .getResponse(new TypeReference<>() {});
//
//        // Then
//        if (response != null) {
//            assertThat(response.get("reviewUpdatedSize"))
//                    .isEqualTo(reviewDTO.WordDtoList().size() - reducedBy);
//            assertThat(response.get("reviewWordDTO"))
//                    .isNotNull()
//                    .asString().contains("id", "nameChinese", "nameChineseTraditional", "pinyin",
//                            "nameEnglish", "nameRussian", "status", "totalStreak");
//        }
//    }
}
