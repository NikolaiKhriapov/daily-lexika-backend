import { ColorMode, useColorMode, useDisclosure } from '@chakra-ui/react';
import { useEffect, useState } from 'react';
import { TbCards } from 'react-icons/tb';
import styled from 'styled-components/macro';
import { errorNotification } from '../../services/popup-notification';
import CreateReviewWindow from '../review/CreateReviewWindow';
import ReviewWordPackWindow from './ReviewWordPackWindow';
import { getAllReviews } from '../../services/reviews';
import { ReviewDTO, WordPackDTO } from '../../utils/types';
import { ButtonType, FontWeight, Size } from '../../utils/constants';
import InfoButton from '../common/basic/InfoButton';
import Text from '../common/basic/Text';
import ButtonsContainer from '../common/complex/ButtonsContainer';
import { theme } from '../../utils/theme';
import SignAdded from '../common/complex/SignAdded';
import { borderStyles } from '../../utils/functions';
import Button from '../common/basic/Button';

type Props = {
  wordPackDTO: WordPackDTO;
  fetchAllWordPacksDTO: any;
};

export default function WordPackCard(props: Props) {
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

  return (
    <Container colorMode={colorMode}>
      <InfoButtonContainer>
        <InfoButton onClick={onOpenPreviewButton} />
        <ReviewWordPackWindow
          button={null}
          isOpen={isOpenPreviewButton}
          onClose={onClosePreviewButton}
          wordPackDTO={wordPackDTO}
        />
      </InfoButtonContainer>
      <WordPackNameContainer>
        <Text size={Size.XXL} fontWeight={FontWeight.SEMIBOLD}>{wordPackDTO.name}</Text>
      </WordPackNameContainer>
      <WordsCountContainer>
        <TbCards />
        <Text size={Size.MD}>{wordPackDTO.totalWords}</Text>
      </WordsCountContainer>
      <DescriptionContainer>
        <Text size={{ base: Size.SM, md: Size.MD, xl: Size.MD }} isCentered>{wordPackDTO.description}</Text>
      </DescriptionContainer>
      <ButtonsContainer>
        {isReviewExists
          ? <SignAdded />
          : (
            <CreateReviewWindow
              isOpen={isOpenCreateButton}
              onClose={onCloseCreateButton}
              wordPackDTO={wordPackDTO}
              fetchAllWordPacksDTO={fetchAllWordPacksDTO}
              fetchAllReviewsDTO={fetchAllReviewsDTO}
              button={(
                <Button
                  buttonText='Create Daily Review'
                  buttonType={ButtonType.BUTTON}
                  size={Size.SM}
                  onClick={onOpenCreateButton}
                />
              )}
            />
          )}
      </ButtonsContainer>
    </Container>
  );
}

const Container = styled.div<{ colorMode: ColorMode }>`
  height: 280px;
  width: calc(280px / 1.3);
  padding: 0 25px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  border: ${({ colorMode }) => borderStyles(colorMode)};
  border-radius: ${theme.stylesToDelete.borderRadius};
  background-color: ${({ colorMode }) => theme.colors[colorMode].bgColor};
`;

const InfoButtonContainer = styled.div`
  display: flex;
  justify-content: right;
  margin-top: -15px;
  margin-bottom: 15px;
  margin-right: -15px;
`;

const WordPackNameContainer = styled.div`
  display: flex;
  justify-content: center;
`;

const WordsCountContainer = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
`;

const DescriptionContainer = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  height: 115px;
`;
