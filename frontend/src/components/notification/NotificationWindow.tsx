import React from 'react';
import {
  Modal, ModalBody, ModalCloseButton, ModalContent, ModalFooter, ModalHeader, ModalOverlay, useColorModeValue,
} from '@chakra-ui/react';

function NotificationWindow({ formatDateTime, handleCloseNotificationModal, selectedNotification }: {
  formatDateTime: any, handleCloseNotificationModal: any, selectedNotification: any
}) {
  const bgColor = useColorModeValue('white', 'rgba(40,40,40)');

  return (
    <div>
      {selectedNotification && (
        <Modal isOpen={!!selectedNotification} onClose={handleCloseNotificationModal} size='2xl' isCentered>
          <ModalOverlay />
          <ModalContent rounded='lg' bgColor={bgColor}>
            <ModalHeader style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'baseline' }}>
              <div>{selectedNotification.subject}</div>
              <div style={{ fontSize: '15px', color: 'lightgray', marginRight: '30px' }}>
                {formatDateTime(selectedNotification.sentAt)}
              </div>
            </ModalHeader>
            <ModalCloseButton />
            <ModalBody>
              <p>{selectedNotification.message}</p>
            </ModalBody>
            <ModalFooter />
          </ModalContent>
        </Modal>
      )}
    </div>
  );
}

export default NotificationWindow;
