import { Flex } from '@chakra-ui/react';
import { useEffect, useState } from 'react';
import PageLayout from '../../shared/PageLayout';
import { errorNotification } from '../../services/popup-notification';
import WordPackCard from '../../components/word-pack/WordPackCard';
import { getAllWordPacks } from '../../services/word-packs';
import { WordPackDTO } from '../../types/types';
import ErrorComponent from '../../components/common/complex/ErrorComponent';
import Heading from '../../components/common/basic/Heading';
import Spinner from '../../components/common/basic/Spinner';

function WordPack() {
  const [allWordPacksDTO, setAllWordPacksDTO] = useState<WordPackDTO[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const fetchAllWordPacksDTO = () => {
    setLoading(true);
    getAllWordPacks()
      .then((response) => setAllWordPacksDTO(response.data.data.allWordPacksDTO))
      .catch((e) => {
        setError((e.response.data.message));
        errorNotification(e.code, e.response.data.message);
      })
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    fetchAllWordPacksDTO();
  }, []);

  if (loading) {
    return <PageLayout><Spinner /></PageLayout>;
  }

  if (error) {
    return <ErrorComponent />;
  }

  if (allWordPacksDTO.length <= 0) {
    return (
      <PageLayout>
        <Flex className='WordPack_container'>
          <Heading level={2} text='No Word Packs available' />
        </Flex>
      </PageLayout>
    );
  }

  return (
    <PageLayout>
      <Flex className='WordPack_container'>
        {allWordPacksDTO.map((wordPackDTO) => (
          <Flex key={wordPackDTO.name}>
            <WordPackCard
              wordPackDTO={wordPackDTO}
              fetchAllWordPacksDTO={fetchAllWordPacksDTO}
            />
          </Flex>
        ))}
      </Flex>
    </PageLayout>
  );
}

export default WordPack;
