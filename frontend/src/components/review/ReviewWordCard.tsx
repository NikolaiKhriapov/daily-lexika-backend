import { ColorMode, useColorMode } from '@chakra-ui/react';
import styled, { css } from 'styled-components/macro';
import React from 'react';
import { Status, WordDTO } from '../../utils/types';
import { Breakpoint, Size } from '../../utils/constants';
import Text from '../common/basic/Text';
import StatusBadge from '../common/basic/StatusBadge';
import { theme } from '../../utils/theme';
import { borderStyles, mediaBreakpointUp } from '../../utils/functions';

type Props = {
  reviewWordDTO: WordDTO;
  isFlipped: boolean;
  setIsFlipped: React.Dispatch<React.SetStateAction<boolean>>;
};

export default function ReviewWordCard(props: Props) {
  const { reviewWordDTO, isFlipped, setIsFlipped } = props;

  const { colorMode } = useColorMode();
  const handleFlip = () => setIsFlipped(!isFlipped);
  const isNewStatus = reviewWordDTO.status.toString() === Status[Status.NEW];

  const wordData = {
    pinyin: {
      text: reviewWordDTO.pinyin,
      size: { base: Size.XXL, md: Size.XXXL, xl: Size.XXXL },
    },
    nameChineseSimplified: {
      text: reviewWordDTO.nameChineseSimplified,
      size: { base: Size.XXXXL, md: Size.XXXXXXL, xl: Size.XXXXXXL },
    },
    nameEnglish: {
      text: reviewWordDTO.nameEnglish,
      size: { base: Size.MD, md: Size.XL, xl: Size.XL },
    },
  };

  return (
    <Container
      colorMode={colorMode}
      isNewStatus={isNewStatus}
      isFlipped={isFlipped}
      onClick={handleFlip}
    >
      {isNewStatus && <StatusBadge text={reviewWordDTO.status} colorScheme='red' isInTopRight />}
      <ContentsContainer>
        <Text size={isFlipped ? wordData.pinyin.size : wordData.nameChineseSimplified.size}>
          {isFlipped ? wordData.pinyin.text : wordData.nameChineseSimplified.text}
        </Text>
        {isFlipped && (
          <Text size={wordData.nameEnglish.size}>
            {wordData.nameEnglish.text}
          </Text>
        )}
      </ContentsContainer>
    </Container>
  );
}

const Container = styled.div<{
  colorMode: ColorMode;
  isNewStatus: boolean;
  isFlipped: boolean;
}>`
  display: flex;
  justify-content: center;
  align-items: center;
  width: 240px;
  height: calc(240px * 1.3);
  padding: 10px;

  background-color: ${({ colorMode }) => theme.colors[colorMode].reviewWordCardBgColor};
  border: ${({ colorMode }) => borderStyles(colorMode)};
  border-radius: ${theme.stylesToDelete.borderRadius};
  border-color: ${({ colorMode, isNewStatus }) => (isNewStatus
          && theme.colors[colorMode].reviewWordCardBadgeRedColor
  )};

  transition: transform 0.3s;
  transform: ${({ isFlipped }) => (isFlipped
    ? css`rotateY(180deg) scaleX(-1);`
    : css`rotateY(0deg)`
  )};

  ${mediaBreakpointUp(Breakpoint.TABLET)} {
    width: 400px;
    height: calc(400px * 1.3);
  }
`;

const ContentsContainer = styled.div`
  width: 80%;
  display: flex;
  flex-direction: column;
  gap: 60px;
  text-align: center;
  justify-content: center;
`;
