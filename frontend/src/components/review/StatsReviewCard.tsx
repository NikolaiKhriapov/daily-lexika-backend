import { useEffect, useState } from 'react';
import { Box, Flex, Stat, useColorMode, useDisclosure } from '@chakra-ui/react';
import { errorNotification } from '../../services/popup-notification';
import { getReviewStatistics } from '../../services/reviews';
import StatsReviewWindow from './StatsReviewWindow';
import { getWordPack } from '../../services/word-packs';
import { ReviewDTO, ReviewStatisticsDTO, WordPackDTO } from '../../types/types';
import { TextSize } from '../../utils/constants';
import Text from '../common/basic/Text';
import ProgressBar from '../common/basic/ProgressBar';
import InfoButton from '../common/basic/InfoButton';

interface StatsReviewCardProps {
  reviewDTO: ReviewDTO;
}

function StatsReviewCard(props: StatsReviewCardProps) {
  const { reviewDTO } = props;

  const { colorMode } = useColorMode();
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

  const wordsKnownPercentage = reviewStatisticsDTO
    && Math.round((reviewStatisticsDTO.wordsKnown / reviewStatisticsDTO.wordsTotal) * 100);

  return (
    <Box>
      <Stat className={`StatsReviewCard_container ${colorMode}`} shadow='2xl'>
        <Flex className='wordPackNameAndInfoButton'>
          <Text size={TextSize.LARGE} text={reviewDTO.wordPackName} isBold />
          <InfoButton onOpen={onOpen} />
          <StatsReviewWindow
            isOpen={isOpen}
            onClose={onClose}
            reviewDTO={reviewDTO}
            reviewStatisticsDTO={reviewStatisticsDTO!}
            wordPackDTO={wordPackDTO!}
          />
        </Flex>
        <Flex className='stats'>
          <span>
            <Text size={TextSize.LARGE} text={`${wordsKnownPercentage}%`} isBold />
            <Text size={TextSize.SMALL} text=' known' isBold />
          </span>
          <Text
            size={TextSize.SMALL}
            text={reviewStatisticsDTO && `${reviewStatisticsDTO.wordsKnown}/${reviewStatisticsDTO.wordsTotal}`}
            isBold
          />
        </Flex>
        <ProgressBar value={wordsKnownPercentage || 0} />
      </Stat>
    </Box>
  );
}

export default StatsReviewCard;
