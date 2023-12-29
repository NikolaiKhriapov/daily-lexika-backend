import { ColorMode, useColorMode, useDisclosure } from '@chakra-ui/react';
import styled from 'styled-components/macro';
import ReviewWordPackWindow from '../word-pack/ReviewWordPackWindow';
import { ReviewDTO, ReviewStatisticsDTO, WordPackDTO } from '../../utils/types';
import Modal from '../common/complex/Modal';
import Text from '../common/basic/Text';
import ProgressBar from '../common/basic/ProgressBar';
import ProgressCircular from '../common/basic/ProgressCircular';
import { theme } from '../../utils/theme';
import InfoButton from '../common/basic/InfoButton';
import { borderStyles, mediaBreakpointUp } from '../../utils/functions';
import { Breakpoint, FontWeight, Size } from '../../utils/constants';

type Props = {
  isOpen: boolean;
  onClose: any;
  reviewDTO: ReviewDTO;
  reviewStatisticsDTO: ReviewStatisticsDTO;
  wordPackDTO: WordPackDTO;
};

export default function StatsReviewWindow(props: Props) {
  const { isOpen, onClose, reviewDTO, reviewStatisticsDTO, wordPackDTO } = props;

  const { colorMode } = useColorMode();
  const { isOpen: isOpenDrawer, onOpen: onOpenDrawer, onClose: onCloseDrawer } = useDisclosure();

  const wordsKnownPercentage = Math.round(reviewStatisticsDTO
    && (reviewStatisticsDTO.wordsKnown / reviewStatisticsDTO.wordsTotal) * 100);
  const wordsInReviewPercentage = Math.round(reviewStatisticsDTO
    && (reviewStatisticsDTO.wordsInReview / reviewStatisticsDTO.wordsTotal) * 100);

  return (
    <Modal
      size={Size.XL}
      isOpen={isOpen}
      onClose={onClose}
      header={reviewDTO.wordPackName}
      body={(
        <>
          <PackProgress $colorMode={colorMode}>
            <WordPackNameAndInfoButton>
              <Text size={Size.XL} fontWeight={FontWeight.SEMIBOLD}>Pack Progress</Text>
              <ReviewWordPackWindow
                button={<InfoButton onClick={onOpenDrawer} />}
                isOpen={isOpenDrawer}
                onClose={onCloseDrawer}
                wordPackDTO={wordPackDTO}
              />
            </WordPackNameAndInfoButton>
            <StatsRow>
              <Percentage>
                <Text size={{ base: Size.MD, md: Size.XL, xl: Size.XL }} fontWeight={FontWeight.SEMIBOLD}>
                  {`${wordsKnownPercentage}%`}
                </Text>
                <Text size={{ base: Size.XS, md: Size.SM, xl: Size.SM }} fontWeight={FontWeight.SEMIBOLD}>
                  {' known'}
                </Text>
              </Percentage>
              <Text size={{ base: Size.SM, md: Size.MD, xl: Size.MD }} fontWeight={FontWeight.SEMIBOLD}>
                {reviewStatisticsDTO && `${reviewStatisticsDTO.wordsKnown}/${reviewStatisticsDTO.wordsTotal}`}
              </Text>
            </StatsRow>
            <ProgressBar value={wordsKnownPercentage} />
          </PackProgress>
          <ReviewStatus $colorMode={colorMode}>
            <Text size={Size.XL} fontWeight={FontWeight.SEMIBOLD}>Review Status</Text>
            <StatsContainer>
              <ProgressCircular value={wordsInReviewPercentage + wordsKnownPercentage} text='In Review' />
              <StatsColumn>
                <Stat>
                  <Text size={{ base: Size.XL, md: Size.XXXL, xl: Size.XXXL }}>
                    {reviewStatisticsDTO && (reviewStatisticsDTO.wordsInReview + reviewStatisticsDTO.wordsKnown)}
                  </Text>
                  <Text size={{ base: Size.XS, md: Size.MD, xl: Size.MD }} fontWeight={FontWeight.SEMIBOLD}>
                    Words In Review
                  </Text>
                </Stat>
                <Stat>
                  <Text size={{ base: Size.XL, md: Size.XXXL, xl: Size.XXXL }}>
                    {reviewStatisticsDTO && reviewStatisticsDTO.wordsNew}
                  </Text>
                  <Text size={{ base: Size.XS, md: Size.MD, xl: Size.MD }} fontWeight={FontWeight.SEMIBOLD}>
                    Unseen Words
                  </Text>
                </Stat>
              </StatsColumn>
            </StatsContainer>
          </ReviewStatus>
        </>
      )}
    />
  );
}

const PackProgress = styled.div<{ $colorMode: ColorMode }>`
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 10px 15px 15px;
  border: ${({ $colorMode }) => borderStyles($colorMode)};
  border-radius: ${theme.stylesToDelete.borderRadius};
`;

const WordPackNameAndInfoButton = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
`;

const StatsRow = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: baseline;
`;

const Percentage = styled.div`
  display: flex;
  justify-content: center;
  align-items: baseline;
  gap: 4px;
`;

const ReviewStatus = styled.div<{ $colorMode: ColorMode }>`
  display: flex;
  flex-direction: column;
  padding: 10px 15px 15px;
  border: ${({ $colorMode }) => borderStyles($colorMode)};
  border-radius: ${theme.stylesToDelete.borderRadius};
`;

const StatsContainer = styled.div`
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 15px;
`;

const StatsColumn = styled.div`
  display: flex;
  flex-direction: column;
  gap: 5px;
  
  ${mediaBreakpointUp(Breakpoint.TABLET)} {
    gap: 10px;
  }
`;

const Stat = styled.div`
  display: flex;
  flex-direction: column;
`;
