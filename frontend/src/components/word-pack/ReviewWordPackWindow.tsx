import { Badge, Flex, useColorMode } from '@chakra-ui/react';
import { CopyIcon } from '@chakra-ui/icons';
import { useEffect, useRef, useState } from 'react';
import { getAllWordsForWordPack } from '../../services/word-packs';
import { errorNotification } from '../../services/popup-notification';
import { Status, WordDTO, WordPackDTO } from '../../types/types';
import Modal from '../common/complex/Modal';
import StatusBadge from '../common/basic/StatusBadge';
import Text from '../common/basic/Text';
import { TextSize } from '../../utils/constants';

interface ReviewWordPackWindowProps {
  button: any;
  isOpen: boolean;
  onClose: any;
  wordPackDTO: WordPackDTO;
}

function ReviewWordPackWindow(props: ReviewWordPackWindowProps) {
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
        size='3xl'
        isOpen={isOpen}
        onClose={onClose}
        header={wordPackDTO.name}
        body={(
          <div className='ReviewWordCardWindow_container'>
            <Flex className='totalWords_container'>
              <CopyIcon /><Text size={TextSize.MEDIUM} text={wordPackDTO.totalWords} />
            </Flex>
            <Flex className='description_container'>
              <Text size={TextSize.MEDIUM} text={wordPackDTO.description} />
            </Flex>
            <Flex className='wordInfo_container' ref={containerRef} onScroll={handleScroll}>
              {allWordsForWordPackDTO?.slice(0, visibleWords).map((wordDTO) => (
                <Flex className={`wordInfo ${colorMode}`} key={wordDTO.id}>
                  <Flex className='characterAndPinyinAndTranslation'>
                    <Flex>{wordDTO.nameChineseSimplified}&nbsp;&nbsp;{wordDTO.pinyin}</Flex>
                    <Flex>{wordDTO.nameEnglish}</Flex>
                  </Flex>
                  <Flex className='badgeOrStreakCount'>
                    {wordDTO.status.toString() === Status[Status.IN_REVIEW] ? (
                      Array.from({ length: wordDTO.totalStreak }).map(() => (
                        <Badge className='streakCount' key={wordDTO.id} />
                      ))
                    ) : (
                      <StatusBadge text={wordDTO.status} colorScheme={getStatusColor(wordDTO.status)} />
                    )}
                  </Flex>
                </Flex>
              ))}
            </Flex>
          </div>
        )}
      />
    </>
  );
}

export default ReviewWordPackWindow;
