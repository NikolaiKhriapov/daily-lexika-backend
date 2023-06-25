import {
    Badge,
    Button, Drawer, DrawerBody, DrawerCloseButton, DrawerContent, DrawerFooter, DrawerOverlay, useDisclosure
} from "@chakra-ui/react";
import {CopyIcon} from "@chakra-ui/icons";

const CloseIcon = () => "x";

const ReviewWordPackDrawer = ({allWordsForWordPack, name, description, listOfWordId}) => {

    const {isOpen, onOpen, onClose} = useDisclosure()

    const getStatusColor = (status) => {
        if (status === "KNOWN") {
            return "green";
        } else if (status === "NEW") {
            return "red";
        }
        return "gray";
    };

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
            Review Words
        </Button>
        <Drawer isOpen={isOpen} onClose={onClose} size={"md"}>
            <DrawerOverlay/>
            <DrawerContent>
                <DrawerCloseButton/>

                <DrawerBody>
                    <div style={{margin: '15px 0', fontSize: '20px', fontWeight: 'bold'}}>{name}</div>
                    <div style={{margin: '10px 0'}}><CopyIcon/>{listOfWordId.length}</div>
                    <div style={{margin: '10px 0'}}>{description}</div>
                    <hr style={{margin: '30px 0 5px 0', borderTop: '1px solid black'}}/>
                    <div>
                        {allWordsForWordPack.map((oneWord, index) => (
                            <div key={index}>
                                <div style={{display: "flex"}}>
                                    <div style={{flex: "1 1 auto", minWidth: 0}}>
                                        <div>{oneWord.nameChineseSimplified} {oneWord.pinyin}</div>
                                        <div>{oneWord.nameEnglish}</div>
                                    </div>
                                    <div style={{flex: "0 0 auto", minWidth: "fit-content"}}>
                                        <div>
                                            <Badge colorScheme={getStatusColor(oneWord.status)}>{oneWord.status}</Badge>
                                        </div>
                                    </div>
                                </div>
                                <hr style={{margin: '5px 0', borderTop: '1px solid black'}}/>
                                    </div>
                        ))}
                    </div>
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

export default ReviewWordPackDrawer;