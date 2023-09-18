import {Badge, Drawer, DrawerBody, DrawerCloseButton, DrawerContent, DrawerOverlay} from "@chakra-ui/react";
import {CopyIcon} from "@chakra-ui/icons";
import {useEffect, useState, useRef} from "react";
import {getAllWordsForWordPack} from "../../services/word-pack.js";
import {errorNotification} from "../../services/notification.js";

const ReviewWordPackDrawer = ({button, isOpen, onClose, wordPackDTO}) => {

    const [allWordsForWordPackDTO, setAllWordsForWordPackDTO] = useState([]);
    const [visibleWords, setVisibleWords] = useState(50);
    const containerRef = useRef(null);

    const fetchAllWordsForWordPackDTO = () => {
        getAllWordsForWordPack(wordPackDTO.name)
            .then((response) => setAllWordsForWordPackDTO(response.data.data.allWordsForWordPackDTO))
            .catch((error) => errorNotification(error.code, error.response.data.message))
    };

    useEffect(() => {
        fetchAllWordsForWordPackDTO();
    }, []);

    const getStatusColor = (status) => {
        if (status === "KNOWN") {
            return "green";
        } else if (status === "NEW") {
            return "red";
        }
        return "gray";
    };

    const handleScroll = () => {
        const container = containerRef.current;
        if (container && container.scrollTop + container.clientHeight >= container.scrollHeight - 10) {
            setVisibleWords((prevVisibleWords) => prevVisibleWords + 50);
        }
    };

    return (
        <>
            {button}
            <Drawer isOpen={isOpen} onClose={onClose} size={"md"}>
                <DrawerOverlay/>
                <DrawerContent>
                    <DrawerCloseButton/>
                    <DrawerBody>
                        <div style={{margin: "15px 0", fontSize: "20px", fontWeight: "bold"}}>{wordPackDTO.name}</div>
                        <div style={{margin: "10px 0"}}><CopyIcon/>{wordPackDTO.totalWords}</div>
                        <div style={{margin: "10px 0 30px 0"}}>{wordPackDTO.description}</div>
                        <div ref={containerRef} style={{maxHeight: "81vh", overflowY: "auto"}} onScroll={handleScroll}>
                            {allWordsForWordPackDTO.slice(0, visibleWords).map((wordDTO, index) => (
                                <div key={index}
                                     style={{
                                         border: "1px solid #ccc",
                                         borderRadius: "5px",
                                         padding: "10px",
                                         marginBottom: "10px"
                                     }}>
                                    <div style={{display: "flex"}}>
                                        <div style={{flex: "1 1 auto", minWidth: 0}}>
                                            <div>{wordDTO.nameChineseSimplified} {wordDTO.pinyin}</div>
                                            <div>{wordDTO.nameEnglish}</div>
                                        </div>
                                        <div style={{flex: "0 0 auto", minWidth: "fit-content"}}>
                                            <Badge colorScheme={getStatusColor(wordDTO.status)}>{wordDTO.status}</Badge>
                                        </div>
                                    </div>
                                </div>
                            ))}
                        </div>
                    </DrawerBody>
                </DrawerContent>
            </Drawer>
        </>
    );
};

export default ReviewWordPackDrawer;
