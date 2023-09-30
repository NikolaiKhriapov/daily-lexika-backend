import {
    Heading, Box, Center, Flex, Text, Stack, Tag, Button, useDisclosure, AlertDialog, AlertDialogOverlay,
    AlertDialogHeader, AlertDialogContent, AlertDialogBody, AlertDialogFooter
} from '@chakra-ui/react';
import {useRef} from "react";
import {errorNotification, successNotification} from "../../services/notification.js";
import {deleteReview} from "../../services/review.js";
import {CopyIcon} from "@chakra-ui/icons";
import StartReviewWindow from "./StartReviewWindow.jsx";

export default function ReviewCard({reviewDTO, fetchAllReviewsDTO}) {

    const {isOpen: isOpenRemoveButton, onOpen: onOpenRemoveButton, onClose: onCloseRemoveButton} = useDisclosure()
    const {isOpen: isOpenStartButton, onOpen: onOpenStartButton, onClose: onCloseStartButton} = useDisclosure()
    const cancelRef = useRef()

    const startButton = (
        <Button
            bg={"grey"} color={"white"} rounded={"full"}
            _hover={{transform: 'translateY(-2px)', boxShadow: 'lg'}}
            onClick={onOpenStartButton}
        >
            Start
        </Button>
    )
    const removeButton = (
        <Button
            bg={"red.400"} color={"white"} rounded={"full"}
            _hover={{transform: 'translateY(-2px)', boxShadow: 'lg'}}
            onClick={onOpenRemoveButton}
        >
            Remove
        </Button>
    )

    return (
        <Center py={6}>
            <Flex
                justify="center" align="center" minH={'250px'} maxW={'225px'} minW={'225px'} w={'full'} m={2}
                bg={'black'} color={'white'} boxShadow={'l'} rounded={'lg'} overflow={'hidden'}
            >
                <Box p={6}>
                    <Stack spacing={2} align={'center'} mb={30}>
                        <Heading fontSize={'2xl'} fontWeight={500} fontFamily={'body'}>
                            {reviewDTO.wordPackName}
                        </Heading>
                    </Stack>
                    <Stack spacing={2} align={'left'} mb={30}>
                        <Text color={'gray.500'}>
                            New Words: <Tag borderRadius={'full'}><CopyIcon/>{reviewDTO.maxNewWordsPerDay}</Tag>
                        </Text>
                        <Text color={'gray.500'}>
                            Review Words: <Tag borderRadius={'full'}><CopyIcon/>{reviewDTO.maxReviewWordsPerDay}</Tag>
                        </Text>
                    </Stack>
                    <Stack spacing={2} align={'center'}>
                        <Flex>
                            <Stack>
                                <StartReviewWindow
                                    reviewId={reviewDTO.id}
                                    isOpen={isOpenStartButton}
                                    onClose={onCloseStartButton}
                                    button={startButton}
                                    totalReviewWords={reviewDTO.listOfWordId.length}
                                />
                            </Stack>
                            <Stack ml={5}>
                                {removeButton}
                                <AlertDialog
                                    isOpen={isOpenRemoveButton}
                                    onClose={onCloseRemoveButton}
                                    leastDestructiveRef={cancelRef}>
                                    <AlertDialogOverlay>
                                        <AlertDialogContent>
                                            <AlertDialogHeader fontSize='lg' fontWeight='bold'>Remove
                                                Review</AlertDialogHeader>
                                            <AlertDialogBody>
                                                Are you sure you want to remove '{reviewDTO.wordPackName}'?
                                                You can't undo this action.
                                            </AlertDialogBody>
                                            <AlertDialogFooter>
                                                <Button ref={cancelRef} onClick={onCloseRemoveButton}>Cancel</Button>
                                                <Button colorScheme='red' onClick={() => {
                                                    deleteReview(reviewDTO.id)
                                                        .then(() => {
                                                            successNotification(
                                                                "Review deleted successfully",
                                                                `${reviewDTO.wordPackName} deleted successfully`
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
                            </Stack>
                        </Flex>
                    </Stack>
                </Box>
            </Flex>
        </Center>
    );
}
