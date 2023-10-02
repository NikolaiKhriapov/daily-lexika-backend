import React, {useEffect, useState} from 'react';
import {
    Avatar, Box, Button, CloseButton, Drawer, DrawerContent, Flex, HStack, Icon, IconButton, Link, Menu, MenuButton,
    MenuDivider, MenuItem, MenuList, Modal, ModalBody, ModalCloseButton, ModalContent, ModalFooter, ModalHeader,
    ModalOverlay, Text, useColorMode, useColorModeValue, useDisclosure,
} from '@chakra-ui/react';
import {FiBell, FiMenu} from 'react-icons/fi';
import {useAuth} from "../components/context/AuthContext.jsx";
import {MoonIcon, SunIcon} from "@chakra-ui/icons";
import {showUserAccount} from "../services/user.js";
import {errorNotification} from "../services/popup-notification.js";
import UserAccountWindow from "../components/user/UserAccountWindow.jsx";
import {getAllNotifications, readNotification} from "../services/notification.js";
import NotificationWindow from "../components/notification/NotificationWindow.jsx";

const LinkItems = [
    {name: 'Daily Reviews', route: '/reviews', icon: FiMenu},
    {name: 'Word Packs', route: '/word-packs', icon: FiMenu},
    {name: 'Statistics', route: '/statistics', icon: FiMenu}
]

export default function SidebarWithHeader({children}) {
    const {isOpen, onOpen, onClose} = useDisclosure();
    return (
        <Box minH="100vh" bg={useColorModeValue('gray.100', 'gray.900')}>
            <SidebarContent onClose={onClose} display={{base: 'none', md: 'block'}}/>
            <Drawer
                isOpen={isOpen} onClose={onClose} onOverlayClick={onClose}
                autoFocus={false} returnFocusOnClose={false}
                placement="left" size="full">
                <DrawerContent>
                    <SidebarContent onClose={onClose}/>
                </DrawerContent>
            </Drawer>
            <MobileNav onOpen={onOpen}/>
            <Box ml={{base: 0, md: 60}} p="4">{children}</Box>
        </Box>
    );
}

const SidebarContent = ({onClose, ...rest}) => {
    return (
        <Box
            transition="3s ease"
            bg={useColorModeValue('white', 'gray.900')}
            borderRight="1px"
            borderRightColor={useColorModeValue('gray.200', 'gray.700')}
            w={{base: 'full', md: 60}}
            pos="fixed"
            h="full"
            {...rest}>
            <Flex flexDirection="column" alignItems="center" mt={5} mb={6}
                  justifyContent="space-between">
                <Text fontSize="2xl" fontFamily="monospace" fontWeight="bold">
                    Dashboard
                </Text>
                <CloseButton display={{base: 'flex', md: 'none'}} onClick={onClose}/>
            </Flex>
            {LinkItems.map((link) => (
                <NavItem key={link.name} route={link.route} icon={link.icon}>
                    {link.name}
                </NavItem>
            ))}
        </Box>
    );
};

const NavItem = ({icon, route, children, ...rest}) => {
    return (
        <Link href={route} style={{textDecoration: 'none'}} _focus={{boxShadow: 'none'}}>
            <Flex
                align="center" p="4" mx="4" borderRadius="lg" role="group" cursor="pointer"
                _hover={{bg: 'red.400', color: 'white'}}
                {...rest}
            >
                {icon && (<Icon mr="4" fontSize="16" _groupHover={{color: 'white'}} as={icon}/>)}
                {children}
            </Flex>
        </Link>
    );
};

