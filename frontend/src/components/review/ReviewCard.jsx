import {
    Heading, Box, Center, Flex, Text, Stack, Tag, Button, useDisclosure, AlertDialog, AlertDialogOverlay,
    AlertDialogHeader, AlertDialogContent, AlertDialogBody, AlertDialogFooter
} from '@chakra-ui/react';
import {useRef} from "react";
import {errorNotification, successNotification} from "../../services/notification.js";
import {deleteReview} from "../../services/review.js";
import {CopyIcon} from "@chakra-ui/icons";
import StartReviewDrawer from "./StartReviewDrawer.jsx";

export default function ReviewCard({
                                       id,
                                       maxNewWordsPerDay,
                                       maxReviewWordsPerDay,
                                       wordPackName,
                                       listOfWordId,
                                       fetchAllReviews
                                   }) {

    const {isOpen, onOpen, onClose} = useDisclosure()
    const cancelRef = useRef()

    return (
        <Center py={6}>
            <Flex
                justify="center"
                align="center"
                minH={'250px'}
                maxW={'225px'}
                minW={'225px'}
                w={'full'}
                m={2}
                bg={'black'}
                color={'white'}
                boxShadow={'l'}
                rounded={'md'}
                overflow={'hidden'}
            >
                <Box p={6}>
                    <Stack spacing={2} align={'center'} mb={30}>
                        <Heading fontSize={'2xl'} fontWeight={500} fontFamily={'body'}>{wordPackName}</Heading>
                    </Stack>
                    <Stack spacing={2} align={'left'} mb={30}>
                        <Text color={'gray.500'}>New Words: <Tag borderRadius={'full'}><CopyIcon/>{maxNewWordsPerDay}
                        </Tag></Text>
                        <Text color={'gray.500'}>Review Words: <Tag
                            borderRadius={'full'}><CopyIcon/>{maxReviewWordsPerDay}</Tag></Text>
                    </Stack>
                    <Stack spacing={2} align={'center'}>
                        <Flex>
                            <Stack>
                                <StartReviewDrawer
                                    reviewId={id}
                                />
                            </Stack>
                            <Stack ml={5}>
                                <Button
                                    bg={"red.400"}
                                    color={"white"}
                                    rounded={"full"}
                                    _hover={{
                                        transform: 'translateY(-2px)',
                                        boxShadow: 'lg'
                                    }}
                                    onClick={onOpen}
                                >
                                    Remove
                                </Button>
                                <AlertDialog
                                    isOpen={isOpen}
                                    leastDestructiveRef={cancelRef}
                                    onClose={onClose}
                                >
                                    <AlertDialogOverlay>
                                        <AlertDialogContent>
                                            <AlertDialogHeader fontSize='lg' fontWeight='bold'>
                                                Remove Review
                                            </AlertDialogHeader>

                                            <AlertDialogBody>
                                                Are you sure you want to remove '{wordPackName}'? You can't undo this
                                                action.
                                            </AlertDialogBody>

                                            <AlertDialogFooter>
                                                <Button ref={cancelRef} onClick={onClose}>
                                                    Cancel
                                                </Button>
                                                <Button colorScheme='red' onClick={() => {
                                                    deleteReview(id).then(response => {
                                                        console.log(response)
                                                        successNotification(
                                                            "Review deleted successfully",
                                                            `${wordPackName} was successfully deleted`
                                                        )
                                                        fetchAllReviews();
                                                    }).catch(error => {
                                                        console.log(error);
                                                        errorNotification(
                                                            error.code,
                                                            error.response.data.message
                                                        )
                                                    }).finally(() => {
                                                        onClose()
                                                    })
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
