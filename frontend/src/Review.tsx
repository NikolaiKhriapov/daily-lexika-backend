import { useEffect, useState } from 'react';
import { Wrap, WrapItem, Spinner, chakra } from '@chakra-ui/react';
import SidebarWithHeader from './shared/SideBar';
import { errorNotification } from './services/popup-notification';
import ReviewCard from './components/review/ReviewCard';
import { getAllReviews } from './services/reviews';
import { ReviewDTO } from './types/types';

function Review() {
  const [allReviewsDTO, setAllReviewsDTO] = useState<ReviewDTO[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const fetchAllReviewsDTO = () => {
    setLoading(true);
    getAllReviews()
      .then((response) => {
        const data: ReviewDTO[] = response.data.data.allReviewsDTO;
        setAllReviewsDTO(data.sort((a, b) => a.wordPackName.localeCompare(b.wordPackName)));
      })
      .catch((e) => {
        setError(e.response.data.message);
        errorNotification(e.code, e.response.data.message);
      })
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    fetchAllReviewsDTO();
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

  if (allReviewsDTO.length <= 0) {
    return (
      <SidebarWithHeader>
        <chakra.h1 textAlign='center' fontSize='3xl' py={10} fontWeight='bold'>
          You do not have any daily reviews
        </chakra.h1>
        <chakra.h1 textAlign='center' fontSize='2xl' py={3} fontWeight='light'>
          Add a word pack to create a daily review and start growing your vocabulary
        </chakra.h1>
      </SidebarWithHeader>
    );
  }

  return (
    <SidebarWithHeader>
      <Wrap justify='center' spacing='30px'>
        {allReviewsDTO.map((reviewDTO) => (
          <WrapItem key={reviewDTO.id}>
            <ReviewCard reviewDTO={reviewDTO} fetchAllReviewsDTO={fetchAllReviewsDTO} />
          </WrapItem>
        ))}
      </Wrap>
    </SidebarWithHeader>
  );
}

export default Review;
