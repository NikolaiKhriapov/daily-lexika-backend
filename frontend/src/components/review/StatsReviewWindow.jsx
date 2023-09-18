import React from 'react';
import {
    Modal, ModalOverlay, ModalContent, ModalHeader, ModalBody, ModalCloseButton, Flex, StatNumber, StatLabel,
    Progress, CircularProgress, CircularProgressLabel, Box, useDisclosure
} from '@chakra-ui/react';
import {AiOutlineQuestionCircle} from "react-icons/ai";
import ReviewWordPackDrawer from "../word-pack/ReviewWordPackDrawer.jsx";

const StatsReviewWindow = ({isOpen, onClose, reviewDTO, reviewStatisticsDTO, wordPackDTO}) => {

    const {isOpen: isOpenDrawer, onOpen: onOpenDrawer, onClose: onCloseDrawer} = useDisclosure()

    const wordsKnownPercentage = Math.round(reviewStatisticsDTO.wordsKnown / reviewStatisticsDTO.wordsTotal * 100)
    const wordsInReviewPercentage = Math.round(reviewStatisticsDTO.wordsInReview / reviewStatisticsDTO.wordsTotal * 100)

    return (
        <Modal isOpen={isOpen} onClose={onClose} size="xl" isCentered>
            <ModalOverlay/>
            <ModalContent>
                <ModalHeader>{reviewDTO.wordPackName}</ModalHeader>
                <ModalCloseButton/>
                <ModalBody>
                    <div style={{border: "1px solid #ccc", borderRadius: "5px", padding: "10px", marginBottom: "10px"}}>
                        <Flex justifyContent="space-between" alignItems="baseline">
                            <StatLabel fontSize={'2xl'}>Pack Progress</StatLabel>
                            <ReviewWordPackDrawer
                                button={(<AiOutlineQuestionCircle size="1em" onClick={onOpenDrawer}/>)}
                                isOpen={isOpenDrawer}
                                onClose={onCloseDrawer}
                                wordPackDTO={wordPackDTO}
                            />
                        </Flex>
                        <Flex justifyContent="space-between" alignItems="baseline">
                            <StatNumber fontSize={'2xl'}>
                                {wordsKnownPercentage}%
                                <StatLabel as="span" fontWeight={'medium'}> known</StatLabel>
                            </StatNumber>
                            <StatLabel fontWeight={'bold'}>
                                {reviewStatisticsDTO.wordsKnown}/{reviewStatisticsDTO.wordsTotal}
                            </StatLabel>
                        </Flex>
                        <Progress color='blue' size='md' value={wordsKnownPercentage}/>
                    </div>
                    <div style={{border: "1px solid #ccc", borderRadius: "5px", padding: "10px", marginBottom: "10px"}}>
                        <Flex justifyContent="space-between" alignItems="baseline">
                            <StatLabel fontSize={'2xl'}>Review Status</StatLabel>
                        </Flex>
                        <Flex>
                            <CircularProgress value={wordsKnownPercentage} size='175px' thickness='6px' color='blue'>
                                <CircularProgressLabel>{wordsInReviewPercentage}%
                                    <div style={{fontSize: '14px', fontWeight: 'medium'}}> In Review</div>
                                </CircularProgressLabel>
                            </CircularProgress>
                            <Box pl={'5'}>
                                <StatNumber fontSize={'2xl'}>
                                    {reviewStatisticsDTO.wordsInReview}
                                </StatNumber>
                                <StatLabel fontWeight={'medium'}>Words In Review</StatLabel>
                                <p style={{margin: '10px'}}/>
                                <StatNumber fontSize={'2xl'}>
                                    {reviewStatisticsDTO.wordsNew}
                                </StatNumber>
                                <StatLabel fontWeight={'medium'}>Unseen Words</StatLabel>
                            </Box>
                        </Flex>
                    </div>
                    <br/>
                </ModalBody>
            </ModalContent>
        </Modal>
    );
};

export default StatsReviewWindow;
