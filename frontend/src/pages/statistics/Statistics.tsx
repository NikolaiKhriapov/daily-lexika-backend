import { BsFire } from 'react-icons/bs';
import { ImFire } from 'react-icons/im';
import { GiYinYang } from 'react-icons/gi';
import { useEffect, useState } from 'react';
import styled from 'styled-components/macro';
import StatsCard from '../../components/review/StatsCard';
import { errorNotification } from '../../services/popup-notification';
import { getUserStatistics } from '../../services/user';
import { getWordStatistics } from '../../services/words';
import { getAllReviews } from '../../services/reviews';
import StatsReviewCard from '../../components/review/StatsReviewCard';
import { ReviewDTO, UserDTO, WordStatisticsDTO } from '../../utils/types';
import PageLayout from '../../shared/PageLayout';
import ErrorComponent from '../../components/common/complex/ErrorComponent';
import Spinner from '../../components/common/basic/Spinner';
import Heading from '../../components/common/basic/Heading';
import Text from '../../components/common/basic/Text';
import { Size } from '../../utils/constants';

export default function Statistics() {
  const [userStatisticsDTO, setUserStatisticsDTO] = useState<UserDTO>();
  const [wordStatisticsDTO, setWordStatisticsDTO] = useState<WordStatisticsDTO>();
  const [allReviewsDTO, setAllReviewsDTO] = useState<[ReviewDTO]>();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const fetchUserStatisticsDTO = () => {
    setLoading(true);
    getUserStatistics()
      .then((response) => setUserStatisticsDTO(response.data.data.userStatisticsDTO))
      .catch((e) => {
        setError((e.response.data.message));
        errorNotification(e.code, e.response.data.message);
      })
      .finally(() => setLoading(false));
  };

  const fetchWordStatisticsDTO = () => {
    setLoading(true);
    getWordStatistics()
      .then((response) => setWordStatisticsDTO(response.data.data.wordStatisticsDTO))
      .catch((e) => {
        setError((e.response.data.message));
        errorNotification(e.code, e.response.data.message);
      })
      .finally(() => setLoading(false));
  };

  const fetchAllReviewsDTO = () => {
    setLoading(true);
    getAllReviews()
      .then((response) => {
        const data: [ReviewDTO] = response.data.data.allReviewsDTO;
        setAllReviewsDTO(data.sort((a, b) => a.wordPackName.localeCompare(b.wordPackName)));
      })
      .catch((e) => {
        setError((e.response.data.message));
        errorNotification(e.code, e.response.data.message);
      })
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    fetchUserStatisticsDTO();
    fetchWordStatisticsDTO();
    fetchAllReviewsDTO();
  }, []);

  if (loading) {
    return <PageLayout><Spinner /></PageLayout>;
  }

  if (error) {
    return <ErrorComponent />;
  }

  return (
    <PageLayout>
      <StatisticsContainer>
        <Section>
          <Heading size={Size.LG}>Daily Streak</Heading>
          <CardsContainer>
            <StatsCard title='Current Streak' stat={userStatisticsDTO?.currentStreak} icon={<BsFire size='45px' />} />
            <StatsCard title='Record Streak' stat={userStatisticsDTO?.recordStreak} icon={<ImFire size='45px' />} />
          </CardsContainer>
        </Section>
        <Section>
          <Heading size={Size.LG}>Vocabulary</Heading>
          <CardsContainer>
            <StatsCard title='Words Known' stat={wordStatisticsDTO?.wordsKnown} icon={<GiYinYang size='45px' />} />
            <StatsCard title='Characters Known' icon={<GiYinYang size='45px' />} />
            <StatsCard title='Idioms Known' icon={<GiYinYang size='45px' />} />
          </CardsContainer>
        </Section>
        <Section>
          <Heading size={Size.LG}>Daily Reviews</Heading>
          <CardsContainer>
            {allReviewsDTO && allReviewsDTO.length > 0
              ? (allReviewsDTO.map((reviewDTO) => (
                <StatsReviewCard key={reviewDTO.id} reviewDTO={reviewDTO} />
              )))
              : <Text size={Size.LG}>You do not have any daily reviews</Text>}
          </CardsContainer>
        </Section>
      </StatisticsContainer>
    </PageLayout>
  );
}

const StatisticsContainer = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 40px;
`;

const Section = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
`;

const CardsContainer = styled.div`
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 40px;
`;
