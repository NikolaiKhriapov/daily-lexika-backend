import { useEffect, useState } from 'react';
import { ColorMode, Stat, useColorMode, useDisclosure } from '@chakra-ui/react';
import styled from 'styled-components/macro';
import { errorNotification } from '../../services/popup-notification';
import { getReviewStatistics } from '../../services/reviews';
import StatsReviewWindow from './StatsReviewWindow';
import { getWordPack } from '../../services/word-packs';
import { ReviewDTO, ReviewStatisticsDTO, WordPackDTO } from '../../utils/types';
import Text from '../common/basic/Text';
import ProgressBar from '../common/basic/ProgressBar';
import InfoButton from '../common/basic/InfoButton';
import { theme } from '../../utils/theme';
import { borderStyles } from '../../utils/functions';
import { FontWeight, Size } from '../../utils/constants';

type Props = {
  reviewDTO: ReviewDTO;
};

export default function StatsReviewCard(props: Props) {
  const { reviewDTO } = props;

  const { colorMode } = useColorMode();
  const { isOpen, onOpen, onClose } = useDisclosure();
  const [reviewStatisticsDTO, setReviewStatisticsDTO] = useState<ReviewStatisticsDTO>();
  const [wordPackDTO, setWordPackDTO] = useState<WordPackDTO>();

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
    <Container $colorMode={colorMode} shadow='2xl'>
      <WordPackNameAndInfoButton>
        <Text size={Size.XL} fontWeight={FontWeight.SEMIBOLD}>{reviewDTO.wordPackName}</Text>
        <InfoButton onClick={onOpen} />
        {isOpen && (
          <StatsReviewWindow
            isOpen={isOpen}
            onClose={onClose}
            reviewDTO={reviewDTO}
            reviewStatisticsDTO={reviewStatisticsDTO!}
            wordPackDTO={wordPackDTO!}
          />
        )}
      </WordPackNameAndInfoButton>
      <Stats>
        <Percentage>
          <Text size={Size.XL} fontWeight={FontWeight.SEMIBOLD}>{`${wordsKnownPercentage}%`}</Text>
          <Text size={Size.SM} fontWeight={FontWeight.SEMIBOLD}>&nbsp;known</Text>
        </Percentage>
        <Text fontWeight={FontWeight.SEMIBOLD}>
          {reviewStatisticsDTO && `${reviewStatisticsDTO.wordsKnown}/${reviewStatisticsDTO.wordsTotal}`}
        </Text>
      </Stats>
      <ProgressBar value={wordsKnownPercentage || 0} />
    </Container>
  );
}

const Container = styled(Stat)<{ $colorMode: ColorMode }>`
  min-width: 220px;
  max-width: 220px;
  height: 100px;
  padding: 15px;
  background-color: ${({ $colorMode }) => theme.colors[$colorMode].bgColor};
  border: ${({ $colorMode }) => borderStyles($colorMode)};
  border-radius: ${theme.stylesToDelete.borderRadius};
  box-shadow: ${theme.stylesToDelete.boxShadow};
`;

const WordPackNameAndInfoButton = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
`;

const Stats = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: baseline;
`;

const Percentage = styled.div`
  display: flex;
  flex-direction: row;
  align-items: baseline;
`;
