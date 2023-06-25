import {
    Button, Drawer, DrawerBody, DrawerCloseButton, DrawerContent, DrawerFooter, DrawerOverlay, useDisclosure
} from "@chakra-ui/react";
import StartReviewForm from "./StartReviewForm.jsx";

const CloseIcon = () => "x";

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
        <Drawer isOpen={isOpen} onClose={onClose} size={"full"}>
            <DrawerOverlay/>
            <DrawerContent>
                <DrawerCloseButton/>

                <DrawerBody>
                    <StartReviewForm
                        reviewId={reviewId}
                    />
                </DrawerBody>

                <DrawerFooter>
                    <Button
                        leftIcon={<CloseIcon/>}
                        colorScheme={"teal"}
                        onClick={onClose}
                    >
                        Close
                    </Button>
                </DrawerFooter>
            </DrawerContent>
        </Drawer>
    </>
}

export default StartReviewDrawer;