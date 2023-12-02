import { Flex, useDisclosure } from '@chakra-ui/react';
import { AiOutlineQuestionCircle } from 'react-icons/ai';
import ReviewWordPackWindow from '../word-pack/ReviewWordPackWindow';
import { ReviewDTO, ReviewStatisticsDTO, WordPackDTO } from '../../types/types';
import { TextSize } from '../../utils/constants';
import Modal from '../common/complex/Modal';
import Heading from '../common/basic/Heading';
import Text from '../common/basic/Text';
import ProgressBar from '../common/basic/ProgressBar';
import ProgressCircular from '../common/basic/ProgressCircular';

interface StatsReviewWindowProps {
  isOpen: boolean;
  onClose: any;
  reviewDTO: ReviewDTO;
  reviewStatisticsDTO: ReviewStatisticsDTO;
  wordPackDTO: WordPackDTO;
}

function StatsReviewWindow(props: StatsReviewWindowProps) {
  const { isOpen, onClose, reviewDTO, reviewStatisticsDTO, wordPackDTO } = props;

  const { isOpen: isOpenDrawer, onOpen: onOpenDrawer, onClose: onCloseDrawer } = useDisclosure();

  const wordsKnownPercentage = Math.round(reviewStatisticsDTO
    && (reviewStatisticsDTO.wordsKnown / reviewStatisticsDTO.wordsTotal) * 100);
  const wordsInReviewPercentage = Math.round(reviewStatisticsDTO
    && (reviewStatisticsDTO.wordsInReview / reviewStatisticsDTO.wordsTotal) * 100);

  return (
    <Modal
      size='xl'
      isOpen={isOpen}
      onClose={onClose}
      header={reviewDTO.wordPackName}
      body={(
        <Flex className='StatsReviewWindow_container'>
          <Flex className='packProgress'>
            <Flex className='wordPackNameAndInfoButton'>
              <Heading level={3} text='Pack Progress' />
              <ReviewWordPackWindow
                button={<AiOutlineQuestionCircle className='infoButton' onClick={onOpenDrawer} />}
                isOpen={isOpenDrawer}
                onClose={onCloseDrawer}
                wordPackDTO={wordPackDTO}
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
            <ProgressBar value={wordsKnownPercentage} />
          </Flex>
          <Flex className='reviewStatus'>
            <Heading level={3} text='Review Status' />
            <Flex className='stats'>
              <ProgressCircular value={wordsInReviewPercentage + wordsKnownPercentage} text='In Review' />
              <Flex className='text'>
                <Heading
                  level={3}
                  text={reviewStatisticsDTO && (reviewStatisticsDTO.wordsInReview + reviewStatisticsDTO.wordsKnown)}
                />
                <Text size={TextSize.SMALL} text='Words In Review' isBold />
                <br />
                <Heading level={3} text={reviewStatisticsDTO && reviewStatisticsDTO.wordsNew} />
                <Text size={TextSize.SMALL} text='Unseen Words' isBold />
              </Flex>
            </Flex>
          </Flex>
        </Flex>
      )}
    />
  );
}

export default StatsReviewWindow;
