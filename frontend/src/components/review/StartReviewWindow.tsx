import { useEffect, useState } from 'react';
import { Flex } from '@chakra-ui/react';
import { processReviewAction } from '../../services/reviews';
import { updateUserStreak } from '../../services/user';
import { errorNotification, successNotification } from '../../services/popup-notification';
import ReviewWordCard from './ReviewWordCard';
import { Status, WordDTO } from '../../types/types';
import { ButtonSize, ButtonType } from '../../utils/constants';
import Modal from '../common/complex/Modal';
import Button from '../common/basic/Button';
import ProgressBar from '../common/basic/ProgressBar';

interface StartReviewWindowProps {
  reviewId: number;
  isOpen: boolean;
  onClose: any;
  button: any;
  totalReviewWords: number;
}

function StartReviewWindow(props: StartReviewWindowProps) {
  const { reviewId, isOpen, onClose, button, totalReviewWords } = props;

  const [reviewWordDTO, setReviewWordDTO] = useState<WordDTO | null>(null);
  const [reviewUpdatedSize, setReviewUpdatedSize] = useState<number>(0);
  const [isFormVisible, setIsFormVisible] = useState(true);
  const [isReviewComplete, setIsReviewComplete] = useState(false);
  const [isFlipped, setIsFlipped] = useState(false);

  const fetchReviewAction = (answer: string | null) => {
    if ((answer === 'yes' && reviewWordDTO !== null && (reviewWordDTO.status.toString() === Status[Status.NEW]
      || (reviewWordDTO.status.toString() === Status[Status.IN_REVIEW] && reviewWordDTO.totalStreak === 4)))) {
      successNotification(
        `${reviewWordDTO.nameChineseSimplified} is a known word.`,
        'This word will still be shown occasionally during reviews',
      );
    }
    if (answer === 'no' && reviewWordDTO !== null && (reviewWordDTO.status.toString() === Status[Status.KNOWN])) {
      successNotification(
        `Keep reviewing ${reviewWordDTO.nameChineseSimplified}`,
        'This word will be shown more frequently so that you can relearn it',
      );
    }
    processReviewAction(reviewId, answer)
      .then((response) => {
        if (response.data.data != null) {
          setReviewWordDTO(response.data.data.reviewWordDTO);
          setReviewUpdatedSize(response.data.data.reviewUpdatedSize);
        } else {
          updateUserStreak();
          setIsFormVisible(false);
          setIsReviewComplete(true);
        }
      })
      .catch((error) => errorNotification(error.code, error.response.data.message));
  };

  useEffect(() => {
    fetchReviewAction(null);
  }, [reviewId]);

  useEffect(() => {
    if (!isFormVisible && isReviewComplete) {
      onClose();
    }
  }, [isFormVisible, isReviewComplete]);

  const pressButton = (answer: string | null) => {
    fetchReviewAction(answer);
    setIsFlipped(false);
  };

  const reviewProgress = ((totalReviewWords - reviewUpdatedSize) / totalReviewWords) * 100;

  return (
    <>
      {button}
      <Modal
        size='6xl'
        isOpen={isOpen}
        onClose={onClose}
        header={null}
        body={(
          <div>
            {!isFormVisible && isReviewComplete ? null : (
              <>
                <ProgressBar value={reviewProgress} margin='20px 50px' />
                <Flex className='ReviewWordCard_container'>
                  <ReviewWordCard
                    reviewWordDTO={reviewWordDTO!}
                    isFlipped={isFlipped}
                    setIsFlipped={setIsFlipped}
                  />
                  <Flex className='buttons_container'>
                    <Button
                      content='Forgot'
                      size={ButtonSize.MEDIUM}
                      type={ButtonType.BUTTON_RED}
                      onClick={() => pressButton('no')}
                    />
                    <Button
                      content='Remembered'
                      size={ButtonSize.MEDIUM}
                      type={ButtonType.BUTTON}
                      onClick={() => pressButton('yes')}
                      isDisabled={!reviewWordDTO}
                    />
                  </Flex>
                </Flex>
              </>
            )}
          </div>
        )}
      />
    </>
  );
}

export default StartReviewWindow;
