import {
    Button, Modal, ModalBody, ModalContent, ModalFooter, ModalOverlay, useDisclosure
} from "@chakra-ui/react";
import StartReviewForm from "./StartReviewForm.jsx";

const StartReviewDrawer = ({reviewId}) => {

    const {isOpen, onOpen, onClose} = useDisclosure()

    return <>
        <Button
            bg={"grey"}
            color={"white"}
            rounded={"full"}
            _hover={{
                transform: 'translateY(-2px)',
                boxShadow: 'lg'
            }}
            onClick={onOpen}
        >
            Start
        </Button>
        <Modal isOpen={isOpen} onClose={onClose} size={"6xl"} isCentered>
            <ModalOverlay/>
            <ModalContent maxH="80vh" minH="80vh">
                <ModalBody display="flex" flexDirection="column" justifyContent="center">
                    <StartReviewForm
                        reviewId={reviewId}
                    />
                </ModalBody>
                <ModalFooter>
                    <Button
                        colorScheme={"gray"}
                        onClick={onClose}
                    >
                        Close
                    </Button>
                </ModalFooter>
            </ModalContent>
        </Modal>
    </>
}

export default StartReviewDrawer;