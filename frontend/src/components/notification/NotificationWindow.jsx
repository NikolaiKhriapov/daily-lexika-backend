import React from "react";
import {
    Modal, ModalBody, ModalCloseButton, ModalContent, ModalFooter, ModalHeader, ModalOverlay, useColorModeValue,
} from "@chakra-ui/react";

const NotificationWindow = ({formatDateTime, handleCloseNotificationModal, selectedNotification}) => {

    return (
        <>
            {selectedNotification && (
                <Modal isOpen={!!selectedNotification} onClose={handleCloseNotificationModal} size="2xl" isCentered>
                    <ModalOverlay/>
                    <ModalContent rounded="lg" bg={useColorModeValue('white', 'rgba(40,40,40)')}>
                        <ModalHeader style={{display: 'flex', justifyContent: 'space-between', alignItems: 'baseline'}}>
                            <div>{selectedNotification.subject}</div>
                            <div style={{fontSize: '15px', color: 'lightgray', marginRight: '30px'}}>
                                {formatDateTime(selectedNotification.sentAt)}
                            </div>
                        </ModalHeader>
                        <ModalCloseButton/>
                        <ModalBody>
                            <p>{selectedNotification.message}</p>
                        </ModalBody>
                        <ModalFooter/>
                    </ModalContent>
                </Modal>
            )}
        </>
    );
};

export default NotificationWindow;
