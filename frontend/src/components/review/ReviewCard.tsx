import { useEffect, useRef, useState } from 'react';
import { Flex, useColorMode, useDisclosure } from '@chakra-ui/react';
import { errorNotification, successNotification } from '../../services/popup-notification';
import { getReview, getWordsForReview, refreshReview, removeReview } from '../../services/reviews';
import StartReviewWindow from './StartReviewWindow';
import { ReviewDTO, Status, WordDTO } from '../../types/types';
import { ButtonType, TextSize } from '../../utils/constants';
import AlertDialog from '../common/complex/AlertDialog';
import Button from '../common/basic/Button';
import Text from '../common/basic/Text';
import Heading from '../common/basic/Heading';
import CloseButton from '../common/basic/CloseButton';
import CompletedIcon from '../common/basic/CompletedIcon';

interface ReviewCardProps {
  reviewDTO: ReviewDTO;
  fetchAllReviewsDTO: any;
}

function ReviewCard(props: ReviewCardProps) {
  const { reviewDTO, fetchAllReviewsDTO } = props;

  const { colorMode } = useColorMode();
  const [wordsForReviewDTO, setWordsForReviewDTO] = useState<WordDTO[]>([]);
  const [updatedReviewDTO, setUpdatedReviewDTO] = useState(reviewDTO);
  const [reviewRemoved, setReviewRemoved] = useState(false);
  const [refreshed, setRefreshed] = useState(false);
  const { isOpen: isOpenRemoveButton, onOpen: onOpenRemoveButton, onClose: onCloseRemoveButton } = useDisclosure();
  const { isOpen: isOpenStartButton, onOpen: onOpenStartButton, onClose: onCloseStartButton } = useDisclosure();
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

  const startButton = (
    <Button
      content={!isDateLastCompletedToday() ? 'Start' : 'Refresh'}
      onClick={() => (!isDateLastCompletedToday() ? onOpenStartButton() : requestRefreshReview(reviewDTO.id!))}
      type={ButtonType.BUTTON}
    />
  );

  return (
    <Flex className={`ReviewCard_container ${colorMode}`}>
      <Flex className='completedIconAndCloseButton_container'>
        <Flex className='completedIcon_container'>
          {isDateLastCompletedToday() && <CompletedIcon />}
        </Flex>
        <Flex className='closeButton_container'>
          <CloseButton onClick={onOpenRemoveButton} />
          <AlertDialog
            isOpenDeleteButton={isOpenRemoveButton}
            onCloseDeleteButton={onCloseRemoveButton}
            cancelRef={cancelRef}
            handleDelete={handleRemoveReview}
            header='Remove Review'
            body={`Are you sure you want to delete account? You can't undo this action.`}
            deleteButtonText='Delete'
          />
        </Flex>
      </Flex>
      <Flex className='wordPackName_container'>
        <Heading level={3} text={reviewDTO.wordPackName} />
      </Flex>
      <Flex className='wordsCount_container'>
        <Flex className='words_container'>
          <Text size={TextSize.XL} text={totalNewWords} isBold />
          <Text size={TextSize.SMALL} text=' New Words' isBold />
        </Flex>
        <Flex className='words_container'>
          <Text size={TextSize.XL} text={totalReviewWords} isBold />
          <Text size={TextSize.SMALL} text=' Review Words' isBold />
        </Flex>
      </Flex>
      <Flex className='buttons_container'>
        <StartReviewWindow
          reviewId={reviewDTO.id!}
          isOpen={isOpenStartButton}
          onClose={onCloseStartButton}
          button={startButton}
          totalReviewWords={reviewDTO.listOfWordId!.length}
        />
      </Flex>
    </Flex>
  );
}

export default ReviewCard;
