import { Flex } from '@chakra-ui/react';
import Modal from '../common/complex/Modal';
import Heading from '../common/basic/Heading';
import Text from '../common/basic/Text';
import { TextSize } from '../../utils/constants';

interface NotificationWindowProps {
  formatDateTime: any,
  handleCloseNotificationModal: any,
  selectedNotification: any
}

function NotificationWindow(props: NotificationWindowProps) {
  const { formatDateTime, handleCloseNotificationModal, selectedNotification } = props;

  return (
    <div>
      {selectedNotification && (
        <Modal
          size='2xl'
          isOpen={selectedNotification}
          onClose={handleCloseNotificationModal}
          header={(
            <>
              <Heading level={4} text={selectedNotification.subject} />
              <Flex mr='30px'>
                <Text size={TextSize.SMALL} text={formatDateTime(selectedNotification.sentAt)} />
              </Flex>
            </>
          )}
          body={<Text size={TextSize.MEDIUM} text={selectedNotification.message} />}
        />
      )}
    </div>
  );
}

export default NotificationWindow;
