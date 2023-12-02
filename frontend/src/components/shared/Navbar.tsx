import { useEffect, useState } from 'react';
import {
  Avatar, Button as ChakraButton, Flex, Menu, MenuButton, MenuDivider, MenuItem, MenuList, useColorMode, useDisclosure,
} from '@chakra-ui/react';
import { MoonIcon, SunIcon } from '@chakra-ui/icons';
import { FiBell } from 'react-icons/fi';
import { NotificationDTO, UserDTO } from '../../types/types';
import { showUserAccount } from '../../services/user';
import { errorNotification } from '../../services/popup-notification';
import { getAllNotifications, readNotification } from '../../services/notifications';
import UserAccountWindow from '../user/UserAccountWindow';
import { ButtonSize, ButtonType, TextSize } from '../../utils/constants';
import Modal from '../common/complex/Modal';
import Button from '../common/basic/Button';
import NotificationWindow from '../notification/NotificationWindow';
import { useAuth } from '../context/AuthContext';
import RedDot from '../common/basic/RedDot';
import Text from '../common/basic/Text';

function Navbar() {
  const { colorMode, toggleColorMode } = useColorMode();
  const { logout } = useAuth();
  const [userDTO, setUserDTO] = useState<UserDTO>();
  const [allNotificationsDTO, setAllNotificationsDTO] = useState<NotificationDTO[]>([]);
  const [selectedNotification, setSelectedNotification] = useState<NotificationDTO | null>(null);
  const { isOpen: isOpenAccount, onOpen: onOpenAccount, onClose: onCloseAccount } = useDisclosure();
  const {
    isOpen: isOpenAllNotifications,
    onOpen: onOpenAllNotifications,
    onClose: onCloseAllNotifications,
  } = useDisclosure();

  const fetchUserDTO = () => {
    showUserAccount()
      .then((response) => setUserDTO(response.data.data.userDTO))
      .catch((error) => errorNotification(error.code, error.response.data.message));
  };

  const fetchAllNotificationsDTO = () => {
    getAllNotifications()
      .then((response) => {
        const data: NotificationDTO[] = response.data.data.allNotificationsDTO;
        setAllNotificationsDTO(data.sort((a, b) => new Date(b.sentAt).getTime() - new Date(a.sentAt).getTime()));
      })
      .catch((error) => errorNotification(error.code, error.response.data.message));
  };

  useEffect(() => {
    fetchUserDTO();
    fetchAllNotificationsDTO();
  }, []);

  const updateUserAndName = (updatedUserDTO: UserDTO) => setUserDTO(updatedUserDTO);

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

  function formatDate(dateString: string) {
    const options: Intl.DateTimeFormatOptions = { year: 'numeric', month: 'short', day: 'numeric' };
    return new Date(dateString).toLocaleDateString(undefined, options);
  }

  function formatDateTime(dateString: string) {
    const options: Intl.DateTimeFormatOptions = {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: 'numeric',
      minute: 'numeric',
    };
    return new Date(dateString).toLocaleDateString(undefined, options);
  }

  const totalUnreadNotifications = allNotificationsDTO
    .filter((notification) => !notification.isRead).length;

  return (
    <Flex className={`Navbar_container ${colorMode}`}>
      <Flex className='notifications'>
        <Menu>
          <MenuButton>
            <FiBell />{totalUnreadNotifications > 0 && <RedDot top='-20px' right='-12px' />}
          </MenuButton>
          <MenuList className={`notificationsList ${colorMode}`}>
            {totalUnreadNotifications > 0
              ? (allNotificationsDTO
                .filter((notificationDTO) => !notificationDTO.isRead)
                .map((notificationDTO, index) => (
                  <Flex className='notification_container' key={index}>
                    <MenuItem
                      className={`notification ${colorMode}`}
                      onClick={() => handleNotificationClick(notificationDTO)}
                    >
                      <Text size={TextSize.MEDIUM} text={notificationDTO.subject} isBold />
                      <Text size={TextSize.SMALL} text={formatDate(notificationDTO.sentAt)} isBold />
                    </MenuItem>
                    {index < allNotificationsDTO.length - 1 && <MenuDivider />}
                  </Flex>
                )))
              : (
                <Flex className='noNotifications_container'>
                  <Text size={TextSize.MEDIUM} text='No new notifications' />
                </Flex>
              )}
            <MenuDivider />
            <Flex className='buttons_container'>
              <Button content='Show all notifications' type={ButtonType.BUTTON} onClick={onOpenAllNotifications} />
            </Flex>
            <Modal
              size='lg'
              isOpen={isOpenAllNotifications}
              onClose={onCloseAllNotifications}
              header='Notifications'
              body={(
                <Flex className='notifications_container' gap='0'>
                  {allNotificationsDTO.map((notificationDTO, index) => (
                    <Flex key={index}>
                      <ChakraButton
                        className={`notification ${colorMode}`}
                        onClick={() => handleNotificationClick(notificationDTO)}
                      >
                        <Text size={TextSize.MEDIUM} text={notificationDTO.subject} isBold />
                        {!notificationDTO.isRead && <RedDot top='-10px' right='-125px' />}
                        <Text size={TextSize.SMALL} text={formatDate(notificationDTO.sentAt)} isBold />
                      </ChakraButton>
                      {index < allNotificationsDTO.length - 1 && <MenuDivider />}
                    </Flex>
                  ))}
                </Flex>
              )}
            />
            <NotificationWindow
              formatDateTime={(dateString: string) => formatDateTime(dateString)}
              handleCloseNotificationModal={handleCloseNotificationModal}
              selectedNotification={selectedNotification}
            />
          </MenuList>
        </Menu>
      </Flex>
      <Button
        onClick={toggleColorMode}
        size={ButtonSize.MEDIUM}
        type={ButtonType.BUTTON}
        content={colorMode === 'light' ? <MoonIcon /> : <SunIcon />}
      />
      <Flex className='profile'>
        <Menu>
          <MenuButton><Avatar /></MenuButton>
          <MenuList className={`menuList ${colorMode}`}>
            <UserAccountWindow
              button={<MenuItem className={`menuItem ${colorMode}`} onClick={onOpenAccount}>Account</MenuItem>}
              isOpen={isOpenAccount}
              onClose={onCloseAccount}
              userDTO={userDTO!}
              updateUserAndName={updateUserAndName}
            />
            <MenuDivider />
            <MenuItem className={`menuItem ${colorMode}`} onClick={logout}>Log Out</MenuItem>
          </MenuList>
        </Menu>
      </Flex>
      <Flex className='space' />
    </Flex>
  );
}

export default Navbar;
