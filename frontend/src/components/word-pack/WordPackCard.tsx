import { Flex, useColorMode, useDisclosure } from '@chakra-ui/react';
import { useEffect, useState } from 'react';
import { FaCheck } from 'react-icons/fa';
import { TbCards } from 'react-icons/tb';
import { errorNotification } from '../../services/popup-notification';
import CreateReviewWindow from '../review/CreateReviewWindow';
import ReviewWordPackWindow from './ReviewWordPackWindow';
import { getAllReviews } from '../../services/reviews';
import { ReviewDTO, WordPackDTO } from '../../types/types';
import { ButtonType, TextSize } from '../../utils/constants';
import Button from '../common/basic/Button';
import InfoButton from '../common/basic/InfoButton';
import Heading from '../common/basic/Heading';
import Text from '../common/basic/Text';

interface WordPackCardProps {
  wordPackDTO: WordPackDTO;
  fetchAllWordPacksDTO: any;
}

function WordPackCard(props: WordPackCardProps) {
  const { wordPackDTO, fetchAllWordPacksDTO } = props;

  const { colorMode } = useColorMode();
  const { isOpen: isOpenPreviewButton, onOpen: onOpenPreviewButton, onClose: onClosePreviewButton } = useDisclosure();
  const { isOpen: isOpenCreateButton, onOpen: onOpenCreateButton, onClose: onCloseCreateButton } = useDisclosure();
  const [allReviewsDTO, setAllReviewsDTO] = useState<[ReviewDTO]>();

  const isReviewExists = allReviewsDTO?.some((review: ReviewDTO) => review.wordPackName === wordPackDTO.name);

  const fetchAllReviewsDTO = () => {
    getAllReviews()
      .then((response) => setAllReviewsDTO(response.data.data.allReviewsDTO))
      .catch((error) => errorNotification(error.code, error.response.data.message));
  };

  useEffect(() => {
    fetchAllReviewsDTO();
  }, []);

  const createButton = (
    <Button content='Create Daily Review' type={ButtonType.BUTTON} onClick={onOpenCreateButton} />
  );

  return (
    <Flex className={`WordPackCard_container ${colorMode}`}>
      <Flex className='infoButton_container'>
        <Flex className='infoButton_container1'>
          <InfoButton onOpen={onOpenPreviewButton} />
          <ReviewWordPackWindow
            button={null}
            isOpen={isOpenPreviewButton}
            onClose={onClosePreviewButton}
            wordPackDTO={wordPackDTO}
          />
        </Flex>
      </Flex>
      <Flex className='wordPackName_container'>
        <Heading level={3} text={wordPackDTO.name} />
      </Flex>
      <Flex className='wordsCount_container'>
        <TbCards /><Text size={TextSize.MEDIUM} text={wordPackDTO.totalWords} />
      </Flex>
      <Flex className='description_container'>
        <Text size={TextSize.MEDIUM} text={wordPackDTO.description} />
      </Flex>
      <Flex className='buttons_container'>
        {isReviewExists
          ? <Flex className='wordPackCard__addedSign'><FaCheck />&nbsp;&nbsp;Added</Flex>
          : (
            <CreateReviewWindow
              button={createButton}
              isOpen={isOpenCreateButton}
              onClose={onCloseCreateButton}
              wordPackDTO={wordPackDTO}
              fetchAllWordPacksDTO={fetchAllWordPacksDTO}
              fetchAllReviewsDTO={fetchAllReviewsDTO}
            />
          )}
      </Flex>
    </Flex>
  );
}

export default WordPackCard;
