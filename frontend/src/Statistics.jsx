import {Box, chakra, SimpleGrid, Wrap} from '@chakra-ui/react';
import {BsFire} from 'react-icons/bs';
import {ImFire} from 'react-icons/im';
import StatsCard from "./components/review/StatsCard.jsx";
import SidebarWithHeader from "./shared/SideBar.jsx";
import {errorNotification} from "./services/notification.js";
import {useEffect, useState} from "react";
import {getUserStatistics} from "./services/user.js";
import {getWordStatistics} from "./services/word.js";

const Statistics = () => {

    const [userStatistics, setUserStatistics] = useState([]);
    const [wordStatistics, setWordStatistics] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    const fetchUserStatistics = () => {
        setLoading(true);
        getUserStatistics().then(response => {
            setUserStatistics(response.data.data.userStatistics)
        }).catch(error => {
            setError((error.response.data.message))
            errorNotification(
                error.code,
                error.response.data.message
            )
        }).finally(() => {
            setLoading(false);
        })
    }

    const fetchWordStatistics = () => {
        setLoading(true);
        getWordStatistics().then(response => {
            setWordStatistics(response.data.data.wordStatistics)
        }).catch(error => {
            setError((error.response.data.message))
            errorNotification(
                error.code,
                error.response.data.message
            )
        }).finally(() => {
            setLoading(false);
        })
    }

    useEffect(() => {
        fetchUserStatistics();
        fetchWordStatistics();
    }, [])

    return (
        <SidebarWithHeader>
            <Wrap justify={"center"} spacing={"20px"}>
                <Box maxW="7xl" mx="auto">
                    <chakra.h1 textAlign="center" fontSize="4xl" py={10} fontWeight="bold">Daily Streak</chakra.h1>
                    <SimpleGrid columns={{base: 2}} spacing={"10"}>
                        <StatsCard title="Current Streak" stat={userStatistics.currentStreak} icon={<BsFire size="3em"/>}/>
                        <StatsCard title="Record Streak" stat={userStatistics.recordStreak} icon={<ImFire size="3em"/>}/>
                    </SimpleGrid>
                </Box>
            </Wrap>
            <Wrap justify={"center"} spacing={"20px"}>
                <Box maxW="7xl" mx="auto">
                    <chakra.h1 textAlign="center" fontSize="4xl" py={10} fontWeight="bold">Vocabulary</chakra.h1>
                    <SimpleGrid columns={{base: 3}} spacing={"10"}>
                        <StatsCard title="Words Known" stat={wordStatistics.wordsKnown} icon={<BsFire size="3em"/>}/>
                        <StatsCard title="Characters Known" stat="---" icon={<ImFire size="3em"/>}/>
                        {/*<StatsCard title="Characters Known" stat={userStatistics.charactersKnown} icon={<ImFire size="3em"/>}/>*/}
                        <StatsCard title="Idioms Known" stat="---" icon={<ImFire size="3em"/>}/>
                        {/*<StatsCard title="Idioms Known" stat={userStatistics.idiomsKnown} icon={<ImFire size="3em"/>}/>*/}
                    </SimpleGrid>
                </Box>
            </Wrap>
        </SidebarWithHeader>
    )
}

export default Statistics;
