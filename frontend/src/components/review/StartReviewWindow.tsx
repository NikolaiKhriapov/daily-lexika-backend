import { useEffect, useState } from 'react';
import styled from 'styled-components/macro';
import { processReviewAction } from '../../services/reviews';
import { updateUserStreak } from '../../services/user';
import { errorNotification, successNotification } from '../../services/popup-notification';
import ReviewWordCard from './ReviewWordCard';
import { Status, WordDTO } from '../../utils/types';
import { Breakpoint, ButtonType, Size } from '../../utils/constants';
import Modal from '../common/complex/Modal';
import ButtonsContainer from '../common/complex/ButtonsContainer';
import ProgressBar from '../common/basic/ProgressBar';
import Button from '../common/basic/Button';
import { mediaBreakpointUp } from '../../utils/functions';

type Props = {
  reviewId: number;
  isOpen: boolean;
  onClose: any;
  totalReviewWords: number;
};

export default function StartReviewWindow(props: Props) {
  const { reviewId, isOpen, onClose, totalReviewWords } = props;

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
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      header={null}
      body={(
        <Container>
          {isFormVisible && !isReviewComplete && reviewWordDTO && (
            <>
              <ProgressBarContainer>
                <ProgressBar value={reviewProgress} />
              </ProgressBarContainer>
              <CardContainer>
                <ReviewWordCard
                  reviewWordDTO={reviewWordDTO!}
                  isFlipped={isFlipped}
                  setIsFlipped={setIsFlipped}
                />
                <ButtonsContainer>
                  <Button
                    buttonText='Forgot'
                    buttonType={ButtonType.BUTTON_RED}
                    size={{ base: Size.SM, md: Size.MD }}
                    onClick={() => pressButton('no')}
                  />
                  <Button
                    buttonText='Remembered'
                    buttonType={ButtonType.BUTTON}
                    size={{ base: Size.SM, md: Size.MD }}
                    onClick={() => pressButton('yes')}
                    isDisabled={!reviewWordDTO}
                  />
                </ButtonsContainer>
              </CardContainer>
            </>
          )}
        </Container>
      )}
    />
  );
}

const Container = styled.div`
  display: flex;
  flex-direction: column;
  gap: 30px;
  width: 80vw;
  
  ${mediaBreakpointUp(Breakpoint.TABLET)} {
    gap: 100px;
    margin: 0 0 50px 0;
  }

  ${mediaBreakpointUp(Breakpoint.DESKTOP)} {
    width: 1000px;
  }
`;

const ProgressBarContainer = styled.div`
  margin: 20px 10px;
  
  ${mediaBreakpointUp('450px')} {
    margin: 20px 50px;
  }
`;

const CardContainer = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  gap: 30px;
`;
