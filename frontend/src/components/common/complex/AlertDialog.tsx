import {
  AlertDialog as ChakraAlertDialog, AlertDialogBody, AlertDialogContent, AlertDialogFooter, AlertDialogHeader,
  AlertDialogOverlay, Flex, useColorMode,
} from '@chakra-ui/react';
import { ButtonSize, ButtonType } from '../../../utils/constants';
import Button from '../basic/Button';

interface AlertDialogProps {
  isOpenDeleteButton: any,
  onCloseDeleteButton: any,
  cancelRef: any,
  handleDelete: any,
  header: string,
  body: string,
  deleteButtonText: string,
}

function AlertDialog(props: AlertDialogProps) {
  const { isOpenDeleteButton, onCloseDeleteButton, cancelRef, handleDelete, header, body, deleteButtonText } = props;

  const { colorMode } = useColorMode();

  return (
    <ChakraAlertDialog isOpen={isOpenDeleteButton} onClose={onCloseDeleteButton} leastDestructiveRef={cancelRef}>
      <AlertDialogOverlay>
        <AlertDialogContent className={`alertDialogContent ${colorMode}`}>
          <AlertDialogHeader>{header}</AlertDialogHeader>
          <AlertDialogBody>{body}</AlertDialogBody>
          <AlertDialogFooter>
            <Flex className='buttons_container'>
              <Button
                content='Cancel'
                size={ButtonSize.MEDIUM}
                type={ButtonType.BUTTON}
                onClick={onCloseDeleteButton}
                ref={cancelRef}
              />
              <Button
                content={deleteButtonText}
                size={ButtonSize.MEDIUM}
                type={ButtonType.BUTTON_RED}
                onClick={() => handleDelete()}
              />
            </Flex>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialogOverlay>
    </ChakraAlertDialog>
  );
}

export default AlertDialog;
