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

    const fetchAllReviewsDTO = () => {
        getAllReviews()
            .then(response => setAllReviewsDTO(response.data.data.allReviewsDTO))
            .catch(error => errorNotification(error.code, error.response.data.message))
    }

    useEffect(() => {
        fetchAllReviewsDTO();
    }, []);

    const isReviewExists = allReviewsDTO.some(review => review.wordPackName === wordPackDTO.name);

    const createButton = (
        <Button
            bg={"grey"} rounded={"lg"} size={"sm"} background={'gray.200'}
            borderColor={useColorModeValue('gray.400', 'gray.500')} borderWidth={'0.5px'}
            onClick={onOpenCreateButton}
        >
            Create Daily Review
        </Button>
    )

    return (
        <Box marginTop="50px">
            <Stat
                shadow={'2xl'} border={'1px solid'} rounded={'lg'} width="210px" height="270px" p={6} align={'center'}
                borderColor={useColorModeValue('gray.800', 'gray.500')}
            >
                <Flex justifyContent="right">
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
                            <Text
                                rounded={"lg"} width={'90px'} borderWidth={'1px'} display="flex" alignItems="center"
                                fontSize={'sm'} bg={'gray.100'} p={'1.5'} fontWeight={'600'} justifyContent="center"
                                borderColor={'gray.400'}
                            >
                                <FaCheck color="gray.500" style={{marginRight: '5px'}}/>Added
                            </Text>
                        )
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
