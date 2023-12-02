import { useEffect, useState } from 'react';
import { Flex } from '@chakra-ui/react';
import PageLayout from '../../shared/PageLayout';
import { errorNotification } from '../../services/popup-notification';
import { getAllReviews } from '../../services/reviews';
import { ReviewDTO } from '../../types/types';
import ErrorComponent from '../../components/common/complex/ErrorComponent';
import Spinner from '../../components/common/basic/Spinner';
import Heading from '../../components/common/basic/Heading';
import ReviewCard from '../../components/review/ReviewCard';

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
    return <PageLayout><Spinner /></PageLayout>;
  }

  if (error) {
    return <ErrorComponent />;
  }

  if (allReviewsDTO.length <= 0) {
    return (
      <PageLayout>
        <Flex className='indexPage_container'>
          <Heading level={2} text='You do not have any daily reviews' />
          <Flex className='textLarge'>Add a word pack to create a daily review and start growing your vocabulary</Flex>
        </Flex>
      </PageLayout>
    );
  }

  return (
    <PageLayout>
      <Flex className='Review_container'>
        {allReviewsDTO.map((reviewDTO) => (
          <Flex key={reviewDTO.id}>
            <ReviewCard
              reviewDTO={reviewDTO}
              fetchAllReviewsDTO={fetchAllReviewsDTO}
            />
          </Flex>
        ))}
      </Flex>
    </PageLayout>
  );
}

export default Review;
