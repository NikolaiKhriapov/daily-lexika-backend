import React, { useEffect, useState } from 'react';
import styled from 'styled-components/macro';
import PageLayout from '../../shared/PageLayout';
import { errorNotification } from '../../services/popup-notification';
import { getAllReviews } from '../../services/reviews';
import { ReviewDTO } from '../../utils/types';
import ErrorComponent from '../../components/common/complex/ErrorComponent';
import Spinner from '../../components/common/basic/Spinner';
import Heading from '../../components/common/basic/Heading';
import ReviewCard from '../../components/review/ReviewCard';
import IndexPageContainer from '../../components/common/complex/IndexPageContainer';
import Text from '../../components/common/basic/Text';
import { Size } from '../../utils/constants';

export default function Review() {
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
        <IndexPageContainer>
          <Heading size={Size.LG} isCentered>You do not have any daily reviews</Heading>
          <Text size={Size.LG} isCentered>
            Add a word pack to create a daily review and start growing your vocabulary
          </Text>
        </IndexPageContainer>
      </PageLayout>
    );
  }

  return (
    <PageLayout>
      <ReviewContainer>
        {allReviewsDTO.map((reviewDTO) => (
          <ReviewCard
            key={reviewDTO.id}
            reviewDTO={reviewDTO}
            fetchAllReviewsDTO={fetchAllReviewsDTO}
          />
        ))}
      </ReviewContainer>
    </PageLayout>
  );
}

const ReviewContainer = styled.div`
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  justify-content: center;
  align-items: center;
  gap: 40px;
`;