const MobileNav = ({onOpen, ...rest}) => {

        const {colorMode, toggleColorMode} = useColorMode();
        const {logout, user} = useAuth();
        const [userDTO, setUserDTO] = useState([]);
        const [allNotificationsDTO, setAllNotificationsDTO] = useState([]);
        const [selectedNotification, setSelectedNotification] = useState(null);
        const {isOpen: isOpenAccount, onOpen: onOpenAccount, onClose: onCloseAccount} = useDisclosure()
        const {
            isOpen: isOpenAllNotifications,
            onOpen: onOpenAllNotifications,
            onClose: onCloseAllNotifications
        } = useDisclosure()

        const fetchUserDTO = () => {
            showUserAccount()
                .then(response => setUserDTO(response.data.data.userDTO))
                .catch(error => errorNotification(error.code, error.response.data.message))
        }

        const fetchAllNotificationsDTO = () => {
            getAllNotifications()
                .then(response => {
                    const allNotificationsDTO = response.data.data.allNotificationsDTO;
                    const allNotificationsDTOSorted = allNotificationsDTO.slice()
                        .sort((a, b) => {
                            return new Date(b.sentAt).getTime() - new Date(a.sentAt).getTime()
                        })
                    setAllNotificationsDTO(allNotificationsDTOSorted)
                })
                .catch(error => errorNotification(error.code, error.response.data.message))
        }

        useEffect(() => {
            fetchUserDTO()
            fetchAllNotificationsDTO()
        }, [])

        const updateUserAndName = (updatedUserDTO) => setUserDTO(updatedUserDTO)

        const handleNotificationClick = (notificationDTO) => {
            readNotification(notificationDTO.notificationId)
            setSelectedNotification(notificationDTO)
            setAllNotificationsDTO(prevNotificationsDTO => {
                return prevNotificationsDTO.map(notification => {
                    if (notification.notificationId === notificationDTO.notificationId) {
                        return {...notification, isRead: true};
                    }
                    return notification;
                });
            });
        };
        const handleCloseNotificationModal = () => setSelectedNotification(null)

        function formatDate(dateString) {
            const options = {year: 'numeric', month: 'short', day: 'numeric'};
            return new Date(dateString).toLocaleDateString(undefined, options);
        }

        function formatDateTime(dateString) {
            const options = {year: 'numeric', month: 'short', day: 'numeric', hour: 'numeric', minute: 'numeric'};
            return new Date(dateString).toLocaleDateString(undefined, options);
        }

        const totalUnreadNotifications = allNotificationsDTO
            .filter((notification) => notification.isRead !== true).length

        return (
            <Flex
                ml={{base: 0, md: 60}} px={{base: 4, md: 4}} height="20" alignItems="center"
                borderBottomWidth="1px" borderBottomColor={useColorModeValue('gray.200', 'gray.700')}
                justifyContent={{base: 'space-between', md: 'flex-end'}}
                bg={useColorModeValue('white', 'gray.900')}
                {...rest}>
                <IconButton
                    display={{base: 'flex', md: 'none'}} variant="outline" aria-label="open menu" onClick={onOpen}
                />

                <HStack spacing={{base: '0', md: '6'}}>
                    <Flex alignItems={'center'}>
                        <Menu>
                            <MenuButton>
                                <FiBell/>
                                {totalUnreadNotifications > 0
                                    && (<Box w="7px" h="7px" bg="red.500" borderRadius="full"
                                             position="absolute" mx={"3"} my={"-5"}/>)}
                            </MenuButton>
                            <MenuList bg={useColorModeValue('white', 'gray.900')} minWidth="400px"
                                      borderColor={useColorModeValue('gray.200', 'gray.700')}>
                                {totalUnreadNotifications > 0
                                    ? (allNotificationsDTO
                                        .filter((notificationDTO) => notificationDTO.isRead !== true)
                                        .map((notificationDTO, index) => (
                                            <React.Fragment key={index}>
                                                <MenuItem position="relative" style={{justifyContent: 'space-between'}}
                                                          onClick={() => handleNotificationClick(notificationDTO)}>
                                                    <div>{notificationDTO.subject}</div>
                                                    <div>
                                                        <Box w="7px" h="7px" bg="red.500" borderRadius="full"
                                                             position="absolute" top="3px" right="10px"/>
                                                    </div>
                                                    <div
                                                        style={{fontSize: '14px', color: 'lightgray', marginRight: '15px'}}>
                                                        {formatDate(notificationDTO.sentAt)}
                                                    </div>
                                                </MenuItem>
                                                {index < allNotificationsDTO.length - 1 && <MenuDivider/>}
                                            </React.Fragment>
                                        )))
                                    : (<Text textAlign="center" my={'5'}>No new notifications</Text>)
                                }
                                <Flex justifyContent={"center"}>
                                    <Button onClick={onOpenAllNotifications} size={'sm'}>Show all notifications</Button>
                                </Flex>
                                <Modal isOpen={isOpenAllNotifications} onClose={onCloseAllNotifications} size={"lg"}
                                       isCentered>
                                    <ModalOverlay/>
                                    <ModalContent rounded={"lg"}>
                                        <ModalCloseButton/>
                                        <ModalHeader>Notifications</ModalHeader>
                                        <ModalBody>
                                            {allNotificationsDTO.map((notificationDTO, index) => (
                                                <React.Fragment key={index}>
                                                    <MenuItem position="relative" style={{justifyContent: 'space-between'}}
                                                              onClick={() => handleNotificationClick(notificationDTO)}>
                                                        <div>{notificationDTO.subject}</div>
                                                        <div>
                                                            {!notificationDTO.isRead && (
                                                                <Box w="7px" h="7px" bg="red.500" borderRadius="full"
                                                                     position="absolute" top="3px" right="10px"/>
                                                            )}
                                                        </div>
                                                        <div style={{
                                                            fontSize: '14px',
                                                            color: 'lightgray',
                                                            marginRight: '15px'
                                                        }}>
                                                            {formatDate(notificationDTO.sentAt)}
                                                        </div>
                                                    </MenuItem>
                                                    {index < allNotificationsDTO.length - 1 && <MenuDivider/>}
                                                </React.Fragment>
                                            ))}
                                        </ModalBody>
                                        <ModalFooter/>
                                    </ModalContent>
                                </Modal>
                                <NotificationWindow
                                    formatDateTime={formatDateTime}
                                    handleCloseNotificationModal={handleCloseNotificationModal}
                                    selectedNotification={selectedNotification}
                                />
                            </MenuList>
                            <br/>
                        </Menu>
                    </Flex>

                    <Button onClick={toggleColorMode}>
                        {colorMode === 'light' ? <MoonIcon/> : <SunIcon/>}
                    </Button>

                    <Flex alignItems={'center'}>
                        <Menu>
                            <MenuButton
                                py={2}
                                transition="all 0.3s"
                                _focus={{boxShadow: 'none'}}>
                                <HStack>
                                    <Avatar size="md"/>
                                    <Box width={"5"}/>
                                </HStack>
                            </MenuButton>
                            <MenuList
                                bg={useColorModeValue('white', 'gray.900')}
                                borderColor={useColorModeValue('gray.200', 'gray.700')}>
                                <UserAccountWindow
                                    button={<MenuItem onClick={onOpenAccount}>Account</MenuItem>}
                                    isOpen={isOpenAccount}
                                    onClose={onCloseAccount}
                                    userDTO={userDTO}
                                    updateUserAndName={updateUserAndName}
                                />
                                <MenuDivider/>
                                <MenuItem onClick={logout}>Log Out</MenuItem>
                            </MenuList>
                        </Menu>
                    </Flex>
                </HStack>
            </Flex>
        );
    }
;