import {
    Button, Flex, Modal, ModalBody, ModalCloseButton, ModalContent, ModalFooter, ModalOverlay, Progress, Stack,
    useColorModeValue
} from "@chakra-ui/react"
import React, {useEffect, useState} from "react"
import {processReviewAction} from "../../services/review.js"
import {updateUserStreak} from "../../services/user.js"
import {errorNotification, successNotification} from "../../services/popup-notification.js"
import ReviewWordCard from "./ReviewWordCard.jsx"

const StartReviewWindow = ({reviewId, isOpen, onClose, button, totalReviewWords}) => {

    const [reviewWordDTO, setReviewWordDTO] = useState([])
    const [reviewUpdatedSize, setReviewUpdatedSize] = useState([])
    const [isFormVisible, setIsFormVisible] = useState(true)
    const [isReviewComplete, setIsReviewComplete] = useState(false)
    const [isFlipped, setIsFlipped] = useState(false);

    const fetchReviewAction = (answer) => {
        if (((reviewWordDTO.status === 'NEW') ||
                (reviewWordDTO.status === 'IN_REVIEW' && reviewWordDTO.totalStreak === 4))
            && answer === 'yes') {
            successNotification(
                `${reviewWordDTO.nameChineseSimplified} is a known word.`,
                "This word will still be shown occasionally during reviews"
            )
        }
        if (reviewWordDTO.status === 'KNOWN' && answer === 'no') {
            successNotification(
                `Keep reviewing ${reviewWordDTO.nameChineseSimplified}`,
                "This word will be shown more frequently so that you can relearn it"
            )
        }
        processReviewAction(reviewId, answer)
            .then((response) => {
                if (response.data.data != null) {
                    setReviewWordDTO(response.data.data.reviewWordDTO)
                    setReviewUpdatedSize(response.data.data.reviewUpdatedSize)
                } else {
                    updateUserStreak()
                    setIsFormVisible(false)
                    setIsReviewComplete(true)
                }
            })
            .catch((error) => errorNotification(error.code, error.response.data.message))
    };

    useEffect(() => {
        fetchReviewAction(null);
    }, [reviewId]);

    useEffect(() => {
        if (!isFormVisible && isReviewComplete) {
            onClose()
        }
    }, [isFormVisible, isReviewComplete])

    const pressButton = (answer) => {
        fetchReviewAction(answer);
        setIsFlipped(false)
    };

    const forgotButton = (
        <Button
            rounded={"lg"} shadow={'2xl'}
            color={useColorModeValue('black', 'white')}
            bg={useColorModeValue('gray.300', 'rgba(60,60,60)')}
            _hover={{bg: useColorModeValue('red.300', 'red.400')}}
            onClick={() => pressButton("no")}
        >
            Forgot
        </Button>
    )
    const rememberedButton = (
        <Button rounded={"lg"} shadow={'2xl'} ml={5}
                color={useColorModeValue('black', 'white')}
                bg={useColorModeValue('gray.300', 'rgba(60,60,60)')}
                _hover={{bg: useColorModeValue('gray.400', 'rgba(80,80,80)')}}
                onClick={() => pressButton("yes")} disabled={!reviewWordDTO}
        >
            Remembered
        </Button>
    )

    const reviewProgress = (totalReviewWords - reviewUpdatedSize) / totalReviewWords * 100

    return (
        <>
            {button}
            <Modal isOpen={isOpen} onClose={onClose} size={"6xl"} isCentered>
                <ModalOverlay/>
                <ModalContent border={'1px solid'} rounded={'lg'} width="80vh" height="80vh" maxW="90%" maxH="90%"
                              shadow={'2xl'} align={'center'} p={6}
                              borderColor={useColorModeValue('gray.400', 'rgba(80,80,80)')}
                              bg={useColorModeValue('gray.100', 'rgba(40,40,40)')}
                >
                    <ModalCloseButton/>
                    <ModalBody display="flex" flexDirection="column" justifyContent="center">
                        {!isFormVisible && isReviewComplete ? null : (
                            <>
                                <Stack spacing={2} mb={90} ml={10} mr={10}>
                                    <Progress value={reviewProgress} size='sm' rounded={'md'}
                                              colorScheme={useColorModeValue('gray', 'gray')}
                                              bg={useColorModeValue('gray.200', 'rgba(80,80,80)')}/>
                                </Stack>
                                <ReviewWordCard
                                    reviewWordDTO={reviewWordDTO}
                                    isFlipped={isFlipped}
                                    setIsFlipped={setIsFlipped}
                                />
                                <Stack align={"center"}>
                                    <Flex>
                                        {forgotButton}
                                        {rememberedButton}
                                    </Flex>
                                </Stack>
                            </>
                        )}
                    </ModalBody>
                    <ModalFooter height={90}/>
                </ModalContent>
            </Modal>
        </>
    )
}

export default StartReviewWindow;