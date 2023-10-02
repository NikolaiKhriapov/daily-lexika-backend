import {Heading, Box, Center, Flex, Text, Stack, Tag, useColorMode, Button, useDisclosure} from '@chakra-ui/react';
import {useEffect, useState} from 'react';
import {errorNotification} from '../../services/popup-notification.js';
import CreateReviewWindow from "../review/CreateReviewWindow.jsx";
import {CopyIcon} from "@chakra-ui/icons";
import ReviewWordPackWindow from "./ReviewWordPackWindow.jsx";
import {getAllReviews} from "../../services/review.js";

export default function WordPackCard({wordPackDTO, fetchAllWordPacksDTO}) {

    const [allReviewsDTO, setAllReviewsDTO] = useState([]);
    const {isOpen: isOpenPreviewButton, onOpen: onOpenPreviewButton, onClose: onClosePreviewButton} = useDisclosure()
    const {isOpen: isOpenCreateButton, onOpen: onOpenCreateButton, onClose: onCloseCreateButton} = useDisclosure()

    const fetchAllReviewsDTO = () => {
        getAllReviews()
            .then(response => setAllReviewsDTO(response.data.data.allReviewsDTO))
            .catch(error => errorNotification(error.code, error.response.data.message))
    }

    useEffect(() => {
        fetchAllReviewsDTO();
    }, []);

    const isReviewExists = allReviewsDTO.some(review => review.wordPackName === wordPackDTO.name);
    const {colorMode} = useColorMode();

    const previewButton = (
        <Button
            bg={"grey"} color={"white"} rounded={"full"} size={"sm"}
            _hover={{transform: 'translateY(-2px)', boxShadow: 'lg'}}
            onClick={onOpenPreviewButton}
        >
            Preview
        </Button>
    )
    const createButton = (
        <Button
            bg={"grey"} color={"white"} rounded={"full"} size={"sm"}
            _hover={{transform: 'translateY(-2px)', boxShadow: 'lg'}}
            onClick={onOpenCreateButton}
        >
            Create Daily Review
        </Button>
    )

    return (
        <Center py={6}>
            <Flex
                justify="center" align="center" minH={'250px'} maxW={'225px'} minW={'225px'} w={'full'} m={2}
                bg={isReviewExists ? (colorMode === 'dark' ? 'rgba(255, 255, 255, 0.1)' : 'rgba(0, 0, 0, 0.6)') : 'black'}
                color={'white'} boxShadow={'l'} rounded={'lg'} overflow={'hidden'}
                style={{pointerEvents: isReviewExists ? 'none' : 'auto'}}
            >
                <Box p={6} align={'center'}>
                    <Stack spacing={2} align={'center'} mb={30}>
                        <Heading fontSize={'2xl'} fontWeight={500} fontFamily={'body'}>{wordPackDTO.name}</Heading>
                        <Tag borderRadius={'full'}><CopyIcon/>{wordPackDTO.totalWords}</Tag>
                    </Stack>
                    <Stack spacing={2} mb={30}>
                        <Text color={'gray.500'}>{wordPackDTO.description}</Text>
                    </Stack>
                    <ReviewWordPackWindow
                        button={previewButton}
                        isOpen={isOpenPreviewButton}
                        onClose={onClosePreviewButton}
                        wordPackDTO={wordPackDTO}
                    />
                    <p style={{margin: '10px'}}/>
                    <CreateReviewWindow
                        button={createButton}
                        isOpen={isOpenCreateButton}
                        onClose={onCloseCreateButton}
                        wordPackDTO={wordPackDTO}
                        fetchAllWordPacksDTO={fetchAllWordPacksDTO}
                        fetchAllReviewsDTO={fetchAllReviewsDTO}
                    />
                </Box>
            </Flex>
        </Center>
    );
}
