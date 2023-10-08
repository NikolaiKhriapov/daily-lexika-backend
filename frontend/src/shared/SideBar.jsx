import React, {useEffect, useState} from 'react'
import {
    Avatar, Box, Button, Drawer, DrawerContent, Flex, HStack, Icon, IconButton, Link, Menu, MenuButton, MenuDivider,
    MenuItem, MenuList, Modal, ModalBody, ModalCloseButton, ModalContent, ModalFooter, ModalHeader, ModalOverlay, Text,
    useColorMode, useColorModeValue, useDisclosure,
} from '@chakra-ui/react'
import {FiBell, FiMenu} from 'react-icons/fi'
import {useAuth} from "../components/context/AuthContext.jsx"
import {MoonIcon, SunIcon} from "@chakra-ui/icons"
import {showUserAccount} from "../services/user.js"
import {errorNotification} from "../services/popup-notification.js"
import UserAccountWindow from "../components/user/UserAccountWindow.jsx"
import {getAllNotifications, readNotification} from "../services/notification.js"
import NotificationWindow from "../components/notification/NotificationWindow.jsx"

const LinkItems = [
    {name: 'Daily Reviews', route: '/reviews', icon: FiMenu},
    {name: 'Word Packs', route: '/word-packs', icon: FiMenu},
    {name: 'Statistics', route: '/statistics', icon: FiMenu}
]

export default function SidebarWithHeader({children}) {
    const {isOpen, onOpen, onClose} = useDisclosure()
    return (
        <Box minH="100vh" bg={useColorModeValue('rgba(237,238,240)', 'rgba(20,20,20)')}>
            <SidebarContent onClose={onClose} display={{base: 'none', md: 'block'}}/>
            <Drawer
                isOpen={isOpen} onClose={onClose} onOverlayClick={onClose}
                autoFocus={false} returnFocusOnClose={false}
                placement="left" size="xs">
                <DrawerContent>
                    <SidebarContent onClose={onClose}/>
                </DrawerContent>
            </Drawer>
            <MobileNav onOpen={onOpen}/>
            <Box ml={{base: 0, md: 60}} p="4">{children}</Box>
        </Box>
    )
}

const SidebarContent = ({onClose, ...rest}) => {
    return (
        <Box
            w={{base: 'full', md: 60}} pos="fixed" h="full"
            bg={useColorModeValue('rgba(237,238,240)', 'rgba(20,20,20)')}
            {...rest}>
            <Flex alignItems="center" py={{base: '15px', md: '35'}} bg={useColorModeValue('white', 'rgba(40,40,40)')}>
                <IconButton display={{base: 'flex', md: 'none'}} ml={4} variant="outline" aria-label="close menu"
                            onClick={onClose}
                ><FiMenu/></IconButton>
            </Flex>
            <Flex py={'10px'}/>
            {LinkItems.map((link) => (
                <NavItem key={link.name} route={link.route} icon={link.icon}>
                    {link.name}
                </NavItem>
            ))}
        </Box>
    )
}

const NavItem = ({icon, route, children, ...rest}) => {
    return (
        <Link href={route} style={{textDecoration: 'none', colorScheme: 'black'}} _focus={{boxShadow: 'none'}}>
            <Flex
                align="center" p="4" mx="4" borderRadius="lg" role="group" cursor="pointer"
                _hover={{
                    bg: useColorModeValue('gray.300', 'rgba(40,40,40)'),
                    color: useColorModeValue('black', 'white')
                }}
                {...rest}
            >
                {icon && (<Icon mr="4" fontSize="16" as={icon}/>)}
                {children}
            </Flex>
        </Link>
    )
}

