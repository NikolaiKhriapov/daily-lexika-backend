import { useEffect, useState } from 'react';
import styled from 'styled-components/macro';
import PageLayout from '../../shared/PageLayout';
import { errorNotification } from '../../services/popup-notification';
import WordPackCard from '../../components/word-pack/WordPackCard';
import { getAllWordPacks } from '../../services/word-packs';
import { WordPackDTO } from '../../utils/types';
import ErrorComponent from '../../components/common/complex/ErrorComponent';
import Heading from '../../components/common/basic/Heading';
import Spinner from '../../components/common/basic/Spinner';
import IndexPageContainer from '../../components/common/complex/IndexPageContainer';
import { Size } from '../../utils/constants';

export default function WordPack() {
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
        <IndexPageContainer>
          <Heading size={Size.LG} isCentered>No Word Packs available</Heading>
        </IndexPageContainer>
      </PageLayout>
    );
  }

  return (
    <PageLayout>
      <WordPackContainer>
        {allWordPacksDTO.map((wordPackDTO) => (
          <WordPackCard
            key={wordPackDTO.name}
            wordPackDTO={wordPackDTO}
            fetchAllWordPacksDTO={fetchAllWordPacksDTO}
          />
        ))}
      </WordPackContainer>
    </PageLayout>
  );
}

const WordPackContainer = styled.div`
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  justify-content: center;
  align-items: center;
  gap: 40px;
`;
