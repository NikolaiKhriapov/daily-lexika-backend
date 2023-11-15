import { chakra, Spinner, Wrap } from '@chakra-ui/react';
import { BsFire } from 'react-icons/bs';
import { ImFire } from 'react-icons/im';
import { GiYinYang } from 'react-icons/gi';
import { useEffect, useState } from 'react';
import StatsCard from './components/review/StatsCard';
import SidebarWithHeader from './shared/SideBar';
import { errorNotification } from './services/popup-notification';
import { getUserStatistics } from './services/user';
import { getWordStatistics } from './services/words';
import { getAllReviews } from './services/reviews';
import StatsReviewCard from './components/review/StatsReviewCard';
import { ReviewDTO, UserDTO, WordStatisticsDTO } from './types/types';

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

  return (
    <SidebarWithHeader>
      <chakra.h1 textAlign='center' fontSize='3xl' py={10} fontWeight='bold'>Daily Streak</chakra.h1>
      <Wrap justify='center' spacing='40px'>
        <StatsCard
          title='Current Streak'
          stat={userStatisticsDTO?.currentStreak || 0}
          icon={<BsFire size='3em' />}
        />
        <StatsCard
          title='Record Streak'
          stat={userStatisticsDTO?.recordStreak || 0}
          icon={<ImFire size='3em' />}
        />
      </Wrap>

      <chakra.h1 textAlign='center' fontSize='3xl' py={10} fontWeight='bold'>Vocabulary</chakra.h1>
      <Wrap justify='center' spacing='40px'>
        <StatsCard
          title='Words Known'
          stat={wordStatisticsDTO ? wordStatisticsDTO.wordsKnown : 0}
          icon={<GiYinYang size='3em' />}
        />
        <StatsCard title='Characters Known' stat='---' icon={<GiYinYang size='3em' />} />
        <StatsCard title='Idioms Known' stat='---' icon={<GiYinYang size='3em' />} />
      </Wrap>

      <chakra.h1 textAlign='center' fontSize='3xl' py={10} fontWeight='bold'>Daily Reviews</chakra.h1>
      <Wrap justify='center' spacing='40px'>
        {/* eslint-disable-next-line no-nested-ternary */}
        {allReviewsDTO && allReviewsDTO.length > 0 ? (
          allReviewsDTO!.map((reviewDTO) => (
            <StatsReviewCard
              key={reviewDTO.id}
              reviewDTO={reviewDTO}
            />
          ))
        ) : (
          <chakra.h1 fontSize='2xl' py={10} fontWeight='light'>
            You do not have any daily reviews
          </chakra.h1>
        )}
      </Wrap>
    </SidebarWithHeader>
  );
}

export default Statistics;
