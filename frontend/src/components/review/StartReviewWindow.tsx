import React, { useEffect, useState } from 'react';
import {
  Button,
  Flex,
  Modal,
  ModalBody,
  ModalCloseButton,
  ModalContent,
  ModalFooter,
  ModalOverlay,
  Progress,
  Stack,
  useColorModeValue,
} from '@chakra-ui/react';
import { processReviewAction } from '../../services/reviews';
import { updateUserStreak } from '../../services/user';
import { errorNotification, successNotification } from '../../services/popup-notification';
import ReviewWordCard from './ReviewWordCard';
import { Status, WordDTO } from '../../types/types';

function StartReviewWindow({ reviewId, isOpen, onClose, button, totalReviewWords }: {
  reviewId: number;
  isOpen: boolean;
  onClose: any;
  button: any;
  totalReviewWords: number;
}) {
  const [reviewWordDTO, setReviewWordDTO] = useState<WordDTO | null>(null);
  const [reviewUpdatedSize, setReviewUpdatedSize] = useState<number>(0);
  const [isFormVisible, setIsFormVisible] = useState(true);
  const [isReviewComplete, setIsReviewComplete] = useState(false);
  const [isFlipped, setIsFlipped] = useState(false);

  const fetchReviewAction = (answer: string | null) => {
    if (
      (reviewWordDTO !== null && (reviewWordDTO.status.toString() === Status[Status.NEW]
          || (reviewWordDTO.status.toString() === Status[Status.IN_REVIEW] && reviewWordDTO.totalStreak === 4))
        && answer === 'yes')
    ) {
      successNotification(
        `${reviewWordDTO.nameChineseSimplified} is a known word.`,
        'This word will still be shown occasionally during reviews',
      );
    }
    if (reviewWordDTO !== null && (reviewWordDTO.status.toString() === 'KNOWN' && answer === 'no')) {
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

  const forgotButton = (
    <Button
      rounded='lg'
      shadow='2xl'
      color={useColorModeValue('black', 'white')}
      bg={useColorModeValue('gray.300', 'rgba(60,60,60)')}
      _hover={{ bg: useColorModeValue('red.300', 'red.400') }}
      onClick={() => pressButton('no')}
    >
      Forgot
    </Button>
  );
  const rememberedButton = (
    <Button
      rounded='lg'
      shadow='2xl'
      ml={5}
      color={useColorModeValue('black', 'white')}
      bg={useColorModeValue('gray.300', 'rgba(60,60,60)')}
      _hover={{ bg: useColorModeValue('gray.400', 'rgba(80,80,80)') }}
      onClick={() => pressButton('yes')}
      disabled={!reviewWordDTO}
    >
      Remembered
    </Button>
  );

  const reviewProgress = ((totalReviewWords - reviewUpdatedSize) / totalReviewWords) * 100;
  const colorScheme = useColorModeValue('gray', 'gray');
  const bgColor = useColorModeValue('gray.200', 'rgba(80,80,80)');

  return (
    <>
      {button}
      <Modal isOpen={isOpen} onClose={onClose} size='6xl' isCentered>
        <ModalOverlay />
        <ModalContent
          border='1px solid'
          rounded='lg'
          width='80vh'
          height='80vh'
          maxW='90%'
          maxH='90%'
          shadow='2xl'
          // align='center'
          p={6}
          borderColor={useColorModeValue('gray.400', 'rgba(80,80,80)')}
          bg={useColorModeValue('gray.100', 'rgba(40,40,40)')}
        >
          <ModalCloseButton />
          <ModalBody display='flex' flexDirection='column' justifyContent='center'>
            {!isFormVisible && isReviewComplete ? null : (
              <>
                <Stack spacing={2} mb={90} ml={10} mr={10}>
                  <Progress
                    value={reviewProgress}
                    size='sm'
                    rounded='md'
                    colorScheme={colorScheme}
                    bg={bgColor}
                  />
                </Stack>
                <ReviewWordCard
                  reviewWordDTO={reviewWordDTO!}
                  isFlipped={isFlipped}
                  setIsFlipped={setIsFlipped}
                />
                <Stack align='center'>
                  <Flex>
                    {forgotButton}
                    {rememberedButton}
                  </Flex>
                </Stack>
              </>
            )}
          </ModalBody>
          <ModalFooter height={90} />
        </ModalContent>
      </Modal>
    </>
  );
}

export default StartReviewWindow;
