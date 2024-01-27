package my.project.controllers.flashcards;

import com.fasterxml.jackson.core.type.TypeReference;
import my.project.models.dto.flashcards.ReviewDTO;
import my.project.repositories.flashcards.ReviewRepository;
import my.project.config.AbstractIntegrationTest;
import my.project.util.MockMvcService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;

import java.util.List;

import static my.project.models.entity.enumeration.Platform.CHINESE;
import static my.project.util.CommonConstants.TEST_EMAIL_CHINESE;
import static my.project.util.CommonConstants.URI_REVIEWS;
import static my.project.util.data.TestDataUtil.*;
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
        List<ReviewDTO> actual = mockMvcService
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
        List<ReviewDTO> actual = mockMvcService
                .performGet(URI_REVIEWS, status().isOk())
                .getResponse(new TypeReference<>() {});

        // Then
        assertThat(actual).asList().hasSize(1);
    }

    @Test
    void getReview() {
        // Given
        ReviewDTO expected = mockMvcService
                .performPost(URI_REVIEWS, generateReviewDTO(CHINESE), status().isCreated())
                .getResponse(ReviewDTO.class);

        // When
        ReviewDTO actual = mockMvcService
                .performGet(URI_REVIEWS + "/" + expected.id(), status().isOk())
                .getResponse(ReviewDTO.class);

        // Then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void createReview() {
        // Given
        ReviewDTO reviewDTO = generateReviewDTO(CHINESE);
        List<ReviewDTO> allReviewsBefore = mockMvcService
                .performGet(URI_REVIEWS, status().isOk())
                .getResponse(new TypeReference<>() {});

        // When
        mockMvcService.performPost(URI_REVIEWS, reviewDTO, status().isCreated());

        // Then
        List<ReviewDTO> allReviewsAfter = mockMvcService
                .performGet(URI_REVIEWS, status().isOk())
                .getResponse(new TypeReference<>() {});

        assertThat(allReviewsAfter).asList().hasSize(allReviewsBefore.size() + 1);
    }

    @Test
    void refreshReview() {
        // Given
        ReviewDTO reviewDTO = mockMvcService
                .performPost(URI_REVIEWS, generateReviewDTO(CHINESE), status().isCreated())
                .getResponse(ReviewDTO.class);

        // When
        mockMvcService.performPatch(URI_REVIEWS + "/" + reviewDTO.id(), status().isOk());

        // Then
        ReviewDTO reviewDTOAfterRefresh = mockMvcService
                .performGet(URI_REVIEWS + "/" + reviewDTO.id() + "/action?answer=true", status().isOk()) // TODO::: fix
                .performGet(URI_REVIEWS + "/" + reviewDTO.id(), status().isOk())
                .getResponse(ReviewDTO.class);

        assertThat(reviewDTOAfterRefresh.id()).isEqualTo(reviewDTO.id());
        assertThat(reviewDTOAfterRefresh.listOfWordDTO()).isNotEqualTo(reviewDTO.listOfWordDTO());
    }

    @Test
    void deleteReview() {
        // Given
        ReviewDTO reviewDTO = mockMvcService
                .performPost(URI_REVIEWS, generateReviewDTO(CHINESE), status().isCreated())
                .getResponse(ReviewDTO.class);
        List<ReviewDTO> allReviewsBefore = mockMvcService
                .performGet(URI_REVIEWS, status().isOk())
                .getResponse(new TypeReference<>() {});

        // When
        mockMvcService.performDelete(URI_REVIEWS + "/" + reviewDTO.id(), status().isNoContent());

        // Then
        List<ReviewDTO> allReviewsAfter = mockMvcService
                .performGet(URI_REVIEWS, status().isOk())
                .getResponse(new TypeReference<>() {});

        assertThat(allReviewsAfter).asList().hasSize(allReviewsBefore.size() - 1);
        assertThat(allReviewsAfter).asList().doesNotContain(reviewDTO);
    }

//    @ParameterizedTest
//    @CsvSource({"true, 1", "false, 0"})
//    void processReviewAction(Boolean answer, int reducedBy) {
//        // Given
//        ReviewDTO reviewDTO = mockMvcService
//                .performPost(URI_REVIEWS, generateReviewDTO(CHINESE), status().isCreated())
//                .getResponse(ReviewDTO.class);
//
//        // When
//        Map<String, Object> response = mockMvcService
//                .performGet(URI_REVIEWS + "/" + reviewDTO.id() + "/action?answer=" + answer, status().isOk())
//                .getResponse(new TypeReference<>() {});
//
//        // Then
//        if (response != null) {
//            assertThat(response.get("reviewUpdatedSize"))
//                    .isEqualTo(reviewDTO.listOfWordDTO().size() - reducedBy);
//            assertThat(response.get("reviewWordDTO"))
//                    .isNotNull()
//                    .asString().contains("id", "nameChineseSimplified", "nameChineseTraditional", "pinyin",
//                            "nameEnglish", "nameRussian", "status", "totalStreak");
//        }
//    }
}
