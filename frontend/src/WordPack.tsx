import { Wrap, WrapItem, Spinner, chakra } from '@chakra-ui/react';
import { useEffect, useState } from 'react';
import SidebarWithHeader from './shared/SideBar';
import { errorNotification } from './services/popup-notification';
import WordPackCard from './components/word-pack/WordPackCard';
import { getAllWordPacks } from './services/word-packs';
import { WordPackDTO } from './types/types';

function WordPack() {
  const [allWordPacksDTO, setAllWordPacksDTO] = useState<[WordPackDTO]>();
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
    return (
      <SidebarWithHeader>
        <Spinner thickness='4px' speed='0.65s' emptyColor='gray.200' color='blue.500' size='xl' />
      </SidebarWithHeader>
    );
  }

  if (error) {
    return (
      <SidebarWithHeader>
        <chakra.h1 textAlign='center' fontSize='4xl' py={10} fontWeight='bold'>
          Oops, there was an error
        </chakra.h1>
      </SidebarWithHeader>
    );
  }

  if (allWordPacksDTO && allWordPacksDTO.length <= 0) {
    return (
      <SidebarWithHeader>
        <chakra.h1 textAlign='center' fontSize='4xl' py={10} fontWeight='bold'>
          No Word Packs available
        </chakra.h1>
      </SidebarWithHeader>
    );
  }

  return (
    <SidebarWithHeader>
      <Wrap justify='center' spacing='30px'>
        {allWordPacksDTO && allWordPacksDTO.map((wordPackDTO) => (
          <WrapItem key={wordPackDTO.name}>
            <WordPackCard wordPackDTO={wordPackDTO} fetchAllWordPacksDTO={fetchAllWordPacksDTO} />
          </WrapItem>
        ))}
      </Wrap>
    </SidebarWithHeader>
  );
}

export default WordPack;
