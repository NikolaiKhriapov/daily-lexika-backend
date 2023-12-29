import { ColorMode, MenuList as ChakraMenuList, MenuListProps, useColorMode } from '@chakra-ui/react';
import styled from 'styled-components/macro';
import React from 'react';
import { theme } from '../../../utils/theme';

export default function MenuList({ children, ...rest }: MenuListProps) {
  const { colorMode } = useColorMode();

  return (
    <MenuListStyled $colorMode={colorMode} {...rest}>
      {children}
    </MenuListStyled>
  );
}

const MenuListStyled = styled(ChakraMenuList)<{ $colorMode: ColorMode }>`
  background-color: ${({ $colorMode }) => theme.colors[$colorMode].bgColor} !important;
  margin: 0 15px;
`;
