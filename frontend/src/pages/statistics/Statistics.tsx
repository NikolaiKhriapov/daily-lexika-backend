import { Flex } from '@chakra-ui/react';
import { BsFire } from 'react-icons/bs';
import { ImFire } from 'react-icons/im';
import { GiYinYang } from 'react-icons/gi';
import { useEffect, useState } from 'react';
import StatsCard from '../../components/review/StatsCard';
import { errorNotification } from '../../services/popup-notification';
import { getUserStatistics } from '../../services/user';
import { getWordStatistics } from '../../services/words';
import { getAllReviews } from '../../services/reviews';
import StatsReviewCard from '../../components/review/StatsReviewCard';
import { ReviewDTO, UserDTO, WordStatisticsDTO } from '../../types/types';
import PageLayout from '../../shared/PageLayout';
import ErrorComponent from '../../components/common/complex/ErrorComponent';
import Spinner from '../../components/common/basic/Spinner';
import Heading from '../../components/common/basic/Heading';
import Text from '../../components/common/basic/Text';
import { TextSize } from '../../utils/constants';

function Statistics() {
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
      <Flex className='Statistics_container'>
        <Flex className='section'>
          <Heading level={2} text='Daily Streak' />
          <Flex className='cardsContainer'>
            <StatsCard title='Current Streak' stat={userStatisticsDTO?.currentStreak} icon={<BsFire size='3em' />} />
            <StatsCard title='Record Streak' stat={userStatisticsDTO?.recordStreak} icon={<ImFire size='3em' />} />
          </Flex>
        </Flex>
        <Flex className='section'>
          <Heading level={2} text='Vocabulary' />
          <Flex className='cardsContainer'>
            <StatsCard title='Words Known' stat={wordStatisticsDTO?.wordsKnown} icon={<GiYinYang size='3em' />} />
            <StatsCard title='Characters Known' icon={<GiYinYang size='3em' />} />
            <StatsCard title='Idioms Known' icon={<GiYinYang size='3em' />} />
          </Flex>
        </Flex>
        <Flex className='section'>
          <Heading level={2} text='Daily Reviews' />
          <Flex className='cardsContainer'>
            {allReviewsDTO && allReviewsDTO.length > 0
              ? (allReviewsDTO!.map((reviewDTO) => (
                <StatsReviewCard key={reviewDTO.id} reviewDTO={reviewDTO} />
              )))
              : <Text size={TextSize.XL} text='You do not have any daily reviews' />}
          </Flex>
        </Flex>
      </Flex>
    </PageLayout>
  );
}

export default Statistics;
