import {Wrap, WrapItem, Spinner, chakra} from '@chakra-ui/react'
import SidebarWithHeader from "./shared/SideBar.jsx";
import {useEffect, useState} from "react";
import {errorNotification} from "./services/popup-notification.js";
import ReviewCard from "./components/review/ReviewCard.jsx";
import {getAllReviews} from "./services/review.js";

const Review = () => {

    const [allReviewsDTO, setAllReviewsDTO] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    const fetchAllReviewsDTO = () => {
        setLoading(true);
        getAllReviews()
            .then(response => setAllReviewsDTO(response.data.data.allReviewsDTO))
            .catch(error => {
                setError((error.response.data.message))
                errorNotification(error.code, error.response.data.message)
            })
            .finally(() => setLoading(false))
    }

    useEffect(() => {
        fetchAllReviewsDTO();
    }, [])

    if (loading) {
        return (
            <SidebarWithHeader>
                <Spinner thickness='4px' speed='0.65s' emptyColor='gray.200' color='blue.500' size='xl'/>
            </SidebarWithHeader>
        )
    }

    if (error) {
        return (
            <SidebarWithHeader>
                <chakra.h1 textAlign="center" fontSize="4xl" py={10} fontWeight="bold">
                    Ooops, there was an error
                </chakra.h1>
            </SidebarWithHeader>
        )
    }

    if (allReviewsDTO.length <= 0) {
        return (
            <SidebarWithHeader>
                <chakra.h1 textAlign="center" fontSize="3xl" py={10} fontWeight="bold">
                    You do not have any daily reviews
                </chakra.h1>
                <chakra.h1 textAlign="center" fontSize="2xl" py={3} fontWeight="light">
                    Add a word pack to create a daily review and start growing your vocabulary
                </chakra.h1>
            </SidebarWithHeader>
        )
    }

    return (
        <SidebarWithHeader>
            <Wrap justify={"center"} spacing={"30px"}>
                {allReviewsDTO.map((reviewDTO, index) => (
                    <WrapItem key={index}>
                        <ReviewCard reviewDTO={reviewDTO} fetchAllReviewsDTO={fetchAllReviewsDTO}/>
                    </WrapItem>
                ))}
            </Wrap>
        </SidebarWithHeader>
    )
}

export default Review;