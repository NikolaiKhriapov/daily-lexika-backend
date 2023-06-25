import {
    Button, Drawer, DrawerBody, DrawerCloseButton, DrawerContent, DrawerFooter, DrawerOverlay, useDisclosure
} from "@chakra-ui/react";
import CreateReviewForm from "./CreateReviewForm.jsx";
import {CopyIcon} from "@chakra-ui/icons";

const CloseIcon = () => "x";

const CreateReviewDrawer = ({fetchAllWordPacks, name, description, listOfWordId}) => {

    const {isOpen, onOpen, onClose} = useDisclosure()

    return <>
        <Button
            bg={"grey"}
            color={"white"}
            rounded={"full"}
            size={"sm"}
            _hover={{
                transform: 'translateY(-2px)',
                boxShadow: 'lg'
            }}
            onClick={onOpen}
        >
            Create Daily Review
        </Button>
        <Drawer isOpen={isOpen} onClose={onClose} size={"md"}>
            <DrawerOverlay/>
            <DrawerContent>
                <DrawerCloseButton/>

                <DrawerBody>
                    <div style={{margin: '15px 0', fontSize: '20px', fontWeight: 'bold'}}>{name}</div>
                    <div style={{margin: '10px 0'}}><CopyIcon/>{listOfWordId.length}</div>
                    <div style={{margin: '10px 0'}}>{description}</div>
                    <hr style={{margin: '30px 0', borderTop: '1px solid black'}}/>
                    <CreateReviewForm
                        name={name}
                        fetchAllWordPacks={fetchAllWordPacks}
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

export default CreateReviewDrawer;