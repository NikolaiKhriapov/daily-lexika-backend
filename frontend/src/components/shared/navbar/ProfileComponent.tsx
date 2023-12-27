import React, { useEffect, useState } from 'react';
import { Avatar, Menu, MenuButton, MenuDivider, useDisclosure } from '@chakra-ui/react';
import styled from 'styled-components/macro';
import MenuList from '../../common/basic/MenuList';
import UserAccountWindow from '../../user/UserAccountWindow';
import MenuItem from '../../common/basic/MenuItem';
import { mediaBreakpointUp } from '../../../utils/functions';
import { Breakpoint } from '../../../utils/constants';
import { useAuth } from '../../context/AuthContext';
import { UserDTO } from '../../../utils/types';
import { showUserAccount } from '../../../services/user';
import { errorNotification } from '../../../services/popup-notification';

export default function ProfileComponent() {
  const { logout } = useAuth();
  const { isOpen, onOpen, onClose } = useDisclosure();
  const [userDTO, setUserDTO] = useState<UserDTO>();

  const fetchUserDTO = () => {
    showUserAccount()
      .then((response) => setUserDTO(response.data.data.userDTO))
      .catch((error) => errorNotification(error.code, error.response.data.message));
  };

  useEffect(() => {
    fetchUserDTO();
  }, []);

  const updateUserAndName = (updatedUserDTO: UserDTO) => setUserDTO(updatedUserDTO);

  return (
    <Menu>
      <MenuButton>
        <AvatarStyled />
      </MenuButton>
      <MenuList>
        <MenuItem onClick={onOpen}>Account</MenuItem>
        {isOpen && (
          <UserAccountWindow
            isOpen={isOpen}
            onClose={onClose}
            userDTO={userDTO!}
            updateUserAndName={updateUserAndName}
          />
        )}
        <MenuDivider />
        <MenuItem onClick={logout}>Log Out</MenuItem>
      </MenuList>
    </Menu>
  );
}

const AvatarStyled = styled(Avatar)`
  height: 35px !important;
  width: 35px !important;
  margin: 2px 7px;

  ${mediaBreakpointUp(Breakpoint.TABLET)} {
    height: 45px !important;
    width: 45px !important;
  }
`;
