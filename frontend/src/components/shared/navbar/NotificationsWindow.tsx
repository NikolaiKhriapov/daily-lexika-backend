import { Button as ChakraButton, ColorMode, MenuDivider, useColorMode } from '@chakra-ui/react';
import React from 'react';
import styled from 'styled-components/macro';
import { Breakpoint, FontWeight, Size } from '../../../utils/constants';
import Text from '../../common/basic/Text';
import RedDot from '../../common/basic/RedDot';
import Modal from '../../common/complex/Modal';
import { theme } from '../../../utils/theme';
import { mediaBreakpointUp } from '../../../utils/functions';
import { NotificationDTO } from '../../../utils/types';

type Props = {
  isOpen: boolean;
  onClose: () => void;
  formattedDate: (dateString: string) => string;
  formattedDateTime: (dateTimeString: string) => string;
  allNotificationsDTO: NotificationDTO[];
  handleNotificationClick: (notificationDTO: NotificationDTO) => void;
  handleCloseNotificationModal: () => void;
  selectedNotification: NotificationDTO;
};

export default function NotificationsWindow(props: Props) {
  const { isOpen, onClose, formattedDate, formattedDateTime, allNotificationsDTO, handleNotificationClick, handleCloseNotificationModal, selectedNotification } = props;

  const { colorMode } = useColorMode();
  console.log(`${formattedDateTime} ${handleCloseNotificationModal} ${selectedNotification}`);

  return (
    <Modal
      size={Size.LG}
      isOpen={isOpen}
      onClose={onClose}
      header='Notifications'
      body={(
        <Container>
          {allNotificationsDTO.map((notificationDTO, index) => (
            <ButtonContainer key={index}>
              <NotificationButton colorMode={colorMode} onClick={() => handleNotificationClick(notificationDTO)}>
                <SubjectAndDateContainer>
                  <Text
                    size={{ base: Size.SM, md: Size.MD, xl: Size.MD }}
                    fontWeight={notificationDTO.isRead ? FontWeight.NORMAL : FontWeight.SEMIBOLD}
                    textAlign='left'
                  >
                    {notificationDTO.subject}
                  </Text>
                  <Text
                    size={Size.SM}
                    fontWeight={notificationDTO.isRead ? FontWeight.NORMAL : FontWeight.SEMIBOLD}
                    display={{ base: 'none', md: 'unset' }}
                  >
                    {formattedDate(notificationDTO.sentAt)}
                  </Text>
                </SubjectAndDateContainer>
                <RedDotContainer>
                  {!notificationDTO.isRead && <RedDot />}
                </RedDotContainer>
              </NotificationButton>
              {index < allNotificationsDTO.length - 1 && <MenuDivider />}
            </ButtonContainer>
          ))}
        </Container>
      )}
    />
  );
}

const Container = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  gap: 0;
  width: 100%;
  max-width: 300px;

  ${mediaBreakpointUp(Breakpoint.TABLET)} {
    width: 460px;
    max-width: 460px;
  }
`;

const ButtonContainer = styled.div`
  padding-left: 0;
  width: 80vh;
  max-width: 100%;
`;

const NotificationButton = styled(ChakraButton)<{ colorMode: ColorMode }>`
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between !important;
  padding: 0 5px 0 0 !important;
  background-color: ${({ colorMode }) => theme.colors[colorMode].bgColor} !important;
  height: 30px !important;
  width: 250px;

  ${mediaBreakpointUp(Breakpoint.TABLET)} {
    padding: 0 16px !important;
    height: 35px !important;
    width: 460px;
  }

  ${mediaBreakpointUp(Breakpoint.DESKTOP)} {
    height: 40px !important;
  }

  &:hover {
    background-color: ${({ colorMode }) => theme.colors[colorMode].hoverBgColor} !important;
  }
`;

const SubjectAndDateContainer = styled.div`
  display: flex;
  flex-direction: column;
  width: 100%;

  ${mediaBreakpointUp(Breakpoint.TABLET)} {
    flex-direction: row;
    justify-content: space-between !important;
  }
`;

const RedDotContainer = styled.div``;
