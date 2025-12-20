package my.project.dailylexika.flashcard.api;

import my.project.dailylexika.flashcard.persistence.ReviewRepository;
import my.project.dailylexika.config.AbstractIntegrationTest;
import my.project.dailylexika.util.MockMvcService;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;

import static my.project.dailylexika.util.CommonConstants.TEST_EMAIL_CHINESE;

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
}
