import React from 'react';
import {
  Modal, ModalOverlay, ModalContent, ModalHeader, ModalBody, ModalCloseButton, Flex, StatNumber, StatLabel,
  Progress, CircularProgress, CircularProgressLabel, Box, useDisclosure, useColorModeValue,
} from '@chakra-ui/react';
import { AiOutlineQuestionCircle } from 'react-icons/ai';
import ReviewWordPackWindow from '../word-pack/ReviewWordPackWindow';
import { ReviewDTO, ReviewStatisticsDTO, WordPackDTO } from '../../types/types';

function StatsReviewWindow({ isOpen, onClose, reviewDTO, reviewStatisticsDTO, wordPackDTO }: {
  isOpen: boolean;
  onClose: any;
  reviewDTO: ReviewDTO;
  reviewStatisticsDTO: ReviewStatisticsDTO;
  wordPackDTO: WordPackDTO;
}) {
  const { isOpen: isOpenDrawer, onOpen: onOpenDrawer, onClose: onCloseDrawer } = useDisclosure();

  const wordsKnownPercentage = Math.round(reviewStatisticsDTO && (reviewStatisticsDTO.wordsKnown / reviewStatisticsDTO.wordsTotal) * 100);
  const wordsInReviewPercentage = Math.round(reviewStatisticsDTO && (reviewStatisticsDTO.wordsInReview / reviewStatisticsDTO.wordsTotal) * 100);

  return (
    <Modal isOpen={isOpen} onClose={onClose} size='xl' isCentered>
      <ModalOverlay />
      <ModalContent
        shadow='2xl'
        rounded='lg'
        borderColor={useColorModeValue('gray.400', 'rgba(80,80,80)')}
        bg={useColorModeValue('gray.100', 'rgba(40,40,40)')}
      >
        <ModalHeader>{reviewDTO.wordPackName}</ModalHeader>
        <ModalCloseButton />
        <ModalBody>
          <div
            style={{
              border: '1px solid #ccc',
              borderRadius: '10px',
              padding: '15px',
              marginBottom: '15px',
              paddingTop: '10px',
            }}
          >
            <Flex justifyContent='space-between' alignItems='baseline'>
              <StatLabel fontSize='2xl'>Pack Progress</StatLabel>
              <ReviewWordPackWindow
                button={(
                  <AiOutlineQuestionCircle size='1em' onClick={onOpenDrawer} cursor='pointer' />
                )}
                isOpen={isOpenDrawer}
                onClose={onCloseDrawer}
                wordPackDTO={wordPackDTO}
              />
            </Flex>
            <Flex justifyContent='space-between' alignItems='baseline'>
              <StatNumber fontSize='2xl'>
                {wordsKnownPercentage}%
                <StatLabel as='span' fontWeight='medium'> known</StatLabel>
              </StatNumber>
              <StatLabel fontWeight='bold'>
                {reviewStatisticsDTO && `${reviewStatisticsDTO.wordsKnown}/${reviewStatisticsDTO.wordsTotal}`}
              </StatLabel>
            </Flex>
            <Progress
              value={wordsKnownPercentage}
              color={useColorModeValue('blue', 'blue')}
              bgColor={useColorModeValue('gray.200', 'rgba(80,80,80)')}
              size='md'
              rounded='md'
            />
          </div>
          <div
            style={{
              border: '1px solid #ccc',
              borderRadius: '10px',
              padding: '15px',
              marginBottom: '15px',
            }}
          >
            <Flex justifyContent='space-between' alignItems='baseline'>
              <StatLabel fontSize='2xl'>Review Status</StatLabel>
            </Flex>
            <Flex>
              <CircularProgress
                value={wordsInReviewPercentage + wordsKnownPercentage}
                color={useColorModeValue('blue.500', 'blue.200')}
                trackColor={useColorModeValue('gray.200', 'rgba(80,80,80)')}
                size='175px'
                thickness='6px'
              >
                <CircularProgressLabel>{wordsInReviewPercentage + wordsKnownPercentage}%
                  <div style={{ fontSize: '14px', fontWeight: 'medium' }}> In Review</div>
                </CircularProgressLabel>
              </CircularProgress>
              <Box pl='5'>
                <StatNumber fontSize='2xl'>
                  {reviewStatisticsDTO && (reviewStatisticsDTO.wordsInReview + reviewStatisticsDTO.wordsKnown)}
                </StatNumber>
                <StatLabel fontWeight='medium'>Words In Review</StatLabel>
                <p style={{ margin: '10px' }} />
                <StatNumber fontSize='2xl'>
                  {reviewStatisticsDTO && reviewStatisticsDTO.wordsNew}
                </StatNumber>
                <StatLabel fontWeight='medium'>Unseen Words</StatLabel>
              </Box>
            </Flex>
          </div>
        </ModalBody>
      </ModalContent>
    </Modal>
  );
}

export default StatsReviewWindow;
