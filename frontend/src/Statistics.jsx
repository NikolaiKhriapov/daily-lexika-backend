import {chakra, Spinner, Wrap} from '@chakra-ui/react';
import {BsFire} from 'react-icons/bs';
import {ImFire} from 'react-icons/im';
import StatsCard from "./components/review/StatsCard.jsx";
import SidebarWithHeader from "./shared/SideBar.jsx";
import {errorNotification} from "./services/popup-notification.js";
import {useEffect, useState} from "react";
import {getUserStatistics} from "./services/user.js";
import {getWordStatistics} from "./services/word.js";
import {getAllReviews} from "./services/review.js";
import StatsReviewCard from "./components/review/StatsReviewCard.jsx";
import {GiYinYang} from "react-icons/gi";

const Statistics = () => {

    const [userStatisticsDTO, setUserStatisticsDTO] = useState([]);
    const [wordStatisticsDTO, setWordStatisticsDTO] = useState([]);
    const [allReviewsDTO, setAllReviewsDTO] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    const fetchUserStatisticsDTO = () => {
        setLoading(true);
        getUserStatistics()
            .then(response => setUserStatisticsDTO(response.data.data.userStatisticsDTO))
            .catch(error => {
                setError((error.response.data.message))
                errorNotification(error.code, error.response.data.message)
            })
            .finally(() => setLoading(false))
    }

    const fetchWordStatisticsDTO = () => {
        setLoading(true);
        getWordStatistics()
            .then(response => setWordStatisticsDTO(response.data.data.wordStatisticsDTO))
            .catch(error => {
                setError((error.response.data.message))
                errorNotification(error.code, error.response.data.message)
            })
            .finally(() => setLoading(false))
    }

    const fetchAllReviewsDTO = () => {
        setLoading(true);
        getAllReviews()
            .then(response => setAllReviewsDTO(response.data.data.allReviewsDTO))
            .catch(error => {
                setError((error.response.data.message))
                errorNotification(error.code, error.response.data.message)
            })
            .finally(() => setLoading(false))
    }

    useEffect(() => {
        fetchUserStatisticsDTO();
        fetchWordStatisticsDTO();
        fetchAllReviewsDTO();
    }, [])

    if (loading) {
        return (
            <SidebarWithHeader>
                <Spinner thickness='4px' speed='0.65s' emptyColor='gray.200' color='blue.500' size='xl'/>
            </SidebarWithHeader>
        )
    }

    if (error) {
        return (
            <SidebarWithHeader>
                <chakra.h1 textAlign="center" fontSize="4xl" py={10} fontWeight="bold">
                    Ooops, there was an error
                </chakra.h1>
            </SidebarWithHeader>
        )
    }

    return (
        <SidebarWithHeader>
            <chakra.h1 textAlign="center" fontSize="3xl" py={10} fontWeight="bold">Daily Streak</chakra.h1>
            <Wrap justify={"center"} spacing={"40px"}>
                <StatsCard title="Current Streak" stat={userStatisticsDTO.currentStreak} icon={<BsFire size="3em"/>}/>
                <StatsCard title="Record Streak" stat={userStatisticsDTO.recordStreak} icon={<ImFire size="3em"/>}/>
            </Wrap>

            <chakra.h1 textAlign="center" fontSize="3xl" py={10} fontWeight="bold">Vocabulary</chakra.h1>
            <Wrap justify={"center"} spacing={"40px"}>
                <StatsCard title="Words Known" stat={wordStatisticsDTO.wordsKnown} icon={<GiYinYang size="3em"/>}/>
                <StatsCard title="Characters Known" stat="---" icon={<GiYinYang size="3em"/>}/>
                <StatsCard title="Idioms Known" stat="---" icon={<GiYinYang size="3em"/>}/>
            </Wrap>

            <chakra.h1 textAlign="center" fontSize="3xl" py={10} fontWeight="bold">Daily Reviews</chakra.h1>
            <Wrap justify={"center"} spacing={"40px"}>
                {allReviewsDTO.length > 0 ? (
                    allReviewsDTO.map((reviewDTO, index) => (
                        <StatsReviewCard
                            key={index}
                            reviewDTO={reviewDTO}
                            wordStatisticsDTO={wordStatisticsDTO}
                        />
                    ))
                ) : (
                    <chakra.h1 fontSize="2xl" py={10} fontWeight="light">
                        You do not have any daily reviews
                    </chakra.h1>
                )}
            </Wrap>
        </SidebarWithHeader>
    )
}

export default Statistics;
