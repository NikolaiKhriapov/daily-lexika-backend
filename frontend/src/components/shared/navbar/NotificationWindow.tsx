import styled from 'styled-components/macro';
import Text from '../../common/basic/Text';
import Modal from '../../common/complex/Modal';
import { Size } from '../../../utils/constants';

type Props = {
  formattedDateTime: any;
  handleCloseNotificationModal: any;
  selectedNotification: any;
};

export default function NotificationWindow(props: Props) {
  const { formattedDateTime, handleCloseNotificationModal, selectedNotification } = props;

  return (
    <Modal
      size={Size.XXL}
      isOpen={selectedNotification}
      onClose={handleCloseNotificationModal}
      header={(
        <Container>
          {selectedNotification.subject}
          <Text size={Size.XS}>{formattedDateTime(selectedNotification.sentAt)}</Text>
        </Container>
      )}
      body={<Text>{selectedNotification.message}</Text>}
    />
  );
}

const Container = styled.div``;
