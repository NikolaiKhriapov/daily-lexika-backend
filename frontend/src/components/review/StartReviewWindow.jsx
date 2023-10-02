import {
    Button, Flex, Modal, ModalBody, ModalCloseButton, ModalContent, ModalOverlay, Progress, Stack, Text
} from "@chakra-ui/react";
import React, {useEffect, useState} from "react";
import {processReviewAction} from "../../services/review.js";
import {updateUserStreak} from "../../services/user.js";
import {errorNotification, successNotification} from "../../services/popup-notification.js";
import ReviewWordCard from "./ReviewWordCard.jsx";

const StartReviewWindow = ({reviewId, isOpen, onClose, button, totalReviewWords}) => {

    const [reviewWordDTO, setReviewWordDTO] = useState([])
    const [reviewUpdatedSize, setReviewUpdatedSize] = useState([])
    const [isFormVisible, setIsFormVisible] = useState(true)
    const [isReviewComplete, setIsReviewComplete] = useState(false)

    const fetchReviewAction = (answer) => {
        if (((reviewWordDTO.status === 'NEW') || (reviewWordDTO.status === 'IN_REVIEW' && reviewWordDTO.totalStreak === 4))
            && answer === 'yes') {
            successNotification(
                `${reviewWordDTO.nameChineseSimplified} is a known word.`,
                "This word will still be shown occasionally during reviews"
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

    const pressButton = (answer) => {
        fetchReviewAction(answer);
    };

    const forgotButton = (
        <Button
            bg={"grey"} color={"white"} rounded={"lg"} size={"lg"}
            _hover={{bg: "red", transform: "translateY(-2px)", boxShadow: "lg"}}
            onClick={() => pressButton("no")}
        >
            Forgot
        </Button>
    )
    const rememberedButton = (
        <Button
            ml={5} bg={"grey"} color={"white"} rounded={"lg"} size={"lg"}
            _hover={{bg: "green", transform: "translateY(-2px)", boxShadow: "lg"}}
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
                <ModalContent maxH="80vh" minH="80vh" minW="80vh" maxW="80vh" rounded={'lg'}>
                    <ModalCloseButton/>
                    <ModalBody display="flex" flexDirection="column" justifyContent="center">
                        {!isFormVisible && isReviewComplete ? (
                            <Text fontSize="xl" textAlign="center">Daily Review Complete</Text>
                        ) : (
                            <>
                                <Stack spacing={2} mb={90} ml={10} mr={10}>
                                    <Progress colorScheme='gray' size='sm' rounded={'md'} value={reviewProgress}/>
                                </Stack>
                                <ReviewWordCard reviewWordDTO={reviewWordDTO}/>
                                <Stack spacing={2} align={"center"} mb={90}>
                                    <Flex>
                                        {forgotButton}
                                        {rememberedButton}
                                    </Flex>
                                </Stack>
                            </>
                        )}
                    </ModalBody>
                </ModalContent>
            </Modal>
        </>
    )
}

export default StartReviewWindow;