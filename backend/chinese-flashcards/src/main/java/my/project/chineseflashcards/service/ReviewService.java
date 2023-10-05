package my.project.chineseflashcards.service;

import lombok.RequiredArgsConstructor;
import my.project.chineseflashcards.exception.ResourceNotFoundException;
import my.project.chineseflashcards.exception.ReviewAlreadyExistsException;
import my.project.chineseflashcards.model.dto.ReviewStatisticsDTO;
import my.project.chineseflashcards.model.mapper.ReviewMapper;
import my.project.chineseflashcards.model.dto.ReviewDTO;
import my.project.chineseflashcards.model.entity.*;
import my.project.chineseflashcards.model.dto.WordDTO;
import my.project.chineseflashcards.model.mapper.WordMapper;
import my.project.chineseflashcards.repository.ReviewRepository;
import my.project.chineseflashcards.repository.WordRepository;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.DAYS;
import static my.project.chineseflashcards.model.entity.Status.*;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final WordMapper wordMapper;
    private final WordPackService wordPackService;
    private final WordRepository wordRepository;
    private final MessageSource messageSource;

    @Transactional
    public List<ReviewDTO> getAllReviews(Long userId) {
        List<Review> allReviews = reviewRepository.findAllByUserId(userId);

        List<ReviewDTO> allReviewDTOs = new ArrayList<>();
        for (Review oneReview : allReviews) {
            if (!Objects.equals(oneReview.getDateGenerated(), LocalDate.now())) {
                reviewRepository.delete(oneReview);
                reviewRepository.save(generateReview(reviewMapper.toDTO(oneReview), userId));
            }
            allReviewDTOs.add(reviewMapper.toDTO(oneReview));
        }

        return allReviewDTOs;
    }

    public ReviewDTO getReviewById(Long reviewId) {
        return reviewMapper.toDTO(getReview(reviewId));
    }

    public void createReview(ReviewDTO newReviewDTO, Long userId) {
        List<Review> existingReviews = reviewRepository.findAllByUserId(userId);
        if (existingReviews
                .stream().map(review -> review.getWordPack().getName()).toList()
                .contains(newReviewDTO.wordPackName())) {
            throw new ReviewAlreadyExistsException("Review '" + newReviewDTO.wordPackName() + "' already exists");
        }

        Review newReview = generateReview(newReviewDTO, userId);
        reviewRepository.save(newReview);
    }

    @Transactional
    public void refreshReview(Long userId, Long reviewId) {
        Review review = getReview(reviewId);

        List<Word> listOfWords = generateListOfWordsForReview(userId, review.getWordPack(), reviewMapper.toDTO(review));

        review.setDateLastCompleted(null); // TODO::: fix this workaround
        review.setListOfWords(listOfWords);
        reviewRepository.save(review);
    }

    public void deleteReview(Long reviewId) {
        reviewRepository.delete(getReview(reviewId));
    }

    @Transactional
    public Map<String, Object> processReviewAction(Long reviewId, String answer) {
        Review review = getReview(reviewId);
        if (answer != null) {
            List<Word> listOfWords = new ArrayList<>(review.getListOfWords());

            Word thisWord = listOfWords.get(0);
            thisWord.setOccurrence(thisWord.getOccurrence() + 1);

            if (answer.equals("yes")) {
                updateWordForYesAnswer(thisWord, listOfWords);
            }
            if (answer.equals("no")) {
                updateWordForNoAnswer(thisWord, listOfWords);
            }

            review.setListOfWords(listOfWords);
            review = reviewRepository.save(review);
        }

        WordDTO reviewWordDTO = showOneReviewWord(review);

        Map<String, Object> map = null;
        if (reviewWordDTO != null) {
            map = new HashMap<>();
            map.put("reviewWordDTO", reviewWordDTO);
            map.put("reviewUpdatedSize", review.getListOfWords().size());
        }

        return map;
    }

    public List<WordDTO> getAllWordsForReview(Long reviewId) {
        Review review = getReview(reviewId);
        return wordMapper.toDTOShortList(review.getListOfWords());
    }

    public ReviewStatisticsDTO getReviewStatistics(Long userId, Long reviewId) {
        List<WordData> listOfWordData = getReview(reviewId).getWordPack().getListOfWordData();

        List<Long> wordDataIds = listOfWordData.stream()
                .map(WordData::getId)
                .toList();
        long newWords = wordRepository.findByUserIdAndWordIdIn(userId, wordDataIds).stream()
                .filter(word -> word.getStatus().equals(NEW))
                .count();
        long reviewWords = wordRepository.findByUserIdAndWordIdIn(userId, wordDataIds).stream()
                .filter(word -> word.getStatus().equals(IN_REVIEW))
                .count();
        long knownWords = wordRepository.findByUserIdAndWordIdIn(userId, wordDataIds).stream()
                .filter(word -> word.getStatus().equals(KNOWN))
                .count();

        return new ReviewStatisticsDTO(
                (int) newWords,
                (int) reviewWords,
                (int) knownWords,
                listOfWordData.size()
        );
    }

    /**
     * // Answering in Daily Reviews
     * When NEW –> YES –> KNOWN
     * When NEW -> NO  -> IN_REVIEW
     * When KNOWN –> YES -> KNOWN
     * When KNOWN -> NO  -> IN_REVIEW
     * When IN_REVIEW(0-3) -> YES -> IN_REVIEW(1-4)
     * When IN_REVIEW(4) -> YES -> KNOWN
     * <p>
     * // Generating a Daily Review
     * First, the NEW words are added in the amount of review.maxNewWordsPerDay
     * Then, the IN_REVIEW words are added in the amount of review.maxReviewWordsPerDay * 0.7
     * Then, the KNOWN words are added in the amount of review.maxReviewWordsPerDay * 0.3
     * When it is not enough of the IN_REVIEW words, it is compensated by the KNOWN words, and vice versa
     * <p>
     * IN_REVIEW -> if dateOfLastOccurrence >= totalStreak x2
     * KNOWN -> if dateOfLastOccurrence >= totalStreak
     **/
    public List<Word> generateListOfWordsForReview(Long userId, WordPack wordPack, ReviewDTO reviewDTO) {
        List<Long> wordDataIds = wordPack.getListOfWordData().stream()
                .map(WordData::getId)
                .collect(Collectors.toList());
        List<Word> tempListOfWords = wordRepository.findByUserIdAndWordIdIn(userId, wordDataIds);

        List<Word> newWords = tempListOfWords.stream()
                .filter(word -> word.getStatus().equals(Status.NEW))
                .limit(reviewDTO.maxNewWordsPerDay())
                .toList();
        List<Word> reviewWords = tempListOfWords.stream()
                .filter(word -> word.getStatus().equals(Status.IN_REVIEW))
                .filter(word -> DAYS.between(word.getDateOfLastOccurrence(), LocalDate.now())
                        >= word.getTotalStreak() * 2)
                .sorted(Comparator.comparing(Word::getDateOfLastOccurrence).reversed())
                .limit(reviewDTO.maxReviewWordsPerDay())
                .toList();
        List<Word> knownWords = tempListOfWords.stream()
                .filter(word -> word.getStatus().equals(Status.KNOWN))
                .filter(word -> DAYS.between(word.getDateOfLastOccurrence(), LocalDate.now())
                        >= word.getTotalStreak())
                .sorted(Comparator.comparing(Word::getDateOfLastOccurrence).reversed())
                .limit(reviewDTO.maxReviewWordsPerDay())
                .toList();

        int totalReviewWords = reviewWords.size();
        int totalKnownWords = knownWords.size();

        boolean totalReviewWordsIsEnough = totalReviewWords > reviewDTO.maxReviewWordsPerDay() * 0.7;
        boolean totalKnownWordsIsEnough = totalKnownWords > reviewDTO.maxReviewWordsPerDay() * 0.3;

        int selectedReviewWords;
        int selectedKnownWords;

        if (totalReviewWordsIsEnough && totalKnownWordsIsEnough) {
            selectedReviewWords = (int) (reviewDTO.maxReviewWordsPerDay() * 0.7);
            selectedKnownWords = reviewDTO.maxReviewWordsPerDay() - selectedReviewWords;
        } else if (totalReviewWordsIsEnough) {
            selectedKnownWords = totalKnownWords;
            selectedReviewWords = reviewDTO.maxReviewWordsPerDay() - selectedKnownWords;
        } else if (totalKnownWordsIsEnough) {
            selectedReviewWords = totalReviewWords;
            selectedKnownWords = reviewDTO.maxReviewWordsPerDay() - selectedReviewWords;
        } else {
            selectedReviewWords = totalReviewWords;
            selectedKnownWords = totalKnownWords;
        }

        List<Word> listOfWords = new ArrayList<>();
        listOfWords.addAll(newWords);
        listOfWords.addAll(reviewWords.stream().limit(selectedReviewWords).toList());
        listOfWords.addAll(knownWords.stream().limit(selectedKnownWords).toList());

        listOfWords.forEach(word -> {
            word.setOccurrence(0);
            word.setCurrentStreak(0);
        });

        return listOfWords;
    }

    public void deleteAllByUserId(Long userId) {
        reviewRepository.deleteAllByUserId(userId);
    }

    private Review getReview(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException(messageSource.getMessage(
                        "exception.review.notFound", null, Locale.getDefault())));
    }

    private WordDTO showOneReviewWord(Review review) {
        if (!review.getListOfWords().isEmpty()) {
            Word word = review.getListOfWords().get(0);
            return wordMapper.toDTO(word);
        }
        review.setDateLastCompleted(LocalDate.now());
        return null;
    }

    private Review generateReview(ReviewDTO reviewDTO, Long userId) {
        WordPack wordPack = wordPackService.getWordPackByName(reviewDTO.wordPackName());
        List<Word> listOfWords = generateListOfWordsForReview(userId, wordPack, reviewDTO);

        return new Review(
                userId,
                reviewDTO.maxNewWordsPerDay(),
                reviewDTO.maxReviewWordsPerDay(),
                wordPack,
                listOfWords
        );
    }

    private void updateWordForYesAnswer(Word thisWord, List<Word> listOfWords) {
        if (thisWord.getStatus().equals(NEW) || thisWord.getStatus().equals(KNOWN)) {
            thisWord.setStatus(KNOWN);
            thisWord.setCurrentStreak(0);
            thisWord.setOccurrence(0);
            thisWord.setDateOfLastOccurrence(LocalDate.now());
            listOfWords.remove(thisWord);
        }

        if (thisWord.getStatus().equals(IN_REVIEW)) {
            if (thisWord.getCurrentStreak() < 3) {
                thisWord.setCurrentStreak(thisWord.getCurrentStreak() + 1);
            }
            if (thisWord.getCurrentStreak() == 3) {
                thisWord.setTotalStreak(thisWord.getTotalStreak() + 1);
                if (thisWord.getTotalStreak() >= 5) {
                    thisWord.setStatus(KNOWN);
                }
                thisWord.setCurrentStreak(0);
                thisWord.setOccurrence(0);
                thisWord.setDateOfLastOccurrence(LocalDate.now());
                listOfWords.remove(thisWord);
            }
        }

        Collections.rotate(listOfWords, -1);
    }

    private void updateWordForNoAnswer(Word thisWord, List<Word> listOfWords) {
        if (thisWord.getStatus().equals(NEW)) {
            thisWord.setStatus(IN_REVIEW);
        }

        thisWord.setTotalStreak(0);
        thisWord.setCurrentStreak(0);
        thisWord.setStatus(IN_REVIEW);
        listOfWords.remove(0);
        listOfWords.add(Math.min(listOfWords.size(), 3), thisWord);
    }
}
