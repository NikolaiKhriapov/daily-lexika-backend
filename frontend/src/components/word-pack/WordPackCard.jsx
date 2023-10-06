import {Heading, Box, Flex, Text, Button, useDisclosure, useColorModeValue, Stat} from '@chakra-ui/react';
import React, {useEffect, useState} from 'react';
import {errorNotification} from '../../services/popup-notification.js';
import CreateReviewWindow from "../review/CreateReviewWindow.jsx";
import ReviewWordPackWindow from "./ReviewWordPackWindow.jsx";
import {getAllReviews} from "../../services/review.js";
import {AiOutlineQuestionCircle} from "react-icons/ai";
import {TbCards} from "react-icons/tb";
import {FaCheck} from "react-icons/fa";

export default function WordPackCard({wordPackDTO, fetchAllWordPacksDTO}) {

    const [allReviewsDTO, setAllReviewsDTO] = useState([]);
    const {isOpen: isOpenPreviewButton, onOpen: onOpenPreviewButton, onClose: onClosePreviewButton} = useDisclosure()
    const {isOpen: isOpenCreateButton, onOpen: onOpenCreateButton, onClose: onCloseCreateButton} = useDisclosure()

    const isReviewExists = allReviewsDTO.some(review => review.wordPackName === wordPackDTO.name);

    const fetchAllReviewsDTO = () => {
        getAllReviews()
            .then(response => setAllReviewsDTO(response.data.data.allReviewsDTO))
            .catch(error => errorNotification(error.code, error.response.data.message))
    }

    useEffect(() => {
        fetchAllReviewsDTO();
    }, []);

    const createButton = (
        <Button
            rounded={"lg"} size={"sm"} shadow={'2xl'}
            color={useColorModeValue('black', 'white')}
            bg={useColorModeValue('gray.300', 'rgba(60,60,60)')}
            _hover={{bg: useColorModeValue('gray.400', 'rgba(80,80,80)')}}
            onClick={onOpenCreateButton}
        >
            Create Daily Review
        </Button>
    )

    return (
        <Box m={'5px'} mt='50px'>
            <Stat
                shadow={'2xl'} border={'1px solid'} rounded={'lg'} width="220px" height="270px" p={6} align={'center'}
                borderColor={useColorModeValue('gray.400', 'rgba(80,80,80)')}
                bg={useColorModeValue('gray.100', 'rgba(40,40,40)')}
            >
                <Flex justifyContent="right" mr={'-15px'} mt={'-15px'} mb={'15px'}>
                    <AiOutlineQuestionCircle size="1em" onClick={onOpenPreviewButton} cursor={'pointer'}/>
                    <ReviewWordPackWindow
                        isOpen={isOpenPreviewButton}
                        onClose={onClosePreviewButton}
                        wordPackDTO={wordPackDTO}
                    />
                </Flex>
                <Flex justifyContent="center">
                    <Heading fontSize={'2xl'} fontWeight={500} fontFamily={'body'}>{wordPackDTO.name}</Heading>
                </Flex>
                <Flex justifyContent="center">
                    <Text fontSize={'15'} display="flex" alignItems="center">
                        <TbCards size={'20'}/>{wordPackDTO.totalWords}
                    </Text>
                </Flex>
                <Flex justifyContent="center" alignItems="center" height="125px">{wordPackDTO.description}</Flex>
                <Flex justifyContent="center">
                    {isReviewExists
                        ? (
                            <Flex
                                rounded={"lg"} width={'90px'} borderWidth={'1px'} borderColor={'gray.500'}
                                display="flex" alignItems="center" justifyContent="center"
                                fontSize={'sm'} p={'1.5'} fontWeight={'600'}
                            >
                                <FaCheck style={{marginRight: '5px'}}/>Added
                            </Flex>)
                        : (
                            <CreateReviewWindow
                                button={createButton}
                                isOpen={isOpenCreateButton}
                                onClose={onCloseCreateButton}
                                wordPackDTO={wordPackDTO}
                                fetchAllWordPacksDTO={fetchAllWordPacksDTO}
                                fetchAllReviewsDTO={fetchAllReviewsDTO}
                            />
                        )
                    }
                </Flex>
            </Stat>
        </Box>
    );
}
