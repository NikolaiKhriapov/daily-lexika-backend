import React, {useEffect, useState} from 'react';
import {Box, Flex, Progress, Stat, StatLabel, StatNumber, useColorModeValue, useDisclosure} from '@chakra-ui/react';
import {errorNotification} from "../../services/popup-notification.js";
import {getReviewStatistics} from "../../services/review.js";
import {AiOutlineQuestionCircle} from "react-icons/ai";
import StatsReviewWindow from "./StatsReviewWindow.jsx";
import {getWordPack} from "../../services/word-pack.js";

export default function StatsReviewCard({reviewDTO}) {

    const [reviewStatisticsDTO, setReviewStatisticsDTO] = useState([]);
    const [wordPackDTO, setWordPackDTO] = useState([]);
    const {isOpen, onOpen, onClose} = useDisclosure()

    const fetchWordPackDTO = () => {
        getWordPack(reviewDTO.wordPackName)
            .then(response => setWordPackDTO(response.data.data.wordPackDTO))
            .catch(error => errorNotification(error.code, error.response.data.message))
    }
    const fetchReviewStatisticsDTO = () => {
        getReviewStatistics(reviewDTO.id)
            .then(response => setReviewStatisticsDTO(response.data.data.reviewStatisticsDTO))
            .catch(error => errorNotification(error.code, error.response.data.message))
    }

    useEffect(() => {
        fetchReviewStatisticsDTO()
        fetchWordPackDTO()
    }, [])

    const wordsKnownPercentage = Math.round(reviewStatisticsDTO.wordsKnown / reviewStatisticsDTO.wordsTotal * 100)

    return (
        <Box width="220px">
            <Stat
                px={'4'} py={'3.5'} shadow={'xl'} border={'1px solid'} rounded={'lg'}
                borderColor={useColorModeValue('gray.800', 'gray.500')}>
                <Flex justifyContent="space-between" alignItems="center">
                    <StatLabel fontSize={'large'} fontWeight={'bold'}>{reviewDTO.wordPackName}</StatLabel>
                    <AiOutlineQuestionCircle size="1em" onClick={onOpen}/>
                    <StatsReviewWindow
                        isOpen={isOpen}
                        onClose={onClose}
                        reviewDTO={reviewDTO}
                        reviewStatisticsDTO={reviewStatisticsDTO}
                        wordPackDTO={wordPackDTO}
                    />
                </Flex>
                <Flex justifyContent="space-between" alignItems="baseline">
                    <StatNumber fontSize={'2xl'}>
                        {wordsKnownPercentage}%
                        <StatLabel as="span" fontWeight={'medium'}> known</StatLabel>
                    </StatNumber>
                    <StatLabel fontWeight={'bold'}>
                        {reviewStatisticsDTO.wordsKnown}/{reviewStatisticsDTO.wordsTotal}
                    </StatLabel>
                </Flex>
                <Progress colorScheme='blue' size='sm' rounded={'md'} value={wordsKnownPercentage}/>
            </Stat>
        </Box>
    );
}