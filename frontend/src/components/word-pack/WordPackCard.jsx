import {Heading, Box, Center, Flex, Text, Stack, Tag} from '@chakra-ui/react';
import {useEffect, useState} from 'react';
import {errorNotification} from '../../services/notification.js';
import CreateReviewDrawer from "../review/CreateReviewDrawer.jsx";
import {getAllWordPacks, getAllWordsForWordPack} from "../../services/word-pack.js";
import {CopyIcon} from "@chakra-ui/icons";
import ReviewWordPackDrawer from "./ReviewWordPackDrawer.jsx";

export default function WordPackCard({name, description, category, listOfWordId, review}) {

    const [allWordPacks, setAllWordPacks] = useState([]);
    const [allWordsForWordPack, setAllWordsForWordPack] = useState([]);
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

    useEffect(() => {
        fetchAllWordPacks();
        fetchAllWordsForWordPack();
    }, [name]);

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
                bg={'black'}
                color={'white'}
                boxShadow={'l'}
                rounded={'md'}
                overflow={'hidden'}
            >
                <Box p={6} align={'center'}>
                    <Stack spacing={2} align={'center'} mb={30}>
                        <Heading fontSize={'2xl'} fontWeight={500} fontFamily={'body'}>
                            {name}
                        </Heading>
                        <Tag borderRadius={'full'}><CopyIcon/>{listOfWordId.length}</Tag>
                    </Stack>
                    <Stack spacing={2} mb={30}>
                        <Text color={'gray.500'}>{description}</Text>
                    </Stack>
                    <ReviewWordPackDrawer
                        name={name}
                        description={description}
                        listOfWordId={listOfWordId}
                        allWordsForWordPack={allWordsForWordPack}
                    />
                    <p style={{margin: '10px'}}/>
                    <CreateReviewDrawer
                        fetchAllWordPacks={fetchAllWordPacks}
                        name={name}
                        description={description}
                        listOfWordId={listOfWordId}
                    />
                </Box>
            </Flex>
        </Center>
    );
}
