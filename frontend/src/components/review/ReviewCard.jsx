import {
    Heading, Box, Flex, Button, useDisclosure, AlertDialog, AlertDialogOverlay, AlertDialogHeader, AlertDialogContent,
    AlertDialogBody, AlertDialogFooter, Badge, useColorModeValue, Stat, StatNumber, StatLabel
} from '@chakra-ui/react'
import React, {useEffect, useRef, useState} from "react"
import {errorNotification, successNotification} from "../../services/popup-notification.js"
import {removeReview, getWordsForReview, getReview, refreshReview} from "../../services/review.js"
import StartReviewWindow from "./StartReviewWindow.jsx"
import {AiOutlineCloseCircle} from "react-icons/ai";

export default function ReviewCard({reviewDTO, fetchAllReviewsDTO}) {

    const [wordsForReviewDTO, setWordsForReviewDTO] = useState([])
    const [updatedReviewDTO, setUpdatedReviewDTO] = useState(reviewDTO)
    const [reviewRemoved, setReviewRemoved] = useState(false)
    const [refreshed, setRefreshed] = useState(false);
    const {isOpen: isOpenRemoveButton, onOpen: onOpenRemoveButton, onClose: onCloseRemoveButton} = useDisclosure()
    const {isOpen: isOpenStartButton, onOpen: onOpenStartButton, onClose: onCloseStartButton} = useDisclosure()
    const cancelRef = useRef()

    const isDateLastCompletedToday =
        new Date(updatedReviewDTO.dateLastCompleted).toDateString() === new Date().toDateString()

    const fetchWordsForReviewDTO = (reviewId) => {
        getWordsForReview(reviewId)
            .then(response => setWordsForReviewDTO(response.data.data.wordsForReviewDTO))
            .catch(error => errorNotification(error.code, error.response.data.message))
    }

    const fetchReviewDTO = (reviewId) => {
        getReview(reviewId)
            .then(response => setUpdatedReviewDTO(response.data.data.reviewDTO))
            .catch(error => errorNotification(error.code, error.response.data.message))
    }

    const requestRefreshReview = (reviewId) => {
        setRefreshed(true)
        refreshReview(reviewId)
            .then(() => fetchAllReviewsDTO())
            .catch((error) => errorNotification(error.code, error.response.data.message));
    };

    useEffect(() => {
        if (!reviewRemoved && !refreshed) {
            fetchWordsForReviewDTO(reviewDTO.id)
            fetchReviewDTO(reviewDTO.id)
        }
    }, [wordsForReviewDTO])

    const startButton = (
        <Button
            bg={"red.400"} rounded={"lg"} background={'gray.200'}
            borderColor={useColorModeValue('gray.400', 'gray.500')} borderWidth={'0.5px'}
            onClick={() => isDateLastCompletedToday ? requestRefreshReview(reviewDTO.id) : onOpenStartButton()}
        >
            {!isDateLastCompletedToday ? 'Start' : 'Refresh'}
        </Button>
    )

    return (
        <Box marginTop="50px">
            <Stat
                shadow={'2xl'} border={'1px solid'} rounded={'lg'} width="210px" height="270px" p={6} align={'center'}
                borderColor={useColorModeValue('gray.800', 'gray.500')}
            >
                <Flex justifyContent={"space-between"}>
                    <Flex justifyContent="left" height="15px" ml={'-30px'} mt={'-3px'} mb={'3px'}>
                        {isDateLastCompletedToday
                            ? (
                                <Badge color={"green"} border={'1px'} fontSize={'9'}
                                       style={{transform: 'rotate(-45deg)'}}>
                                    Completed
                                </Badge>)
                            : null
                        }
                    </Flex>
                    <Flex justifyContent="left" height="15px" mr={'-15px'} mt={'-15px'} mb={'15px'}>
                        <AiOutlineCloseCircle onClick={onOpenRemoveButton} cursor={'pointer'}/>
                        <AlertDialog
                            isOpen={isOpenRemoveButton}
                            onClose={onCloseRemoveButton}
                            leastDestructiveRef={cancelRef}>
                            <AlertDialogOverlay>
                                <AlertDialogContent>
                                    <AlertDialogHeader fontSize='lg' fontWeight='bold'>Remove Review</AlertDialogHeader>
                                    <AlertDialogBody>
                                        Are you sure you want to remove '{reviewDTO.wordPackName}'?
                                        You can't undo this action.
                                    </AlertDialogBody>
                                    <AlertDialogFooter>
                                        <Button ref={cancelRef} onClick={onCloseRemoveButton}>Cancel</Button>
                                        <Button colorScheme='red' onClick={() => {
                                            setReviewRemoved(true)
                                            removeReview(reviewDTO.id)
                                                .then(() => {
                                                    successNotification(
                                                        "Review removed successfully",
                                                        `${reviewDTO.wordPackName} removed successfully`
                                                    )
                                                    fetchAllReviewsDTO()
                                                })
                                                .catch(error =>
                                                    errorNotification(error.code, error.response.data.message))
                                                .finally(() => onCloseRemoveButton())
                                        }} ml={3}>
                                            Remove
                                        </Button>
                                    </AlertDialogFooter>
                                </AlertDialogContent>
                            </AlertDialogOverlay>
                        </AlertDialog>
                    </Flex>
                </Flex>
                <Flex justifyContent="center">
                    <Heading fontSize={'2xl'} fontWeight={500} fontFamily={'body'}>{reviewDTO.wordPackName}</Heading>
                </Flex>
                <Flex justifyContent="center" height="140px" flexDirection="column">
                    <Flex alignItems="baseline" justifyContent="center">
                        <StatNumber fontSize={'2xl'}>
                            {wordsForReviewDTO.filter((wordDTO) => wordDTO.status === "NEW").length}
                        </StatNumber>
                        <span>&nbsp;</span>
                        <StatLabel fontWeight={'medium'}>New Words</StatLabel>
                    </Flex>
                    <Flex alignItems="baseline" justifyContent="center">
                        <StatNumber fontSize={'2xl'}>
                            {wordsForReviewDTO.filter((wordDTO) => wordDTO.status === "IN_REVIEW" || wordDTO.status === "KNOWN").length}
                        </StatNumber>
                        <span>&nbsp;</span>
                        <StatLabel fontWeight={'medium'}>Review Words</StatLabel>
                    </Flex>
                </Flex>
                <Flex justifyContent="center">
                    <StartReviewWindow
                        reviewId={reviewDTO.id}
                        isOpen={isOpenStartButton}
                        onClose={onCloseStartButton}
                        button={startButton}
                        totalReviewWords={reviewDTO.listOfWordId.length}
                    />
                </Flex>
            </Stat>
        </Box>
    )
}
