import React, { useEffect, useState } from 'react';
import { Box, Flex, Progress, Stat, StatLabel, StatNumber, useColorModeValue, useDisclosure } from '@chakra-ui/react';
import { AiOutlineQuestionCircle } from 'react-icons/ai';
import { errorNotification } from '../../services/popup-notification';
import { getReviewStatistics } from '../../services/reviews';
import StatsReviewWindow from './StatsReviewWindow';
import { getWordPack } from '../../services/word-packs';
import { ReviewDTO, ReviewStatisticsDTO, WordPackDTO } from '../../types/types';

export default function StatsReviewCard({ reviewDTO }: { reviewDTO: ReviewDTO }) {
  const [reviewStatisticsDTO, setReviewStatisticsDTO] = useState<ReviewStatisticsDTO>();
  const [wordPackDTO, setWordPackDTO] = useState<WordPackDTO>();
  const { isOpen, onOpen, onClose } = useDisclosure();

  const fetchWordPackDTO = (wordPackName: string) => {
    getWordPack(wordPackName)
      .then((response) => setWordPackDTO(response.data.data.wordPackDTO))
      .catch((e) => errorNotification(e.code, e.response.data.message));
  };
  const fetchReviewStatisticsDTO = () => {
    getReviewStatistics(reviewDTO.id!)
      .then((response) => setReviewStatisticsDTO(response.data.data.reviewStatisticsDTO))
      .catch((e) => errorNotification(e.code, e.response.data.message));
  };

  useEffect(() => {
    fetchReviewStatisticsDTO();
    fetchWordPackDTO(reviewDTO.wordPackName);
  }, []);

  const wordsKnownPercentage = reviewStatisticsDTO && Math.round((reviewStatisticsDTO.wordsKnown / reviewStatisticsDTO.wordsTotal) * 100);

  return (
    <Box>
      <Stat
        shadow='2xl'
        border='1px solid'
        rounded='lg'
        px={{ md: 4 }}
        py='3.5'
        width='220px'
        borderColor={useColorModeValue('gray.400', 'rgba(80,80,80)')}
        bg={useColorModeValue('gray.100', 'rgba(40,40,40)')}
      >
        <Flex justifyContent='space-between' alignItems='center'>
          <StatLabel fontSize='large' fontWeight='bold'>{reviewDTO.wordPackName}</StatLabel>
          <AiOutlineQuestionCircle size='1em' onClick={onOpen} cursor='pointer' />
          <StatsReviewWindow
            isOpen={isOpen}
            onClose={onClose}
            reviewDTO={reviewDTO}
            reviewStatisticsDTO={reviewStatisticsDTO!}
            wordPackDTO={wordPackDTO!}
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
        <Progress colorScheme='blue' size='sm' rounded='md' value={wordsKnownPercentage} />
      </Stat>
    </Box>
  );
}
