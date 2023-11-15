import React, { useEffect, useRef, useState } from 'react';
import {
  AlertDialog,
  AlertDialogBody,
  AlertDialogContent,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogOverlay,
  Box,
  Button,
  Flex,
  Heading,
  Stat,
  StatLabel,
  StatNumber,
  useColorModeValue,
  useDisclosure,
} from '@chakra-ui/react';
import { AiOutlineCloseCircle } from 'react-icons/ai';
import { BsCheck2Circle } from 'react-icons/bs';
import { errorNotification, successNotification } from '../../services/popup-notification';
import { getReview, getWordsForReview, refreshReview, removeReview } from '../../services/reviews';
import StartReviewWindow from './StartReviewWindow';
import { ReviewDTO, Status, WordDTO } from '../../types/types';

export default function ReviewCard({ reviewDTO, fetchAllReviewsDTO }: {
  reviewDTO: ReviewDTO;
  fetchAllReviewsDTO: any;
}) {
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

  const startButton = (
    <Button
      rounded='lg'
      size='sm'
      color={useColorModeValue('black', 'white')}
      bg={useColorModeValue('gray.300', 'rgba(60,60,60)')}
      _hover={{ bg: useColorModeValue('gray.400', 'rgba(80,80,80)') }}
      shadow='2xl'
      onClick={() => (!isDateLastCompletedToday() ? onOpenStartButton() : requestRefreshReview(reviewDTO.id!))}
    >
      {!isDateLastCompletedToday() ? 'Start' : 'Refresh'}
    </Button>
  );

  return (
    <Box m='5px' mt='50px'>
      <Stat
        shadow='2xl'
        border='1px solid'
        rounded='lg'
        width='220px'
        height='270px'
        p={6}
        borderColor={useColorModeValue('gray.400', 'rgba(80,80,80)')}
        bg={useColorModeValue('gray.100', 'rgba(40,40,40)')}
      >
        <Flex justifyContent='space-between'>
          <Flex justifyContent='left' height='15px' ml='-10px' mt='-10px' mb='10px'>
            {isDateLastCompletedToday() ? <BsCheck2Circle color='green' size={30} /> : null}
          </Flex>
          <Flex justifyContent='left' height='15px' mr='-15px' mt='-15px' mb='15px'>
            <AiOutlineCloseCircle onClick={onOpenRemoveButton} cursor='pointer' />
            <AlertDialog
              isOpen={isOpenRemoveButton}
              onClose={onCloseRemoveButton}
              leastDestructiveRef={cancelRef}
            >
              <AlertDialogOverlay>
                <AlertDialogContent bg={useColorModeValue('gray.100', 'rgba(40,40,40)')}>
                  <AlertDialogHeader fontSize='lg' fontWeight='bold'>
                    Remove Review
                  </AlertDialogHeader>
                  <AlertDialogBody>
                    Are you sure you want to remove &apos;{reviewDTO.wordPackName}&apos;?
                    You cannot undo this action.
                  </AlertDialogBody>
                  <AlertDialogFooter>
                    <Button
                      rounded='lg'
                      shadow='2xl'
                      ml={5}
                      color={useColorModeValue('black', 'white')}
                      bg={useColorModeValue('gray.300', 'rgba(60,60,60)')}
                      _hover={{ bg: useColorModeValue('gray.400', 'rgba(80,80,80)') }}
                      ref={cancelRef}
                      onClick={onCloseRemoveButton}
                    >
                      Cancel
                    </Button>
                    <Button
                      rounded='lg'
                      shadow='2xl'
                      color={useColorModeValue('black', 'white')}
                      bg={useColorModeValue('gray.300', 'rgba(60,60,60)')}
                      _hover={{ bg: useColorModeValue('red.300', 'red.400') }}
                      onClick={() => {
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
                      }}
                      ml={3}
                    >
                      Remove
                    </Button>
                  </AlertDialogFooter>
                </AlertDialogContent>
              </AlertDialogOverlay>
            </AlertDialog>
          </Flex>
        </Flex>
        <Flex justifyContent='center'>
          <Heading fontSize='2xl' fontWeight={500} fontFamily='body'>
            {reviewDTO.wordPackName}
          </Heading>
        </Flex>
        <Flex justifyContent='center' height='150px' flexDirection='column'>
          <Flex alignItems='baseline' justifyContent='center'>
            <StatNumber fontSize='2xl'>
              {wordsForReviewDTO.filter((wordDTO) => wordDTO.status.toString() === Status[Status.NEW]).length}
            </StatNumber>
            <span>&nbsp;</span>
            <StatLabel fontWeight='medium'>New Words</StatLabel>
          </Flex>
          <Flex alignItems='baseline' justifyContent='center'>
            <StatNumber fontSize='2xl'>
              {
                wordsForReviewDTO.filter(
                  (wordDTO) =>
                    wordDTO.status.toString() === 'IN_REVIEW' || wordDTO.status.toString() === 'KNOWN',
                ).length
              }
            </StatNumber>
            <span>&nbsp;</span>
            <StatLabel fontWeight='medium'>Review Words</StatLabel>
          </Flex>
        </Flex>
        <Flex justifyContent='center'>
          <StartReviewWindow
            reviewId={reviewDTO.id!}
            isOpen={isOpenStartButton}
            onClose={onCloseStartButton}
            button={startButton}
            totalReviewWords={reviewDTO.listOfWordId!.length}
          />
        </Flex>
      </Stat>
    </Box>
  );
}