const MobileNav = ({onOpen, ...rest}) => {

    const {colorMode, toggleColorMode} = useColorMode()
    const {logout, user} = useAuth()
    const [userDTO, setUserDTO] = useState([])
    const [allNotificationsDTO, setAllNotificationsDTO] = useState([])
    const [selectedNotification, setSelectedNotification] = useState(null)
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
                const allNotificationsDTO = response.data.data.allNotificationsDTO
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
                    return {...notification, isRead: true}
                }
                return notification
            })
        })
    }
    const handleCloseNotificationModal = () => setSelectedNotification(null)

    function formatDate(dateString) {
        const options = {year: 'numeric', month: 'short', day: 'numeric'}
        return new Date(dateString).toLocaleDateString(undefined, options)
    }

    function formatDateTime(dateString) {
        const options = {year: 'numeric', month: 'short', day: 'numeric', hour: 'numeric', minute: 'numeric'}
        return new Date(dateString).toLocaleDateString(undefined, options)
    }

    const totalUnreadNotifications = allNotificationsDTO
        .filter((notification) => notification.isRead !== true).length

    return (
        <Flex
            ml={{base: 0, md: 60}} px={{base: 4, md: 4}} height="70px" alignItems="center"
            justifyContent={{base: 'space-between', md: 'flex-end'}}
            bg={useColorModeValue('white', 'rgba(40,40,40)')}
            {...rest}>
            <IconButton display={{base: 'flex', md: 'none'}} variant="outline" aria-label="open menu" onClick={onOpen}>
                <FiMenu/>
            </IconButton>
            <HStack spacing="6">
                <Flex alignItems={'center'}>
                    <Menu>
                        <MenuButton>
                            <FiBell/>
                            {totalUnreadNotifications > 0
                                && (<Box w="7px" h="7px" bg="red.500" borderRadius="full"
                                         position="absolute" mx={"3"} my={"-5"}/>)}
                        </MenuButton>
                        <MenuList w={'400px'}
                                  bg={useColorModeValue('white', 'rgba(40,40,40)')}
                                  borderColor={useColorModeValue('gray.300', 'gray.700')}
                                  _hover={{
                                      bg: useColorModeValue('white', 'rgba(40,40,40)'),
                                      borderColor: useColorModeValue('gray.300', 'gray.700')
                                  }}
                        >
                            {totalUnreadNotifications > 0
                                ? (allNotificationsDTO
                                    .filter((notificationDTO) => notificationDTO.isRead !== true)
                                    .map((notificationDTO, index) => (
                                        <React.Fragment key={index}>
                                            <Button w={400} style={{justifyContent: 'space-between'}} rounded={'none'}
                                                    bg={'none'}
                                                    onClick={() => handleNotificationClick(notificationDTO)}
                                            >
                                                <div>{notificationDTO.subject}</div>
                                                <div>
                                                    <Box w="7px" h="7px" bg="red.500" borderRadius="full"
                                                         position="absolute" top="7px" right="7px"/>
                                                </div>
                                                <div style={{fontSize: '14px', color: 'lightgray', marginRight: '5px'}}>
                                                    {formatDate(notificationDTO.sentAt)}
                                                </div>
                                            </Button>
                                            {index < allNotificationsDTO.length - 1 && <MenuDivider/>}
                                        </React.Fragment>
                                    )))
                                : (<Text textAlign="center" my={'5'}>No new notifications</Text>)
                            }
                            <MenuDivider/>
                            <Flex justifyContent={"center"}>
                                <Button onClick={onOpenAllNotifications} size={'sm'}>Show all notifications</Button>
                            </Flex>
                            <Modal isOpen={isOpenAllNotifications} onClose={onCloseAllNotifications} size={"lg"}
                                   isCentered>
                                <ModalOverlay/>
                                <ModalContent rounded={"lg"} bg={useColorModeValue('white', 'rgba(40,40,40)')}>
                                    <ModalCloseButton/>
                                    <ModalHeader>Notifications</ModalHeader>
                                    <ModalBody>
                                        {allNotificationsDTO.map((notificationDTO, index) => (
                                            <React.Fragment key={index}>
                                                <Button w={460} style={{justifyContent: 'space-between'}}
                                                        bg={'none'}
                                                        onClick={() => handleNotificationClick(notificationDTO)}>
                                                    <div>{notificationDTO.subject}</div>
                                                    <div>
                                                        {!notificationDTO.isRead && (
                                                            <Box w="7px" h="7px" bg="red.500" borderRadius="full"
                                                                 position="absolute" top="7px" right="7px"/>
                                                        )}
                                                    </div>
                                                    <div style={{ fontSize: '14px', color: 'lightgray', marginRight: '5px'}}>
                                                        {formatDate(notificationDTO.sentAt)}
                                                    </div>
                                                </Button>
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
                            _focus={{boxShadow: 'none'}}
                        >
                            <HStack>
                                <Avatar size="md"/>
                                <Box width={"5"}/>
                            </HStack>
                        </MenuButton>
                        <MenuList
                            bg={useColorModeValue('white', 'rgba(40,40,40)')}
                            borderColor={useColorModeValue('gray.300', 'gray.700')}
                        >
                            <UserAccountWindow
                                button={
                                    <MenuItem onClick={onOpenAccount}
                                              bg={useColorModeValue('white', 'rgba(40,40,40)')}
                                              _hover={{
                                                  bg: useColorModeValue('gray.200', 'rgba(60,60,60)'),
                                                  borderColor: useColorModeValue('gray.300', 'gray.700')
                                              }}
                                    >Account</MenuItem>
                                }
                                isOpen={isOpenAccount}
                                onClose={onCloseAccount}
                                userDTO={userDTO}
                                updateUserAndName={updateUserAndName}
                            />
                            <MenuDivider/>
                            <MenuItem onClick={logout}
                                      bg={useColorModeValue('white', 'rgba(40,40,40)')}
                                      _hover={{
                                          bg: useColorModeValue('gray.200', 'rgba(60,60,60)'),
                                          borderColor: useColorModeValue('gray.300', 'gray.700')
                                      }}
                            >Log
                                Out</MenuItem>
                        </MenuList>
                    </Menu>
                </Flex>
            </HStack>
        </Flex>
    )
}