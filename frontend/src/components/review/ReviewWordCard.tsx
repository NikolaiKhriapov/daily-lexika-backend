import { Flex, Stack, useColorMode } from '@chakra-ui/react';
import { Status, WordDTO } from '../../types/types';
import { TextSize } from '../../utils/constants';
import Text from '../common/basic/Text';
import StatusBadge from '../common/basic/StatusBadge';

interface ReviewWordCardProps {
  reviewWordDTO: WordDTO;
  isFlipped: boolean;
  setIsFlipped: any;
}

function ReviewWordCard(props: ReviewWordCardProps) {
  const { reviewWordDTO, isFlipped, setIsFlipped } = props;

  const { colorMode } = useColorMode();
  const handleFlip = () => setIsFlipped(!isFlipped);
  const isNewStatus = reviewWordDTO.status.toString() === Status[Status.NEW];

  return (
    <Flex
      className={`ReviewWordCard ${colorMode} ${isNewStatus && 'isNewStatus'} ${isFlipped && 'isFlipped'}`}
      onClick={handleFlip}
    >
      <Stack className='contents_container'>
        {isNewStatus && <StatusBadge text={reviewWordDTO.status} colorScheme='red' isInTopRight />}
        <Text
          size={isFlipped ? TextSize.REVIEW_WORD_CARD_PINYIN : TextSize.REVIEW_WORD_CARD_WORD}
          text={isFlipped ? reviewWordDTO.pinyin : reviewWordDTO.nameChineseSimplified}
        />
        <Text
          size={TextSize.REVIEW_WORD_CARD_TRANSLATION}
          text={isFlipped ? reviewWordDTO.nameEnglish : null}
        />
      </Stack>
    </Flex>
  );
}

export default ReviewWordCard;
