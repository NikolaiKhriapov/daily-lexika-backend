import { useEffect, useRef, useState } from 'react';
import { ColorMode, useColorMode, useDisclosure } from '@chakra-ui/react';
import styled from 'styled-components/macro';
import { errorNotification, successNotification } from '../../services/popup-notification';
import { getReview, getWordsForReview, refreshReview, removeReview } from '../../services/reviews';
import StartReviewWindow from './StartReviewWindow';
import { ReviewDTO, Status, WordDTO } from '../../utils/types';
import { ButtonType, FontWeight, Size } from '../../utils/constants';
import AlertDialog from '../common/complex/AlertDialog';
import Text from '../common/basic/Text';
import CloseButton from '../common/basic/CloseButton';
import CompletedIcon from '../common/basic/CompletedIcon';
import ButtonsContainer from '../common/complex/ButtonsContainer';
import { theme } from '../../utils/theme';
import { borderStyles } from '../../utils/functions';
import Button from '../common/basic/Button';

type Props = {
  reviewDTO: ReviewDTO;
  fetchAllReviewsDTO: any;
};

export default function ReviewCard(props: Props) {
  const { reviewDTO, fetchAllReviewsDTO } = props;

  const { colorMode } = useColorMode();
  const { isOpen: isOpenStartButton, onOpen: onOpenStartButton, onClose: onCloseStartButton } = useDisclosure();
  const { isOpen: isOpenRemoveButton, onOpen: onOpenRemoveButton, onClose: onCloseRemoveButton } = useDisclosure();
  const [wordsForReviewDTO, setWordsForReviewDTO] = useState<WordDTO[]>([]);
  const [updatedReviewDTO, setUpdatedReviewDTO] = useState(reviewDTO);
  const [reviewRemoved, setReviewRemoved] = useState(false);
  const [refreshed, setRefreshed] = useState(false);
  const cancelRef = useRef<HTMLButtonElement>(null);

  const isDateLastCompletedToday = () =>
    new Date(updatedReviewDTO.dateLastCompleted!).getDate() === new Date().getDate();

  const fetchWordsForReviewDTO = (reviewId: number) => {
    getWordsForReview(reviewId)
      .then((response) => setWordsForReviewDTO(response.data.data.wordsForReviewDTO))
      .catch((e) => errorNotification(e.code, e.response.data.message));
  };

  const fetchReviewDTO = (reviewId: number) => {
    getReview(reviewId)
      .then((response) => setUpdatedReviewDTO(response.data.data.reviewDTO))
      .catch((e) => errorNotification(e.code, e.response.data.message));
  };

  const requestRefreshReview = (reviewId: number) => {
    setRefreshed(true);
    refreshReview(reviewId)
      .then(() => fetchAllReviewsDTO())
      .catch((e) => errorNotification(e.code, e.response.data.message));
  };

  useEffect(() => {
    if (!reviewRemoved && !refreshed) {
      fetchWordsForReviewDTO(reviewDTO.id!);
      fetchReviewDTO(reviewDTO.id!);
    }
  }, [wordsForReviewDTO]);

  const handleRemoveReview = () => {
    setReviewRemoved(true);
    removeReview(reviewDTO.id!)
      .then(() => {
        successNotification(
          'Review removed successfully',
          `${reviewDTO.wordPackName} removed successfully`,
        );
        fetchAllReviewsDTO();
      })
      .catch((e) => errorNotification(e.code, e.response.data.message))
      .finally(() => onCloseRemoveButton());
  };

  const totalNewWords = wordsForReviewDTO.filter((wordDTO) => wordDTO.status.toString() === Status[Status.NEW]).length;
  const totalReviewWords = wordsForReviewDTO.filter((wordDTO) => wordDTO.status.toString() === Status[Status.IN_REVIEW] || wordDTO.status.toString() === Status[Status.KNOWN]).length;

  return (
    <Container colorMode={colorMode}>
      <CompletedIconAndCloseButtonContainer>
        <CompletedIconContainer>
          {isDateLastCompletedToday() && <CompletedIcon />}
        </CompletedIconContainer>
        <CloseButtonContainer>
          <CloseButton onClick={onOpenRemoveButton} />
          <AlertDialog
            isOpenDeleteButton={isOpenRemoveButton}
            onCloseDeleteButton={onCloseRemoveButton}
            cancelRef={cancelRef}
            handleDelete={handleRemoveReview}
            header='Remove Review'
            body={`Are you sure you want to delete daily review? You can't undo this action.`}
            deleteButtonText='Delete'
          />
        </CloseButtonContainer>
      </CompletedIconAndCloseButtonContainer>
      <WordPackNameContainer>
        <Text size={Size.XXL} fontWeight={FontWeight.SEMIBOLD}>{reviewDTO.wordPackName}</Text>
      </WordPackNameContainer>
      <WordsCountContainer>
        <WordsContainer>
          <Text size={Size.XXL}>{totalNewWords}</Text>
          <Text size={Size.SM}>{' New Words'}</Text>
        </WordsContainer>
        <WordsContainer>
          <Text size={Size.XXL}>{totalReviewWords}</Text>
          <Text size={Size.SM}>{' Review Words'}</Text>
        </WordsContainer>
      </WordsCountContainer>
      <ButtonsContainer>
        <Button
          buttonText={!isDateLastCompletedToday() ? 'Start' : 'Refresh'}
          buttonType={ButtonType.BUTTON}
          size={Size.SM}
          onClick={() => (!isDateLastCompletedToday() ? onOpenStartButton() : requestRefreshReview(reviewDTO.id!))}
        />
        {isOpenStartButton && (
          <StartReviewWindow
            reviewId={reviewDTO.id!}
            isOpen={isOpenStartButton}
            onClose={onCloseStartButton}
            totalReviewWords={reviewDTO.listOfWordId!.length}
          />
        )}
      </ButtonsContainer>
    </Container>
  );
}

const Container = styled.div<{ colorMode: ColorMode }>`
  height: 280px;
  width: calc(280px / 1.3);
  padding: 0 25px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  border: ${({ colorMode }) => borderStyles(colorMode)};
  border-radius: ${theme.stylesToDelete.borderRadius};
  background-color: ${({ colorMode }) => theme.colors[colorMode].bgColor};
`;

const CompletedIconAndCloseButtonContainer = styled.div`
  display: flex;
  justify-content: space-between;
`;

const CompletedIconContainer = styled.div`
  height: 10px;
  margin-top: -10px;
  margin-bottom: 10px;
  margin-left: -10px;
`;

const CloseButtonContainer = styled.div`
  margin-top: -15px;
  margin-bottom: 15px;
  margin-right: -15px;
`;

const WordPackNameContainer = styled.div`
  display: flex;
  justify-content: center;
`;

const WordsCountContainer = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: center;
  height: 140px;
`;

const WordsContainer = styled.div`
  display: flex;
  justify-content: center;
  align-items: baseline;
  gap: 4px;
`;
