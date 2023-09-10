import {useEffect, useState} from "react";
import {Button, Flex, Stack, Text} from "@chakra-ui/react";

import {processReviewAction} from "../../services/review.js";
import {errorNotification} from "../../services/notification.js";
import ReviewWordCard from "./ReviewWordCard.jsx";
import {updateUserStreak} from "../../services/user.js";

const CreateReviewForm = ({reviewId}) => {
    const [reviewWord, setReviewWord] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");
    const [isFormVisible, setIsFormVisible] = useState(true); // New state variable

    const fetchReviewAction = (answer) => {
        setLoading(true);
        processReviewAction(reviewId, answer)
            .then((response) => {
                if (response.data.data != null) {
                    setReviewWord(response.data.data.reviewWord);
                } else {
                    updateUserStreak()
                    setIsFormVisible(false);
                }
            })
            .catch((error) => {
                setError(error.response.data.message);
                errorNotification(error.code, error.response.data.message);
            })
            .finally(() => {
                setLoading(false);
            });
    };

    useEffect(() => {
        fetchReviewAction(null);
    }, [reviewId]);

    const pressButton = (answer) => {
        fetchReviewAction(answer);
    };

    if (!isFormVisible) {
        return (
            <Text fontSize="xl" textAlign="center" mt={10}>Daily Review Complete</Text>
        );
    }

    return (
        <>
            <ReviewWordCard reviewWord={reviewWord}/>
            <Stack spacing={2} align={"center"} mb={30}>
                <Flex>
                    <Button
                        bg={"grey"}
                        color={"white"}
                        rounded={"base"}
                        size={"lg"}
                        _hover={{
                            bg: "red",
                            transform: "translateY(-2px)",
                            boxShadow: "lg"
                        }}
                        onClick={() => pressButton("no")}
                        disabled={loading}
                    >
                        Forgot
                    </Button>
                    <Button
                        ml={5}
                        bg={"grey"}
                        color={"white"}
                        rounded={"base"}
                        size={"lg"}
                        _hover={{
                            bg: "green",
                            transform: "translateY(-2px)",
                            boxShadow: "lg"
                        }}
                        onClick={() => pressButton("yes")}
                        disabled={loading || !reviewWord}
                    >
                        Remembered
                    </Button>
                </Flex>
            </Stack>
        </>
    );
};

export default CreateReviewForm;
