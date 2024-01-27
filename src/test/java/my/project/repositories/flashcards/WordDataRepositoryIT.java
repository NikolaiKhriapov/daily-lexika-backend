package my.project.repositories.flashcards;

import my.project.config.AbstractIntegrationTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class WordDataRepositoryIT extends AbstractIntegrationTest {

    @Autowired
    private WordDataRepository underTest;

    @ParameterizedTest
    @MethodSource("my.project.util.data.TestDataSource#findAllWordDataIdsByWordPackName")
    void findAllWordDataIdsByWordPackName(String input, int expectedSize) {
        // Given

        // When
        List<Long> actual = underTest.findAllWordDataIdsByWordPackName(input);

        // Then
        assertThat(actual).asList().hasSize(expectedSize);
    }
}
