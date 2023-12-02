import {
  Modal as ChakraModal, ModalBody, ModalCloseButton, ModalContent, ModalFooter, ModalHeader, ModalOverlay, useColorMode,
} from '@chakra-ui/react';

interface ModalProps {
  size: 'xs' | 'sm' | 'md' | 'lg' | 'xl' | 'full' | '2xl' | '3xl' | '4xl' | '5xl' | '6xl';
  isOpen: boolean;
  onClose: any;
  header: any;
  body: any;
}

function Modal(props: ModalProps) {
  const { isOpen, onClose, size, header, body } = props;

  const { colorMode } = useColorMode();

  return (
    <ChakraModal isOpen={isOpen} onClose={onClose} size={size} isCentered>
      <ModalOverlay />
      <ModalContent className={`Modal__ModalContent ${colorMode}`}>
        <ModalCloseButton />
        <ModalHeader className='Modal__ModalHeader'>{header}</ModalHeader>
        <ModalBody className='Modal__ModalBody'>{body}</ModalBody>
        <ModalFooter />
      </ModalContent>
    </ChakraModal>
  );
}

export default Modal;
