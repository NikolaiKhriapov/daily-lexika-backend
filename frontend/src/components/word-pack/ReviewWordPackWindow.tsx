import { ColorMode, useColorMode } from '@chakra-ui/react';
import { CopyIcon } from '@chakra-ui/icons';
import { useEffect, useRef, useState } from 'react';
import styled from 'styled-components/macro';
import { getAllWordsForWordPack } from '../../services/word-packs';
import { errorNotification } from '../../services/popup-notification';
import { Status, WordDTO, WordPackDTO } from '../../utils/types';
import Modal from '../common/complex/Modal';
import StatusBadge from '../common/basic/StatusBadge';
import Text from '../common/basic/Text';
import BadgeStreakCount from '../common/complex/BadgeStreakCount';
import { theme } from '../../utils/theme';
import { borderStyles } from '../../utils/functions';
import { Size } from '../../utils/constants';

type Props = {
  button: any;
  isOpen: boolean;
  onClose: any;
  wordPackDTO: WordPackDTO;
};

export default function ReviewWordPackWindow(props: Props) {
  const { button, isOpen, onClose, wordPackDTO } = props;

  const { colorMode } = useColorMode();
  const [allWordsForWordPackDTO, setAllWordsForWordPackDTO] = useState<[WordDTO]>();
  const [visibleWords, setVisibleWords] = useState(50);
  const containerRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    getAllWordsForWordPack(wordPackDTO.name)
      .then((response) => setAllWordsForWordPackDTO(response.data.data.allWordsForWordPackDTO))
      .catch((error) => errorNotification(error.code, error.response.data.message));
  }, [wordPackDTO.name]);

  const getStatusColor = (status: Status) => {
    switch (status.toString()) {
      case Status[Status.KNOWN]:
        return 'green';
      case Status[Status.NEW]:
        return 'red';
      default:
        return 'gray';
    }
  };

  const handleScroll = () => {
    const container = containerRef.current;
    if (container && container.scrollTop + container.clientHeight >= container.scrollHeight - 10) {
      setVisibleWords((prevVisibleWords) => prevVisibleWords + 50);
    }
  };

  return (
    <>
      {button}
      <Modal
        size={Size.XXXL}
        isOpen={isOpen}
        onClose={onClose}
        header={wordPackDTO.name}
        body={(
          <Container>
            <TotalWords>
              <CopyIcon />
              <Text>{wordPackDTO.totalWords}</Text>
            </TotalWords>
            <Description>
              <Text size={{ base: Size.SM, md: Size.MD, xl: Size.MD }}>{wordPackDTO.description}</Text>
            </Description>
            <WordInfoContainer ref={containerRef} onScroll={handleScroll}>
              {allWordsForWordPackDTO?.slice(0, visibleWords).map((wordDTO) => (
                <WordInfo colorMode={colorMode} key={wordDTO.id}>
                  <CharacterAndPinyinAndTranslation>
                    <Text>{wordDTO.nameChineseSimplified}&nbsp;&nbsp;{wordDTO.pinyin}</Text>
                    <Text size={{ base: Size.SM, md: Size.MD, xl: Size.MD }}>{wordDTO.nameEnglish}</Text>
                  </CharacterAndPinyinAndTranslation>
                  <BadgeOrStreakCount>
                    {wordDTO.status.toString() === Status[Status.IN_REVIEW] ? (
                      Array.from({ length: wordDTO.totalStreak }).map(() => (
                        <BadgeStreakCount key={wordDTO.id} />
                      ))
                    ) : (
                      <StatusBadge text={wordDTO.status} colorScheme={getStatusColor(wordDTO.status)} />
                    )}
                  </BadgeOrStreakCount>
                </WordInfo>
              ))}
            </WordInfoContainer>
          </Container>
        )}
      />
    </>
  );
}

const Container = styled.div`
  display: flex;
  flex-direction: column;
  gap: 20px;
`;

const WordInfoContainer = styled.div`
  display: flex;
  flex-direction: column;
  gap: 5px;

  max-height: 65vh;
  overflow-y: auto;
`;

const WordInfo = styled.div<{ colorMode: ColorMode }>`
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  padding: 10px;
  border: ${({ colorMode }) => borderStyles(colorMode)};
  border-radius: ${theme.stylesToDelete.borderRadius};
  max-width: 800px;
`;

const CharacterAndPinyinAndTranslation = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: space-between;
`;

const TotalWords = styled.div`
  display: flex;
  flex-direction: row;
  gap: 4px;
  align-items: center;
`;

const Description = styled.div`
`;

const BadgeOrStreakCount = styled.div`
  display: flex;
  flex-direction: row;
`;
