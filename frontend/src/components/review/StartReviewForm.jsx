import { useEffect, useState } from "react";
import { Button, Flex, Stack } from "@chakra-ui/react";

import { nextReview, startReview } from "../../services/review.js";
import { errorNotification } from "../../services/notification.js";

import ReviewWordCard from "./ReviewWordCard.jsx";

const CreateReviewForm = ({ reviewId }) => {
    const [reviewWord, setReviewWord] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    const fetchStartReview = () => {
        setLoading(true);
        startReview(reviewId)
            .then((response) => {
                setReviewWord(response.data.data.reviewWord);
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
        fetchStartReview();
    }, [reviewId]);

    const pressRememberedButton = () => {
        setLoading(true);
        nextReview(reviewId, "yes")
            .then((response) => {
                setReviewWord(response.data.data.reviewWord);
            })
            .catch((error) => {
                setError(error.message);
                errorNotification(error.code, error.message);
            })
            .finally(() => {
                setLoading(false);
            });
    };

    const pressForgotButton = () => {
        setLoading(true);
        nextReview(reviewId, "no")
            .then((response) => {
                setReviewWord(response.data.data.reviewWord);
            })
            .catch((error) => {
                setError(error.message);
                errorNotification(error.code, error.message);
            })
            .finally(() => {
                setLoading(false);
            });
    };

    return (
        <div style={{ display: "flex", justifyContent: "center", alignItems: "center", height: "100vh" }}>
            <div>
                <ReviewWordCard reviewWord={reviewWord}/>
                <Stack spacing={2} align={'center'} mb={30}>
                    <Flex>
                        <Button
                            bg={"grey"}
                            color={"white"}
                            rounded={"base"}
                            size={"lg"}
                            _hover={{
                                bg: 'red',
                                transform: 'translateY(-2px)',
                                boxShadow: 'lg'
                            }}
                            onClick={pressForgotButton}
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
                                bg: 'green',
                                transform: 'translateY(-2px)',
                                boxShadow: 'lg'
                            }}
                            onClick={pressRememberedButton}
                        >
                            Remembered
                        </Button>
                    </Flex>
                </Stack>
            </div>
        </div>
    );
};

export default CreateReviewForm;
