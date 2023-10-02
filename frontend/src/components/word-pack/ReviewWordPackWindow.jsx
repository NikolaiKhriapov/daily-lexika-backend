import {Badge, Modal, ModalBody, ModalCloseButton, ModalContent, ModalOverlay} from "@chakra-ui/react";
import {CopyIcon} from "@chakra-ui/icons";
import {useEffect, useState, useRef} from "react";
import {getAllWordsForWordPack} from "../../services/word-pack.js";
import {errorNotification} from "../../services/popup-notification.js";

const ReviewWordPackWindow = ({button, isOpen, onClose, wordPackDTO}) => {

    const [allWordsForWordPackDTO, setAllWordsForWordPackDTO] = useState([]);
    const [visibleWords, setVisibleWords] = useState(50);
    const containerRef = useRef(null);

    useEffect(() => {
        getAllWordsForWordPack(wordPackDTO.name)
            .then((response) => setAllWordsForWordPackDTO(response.data.data.allWordsForWordPackDTO))
            .catch((error) => errorNotification(error.code, error.response.data.message))
    }, [wordPackDTO.name]);

    const getStatusColor = (status) => {
        if (status === "KNOWN") return "green"
        else if (status === "NEW") return "red"
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
            <Modal isOpen={isOpen} onClose={onClose} size={"3xl"} isCentered>
                <ModalOverlay/>
                <ModalContent rounded={'lg'}>
                    <ModalCloseButton/>
                    <ModalBody>
                        <div style={{margin: "15px 0", fontSize: "20px", fontWeight: "bold"}}>{wordPackDTO.name}</div>
                        <div style={{margin: "10px 0"}}><CopyIcon/>{wordPackDTO.totalWords}</div>
                        <div style={{margin: "10px 0 30px 0"}}>{wordPackDTO.description}</div>
                        <div ref={containerRef} style={{maxHeight: "65vh", overflowY: "auto", marginBottom: "20px"}}
                             onScroll={handleScroll}>
                            {allWordsForWordPackDTO.slice(0, visibleWords).map((wordDTO, index) => (
                                <div key={index}
                                     style={{
                                         border: "1px solid #ccc",
                                         borderRadius: "10px",
                                         padding: "10px",
                                         marginBottom: "5px"
                                     }}>
                                    <div style={{display: "flex"}}>
                                        <div style={{flex: "1 1 auto", minWidth: 0}}>
                                            <div>{wordDTO.nameChineseSimplified} {wordDTO.pinyin}</div>
                                            <div>{wordDTO.nameEnglish}</div>
                                        </div>
                                        <div style={{flex: "0 0 auto", minWidth: "fit-content"}}>
                                            {wordDTO.status === "IN_REVIEW"
                                                ? (
                                                    Array.from({ length: wordDTO.totalStreak }).map((_, i) => (
                                                        <Badge
                                                            key={i}
                                                            style={{
                                                                width: "10px",
                                                                height: "10px",
                                                                backgroundColor: "rgba(0, 128, 0, 0.5)",
                                                                borderRadius: "50%",
                                                                marginRight: "3px",
                                                            }}
                                                        />
                                                    ))
                                                )
                                                : (<Badge colorScheme={getStatusColor(wordDTO.status)}>{wordDTO.status}</Badge>)}
                                        </div>
                                    </div>
                                </div>
                            ))}
                        </div>
                    </ModalBody>
                </ModalContent>
            </Modal>
        </>
    );
};

export default ReviewWordPackWindow;
