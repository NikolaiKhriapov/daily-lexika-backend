import { ColorMode, Menu, MenuButton, useColorMode, useDisclosure, MenuDivider } from '@chakra-ui/react';
import { FiBell } from 'react-icons/fi';
import React, { useEffect, useState } from 'react';
import styled from 'styled-components/macro';
import RedDot from '../../common/basic/RedDot';
import MenuList from '../../common/basic/MenuList';
import Text from '../../common/basic/Text';
import { Breakpoint, ButtonType, FontWeight, Size } from '../../../utils/constants';
import ButtonsContainer from '../../common/complex/ButtonsContainer';
import Button from '../../common/basic/Button';
import { theme } from '../../../utils/theme';
import { NotificationDTO } from '../../../utils/types';
import { getAllNotifications, readNotification } from '../../../services/notifications';
import { mediaBreakpointUp } from '../../../utils/functions';
import MenuItem from '../../common/basic/MenuItem';
import { errorNotification } from '../../../services/popup-notification';
import NotificationsWindow from './NotificationsWindow';
import NotificationWindow from './NotificationWindow';

export default function NotificationsComponent() {
  const { colorMode } = useColorMode();
  const { isOpen, onOpen, onClose } = useDisclosure();
  const [allNotificationsDTO, setAllNotificationsDTO] = useState<NotificationDTO[]>([]);
  const [selectedNotification, setSelectedNotification] = useState<NotificationDTO | null>(null);

  const fetchAllNotificationsDTO = () => {
    getAllNotifications()
      .then((response) => {
        const data: NotificationDTO[] = response.data.data.allNotificationsDTO;
        setAllNotificationsDTO(data.sort((a, b) => new Date(b.sentAt).getTime() - new Date(a.sentAt).getTime()));
      })
      .catch((error) => errorNotification(error.code, error.response.data.message));
  };

  useEffect(() => {
    fetchAllNotificationsDTO();
  }, []);

  const handleNotificationClick = (notificationDTO: NotificationDTO) => {
    readNotification(notificationDTO.notificationId);
    setSelectedNotification(notificationDTO);
    setAllNotificationsDTO((prevNotificationsDTO) => prevNotificationsDTO.map((notification) => {
      if (notification.notificationId === notificationDTO.notificationId) {
        return { ...notification, isRead: true };
      }
      return notification;
    }));
  };

  const handleCloseNotificationModal = () => setSelectedNotification(null);

  const formattedDate = (dateString: string) => {
    const options: Intl.DateTimeFormatOptions = { year: 'numeric', month: 'short', day: 'numeric' };
    return new Date(dateString).toLocaleDateString(undefined, options);
  };

  const formattedDateTime = (dateString: string) => {
    const options: Intl.DateTimeFormatOptions = {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: 'numeric',
      minute: 'numeric',
    };
    return new Date(dateString).toLocaleDateString(undefined, options);
  };

  const unreadNotifications = allNotificationsDTO
    .filter((notificationDTO) => !notificationDTO.isRead);

  return (
    <Notifications colorMode={colorMode}>
      <Menu>
        <MenuButton>
          <ButtonContainer colorMode={colorMode}>
            <FiBell />{unreadNotifications.length > 0 && <RedDot />}
          </ButtonContainer>
        </MenuButton>
        <MenuList width={{ base: 290, md: 400 }}>
          {unreadNotifications.length > 0
            ? (unreadNotifications.map((notificationDTO, index) => (
              <NotificationContainer key={index}>
                <MenuItemStyled onClick={() => handleNotificationClick(notificationDTO)}>
                  <Text
                    size={Size.MD}
                    fontWeight={FontWeight.SEMIBOLD}
                  >
                    {notificationDTO.subject}
                  </Text>
                  <Text
                    size={Size.SM}
                    fontWeight={FontWeight.SEMIBOLD}
                    display={{ base: 'none', md: 'unset' }}
                  >
                    {formattedDate(notificationDTO.sentAt)}
                  </Text>
                </MenuItemStyled>
                {selectedNotification && (
                  <NotificationWindow
                    formattedDateTime={formattedDateTime}
                    handleCloseNotificationModal={handleCloseNotificationModal}
                    selectedNotification={selectedNotification}
                  />
                )}
                {index < allNotificationsDTO.length - 1 && <MenuDivider />}
              </NotificationContainer>
            )))
            : (
              <NoNotificationsContainer>
                <Text>No new notifications</Text>
              </NoNotificationsContainer>
            )}
          <MenuDivider />
          <ButtonsContainer>
            <Button
              buttonText='Show all notifications'
              buttonType={ButtonType.BUTTON}
              size={Size.SM}
              onClick={onOpen}
            />
            {isOpen && (
              <NotificationsWindow
                isOpen={isOpen}
                onClose={onClose}
                formattedDate={formattedDate}
                formattedDateTime={formattedDateTime}
                allNotificationsDTO={allNotificationsDTO}
                handleNotificationClick={handleNotificationClick}
                handleCloseNotificationModal={handleCloseNotificationModal}
                selectedNotification={selectedNotification!}
              />
            )}
          </ButtonsContainer>
        </MenuList>
      </Menu>
    </Notifications>
  );
}

const Notifications = styled.div<{ colorMode: ColorMode }>`
  border-radius: 6px;
  display: flex;
  justify-content: center;
  align-items: center;

  &:hover {
    background-color: ${({ colorMode }) => theme.colors[colorMode].hoverBgColor} !important;
  }
`;

const ButtonContainer = styled.div<{ colorMode: ColorMode }>`
  width: 48px;
  height: 40px;
  display: flex;
  justify-content: center;
  align-items: center;
  padding-left: 6px;
`;

const NotificationContainer = styled.div`
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: space-between;

  ${mediaBreakpointUp(Breakpoint.TABLET)} {
    height: 40px;
  }
`;

const MenuItemStyled = styled(MenuItem)`
  display: flex;
  flex-direction: column;

  ${mediaBreakpointUp(Breakpoint.TABLET)} {
    flex-direction: row;
    justify-content: space-between;
  }
`;

const NoNotificationsContainer = styled.div`
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
`;
