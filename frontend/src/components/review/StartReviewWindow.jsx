import {
    Button, Flex, Modal, ModalBody, ModalCloseButton, ModalContent, ModalOverlay, Stack, Text
} from "@chakra-ui/react";
import React, {useEffect, useState} from "react";
import {processReviewAction} from "../../services/review.js";
import {updateUserStreak} from "../../services/user.js";
import {errorNotification} from "../../services/notification.js";
import ReviewWordCard from "./ReviewWordCard.jsx";

const StartReviewWindow = ({reviewId, isOpen, onClose, button}) => {

    const [reviewWordDTO, setReviewWordDTO] = useState([])
    const [isFormVisible, setIsFormVisible] = useState(true)
    const [isReviewComplete, setIsReviewComplete] = useState(false)

    const fetchReviewAction = (answer) => {
        processReviewAction(reviewId, answer)
            .then((response) => {
                if (response.data.data != null) {
                    setReviewWordDTO(response.data.data.reviewWordDTO)
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
            bg={"grey"} color={"white"} rounded={"base"} size={"lg"}
            _hover={{bg: "red", transform: "translateY(-2px)", boxShadow: "lg"}}
            onClick={() => pressButton("no")}
        >
            Forgot
        </Button>
    )
    const rememberedButton = (
        <Button
            ml={5} bg={"grey"} color={"white"} rounded={"base"} size={"lg"}
            _hover={{bg: "green", transform: "translateY(-2px)", boxShadow: "lg"}}
            onClick={() => pressButton("yes")} disabled={!reviewWordDTO}
        >
            Remembered
        </Button>
    )

    return (
        <>
            {button}
            <Modal isOpen={isOpen} onClose={onClose} size={"6xl"} isCentered>
                <ModalOverlay/>
                <ModalContent maxH="80vh" minH="80vh">
                    <ModalCloseButton/>
                    <ModalBody display="flex" flexDirection="column" justifyContent="center">
                        {!isFormVisible && isReviewComplete ? (
                            <Text fontSize="xl" textAlign="center" mt={10}>Daily Review Complete</Text>
                        ) : (
                            <>
                                <ReviewWordCard reviewWordDTO={reviewWordDTO}/>
                                <Stack spacing={2} align={"center"} mb={30}>
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