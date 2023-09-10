import {Heading, Box, Center, Flex, Text, Stack, Tag, useColorMode} from '@chakra-ui/react';
import {useEffect, useState} from 'react';
import {errorNotification} from '../../services/notification.js';
import CreateReviewDrawer from "../review/CreateReviewDrawer.jsx";
import {getAllWordPacks, getAllWordsForWordPack} from "../../services/word-pack.js";
import {CopyIcon} from "@chakra-ui/icons";
import ReviewWordPackDrawer from "./ReviewWordPackDrawer.jsx";
import {getAllReviews} from "../../services/review.js";

export default function WordPackCard({name, description, category, totalWords, review}) {

    const [allWordPacks, setAllWordPacks] = useState([]);
    const [allWordsForWordPack, setAllWordsForWordPack] = useState([]);
    const [allReviews, setAllReviews] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    const fetchAllWordPacks = () => {
        setLoading(true);
        getAllWordPacks().then(response => {
            setAllWordPacks(response.data.data.allWordPacks);
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

    const fetchAllWordsForWordPack = () => {
        setLoading(true);
        getAllWordsForWordPack(name).then(response => {
            setAllWordsForWordPack(response.data.data.allWordsForWordPack);
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

    const fetchAllReviews = () => {
        setLoading(true);
        getAllReviews().then(response => {
            setAllReviews(response.data.data.allReviews)
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
        fetchAllWordPacks();
        fetchAllWordsForWordPack();
        fetchAllReviews();
    }, [name]);

    const isReviewExists = allReviews.some(review => review.wordPackName === name);
    const { colorMode } = useColorMode();

    return (
        <Center py={6}>
                <Flex
                    justify="center"
                    align="center"
                    minH={'250px'}
                    maxW={'225px'}
                    minW={'225px'}
                    w={'full'}
                    m={2}
                    bg={isReviewExists ? (colorMode === 'dark' ? 'rgba(255, 255, 255, 0.1)' : 'rgba(0, 0, 0, 0.6)') : 'black'}
                    color={'white'}
                    boxShadow={'l'}
                    rounded={'md'}
                    overflow={'hidden'}
                    style={{ pointerEvents: isReviewExists ? 'none' : 'auto' }}
                >
                    <Box p={6} align={'center'}>
                        <Stack spacing={2} align={'center'} mb={30}>
                            <Heading fontSize={'2xl'} fontWeight={500} fontFamily={'body'}>
                                {name}
                            </Heading>
                            <Tag borderRadius={'full'}><CopyIcon/>{totalWords}</Tag>
                        </Stack>
                        <Stack spacing={2} mb={30}>
                            <Text color={'gray.500'}>{description}</Text>
                        </Stack>
                        <ReviewWordPackDrawer
                            name={name}
                            description={description}
                            totalWords={totalWords}
                            allWordsForWordPack={allWordsForWordPack}
                        />
                        <p style={{margin: '10px'}}/>
                        <CreateReviewDrawer
                            fetchAllWordPacks={fetchAllWordPacks}
                            name={name}
                            description={description}
                            totalWords={totalWords}
                        />
                    </Box>
                </Flex>
        </Center>
    );
}
