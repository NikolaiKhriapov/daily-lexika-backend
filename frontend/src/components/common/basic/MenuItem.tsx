import { ColorMode, MenuItem as ChakraMenuItem, MenuItemProps, useColorMode } from '@chakra-ui/react';
import styled from 'styled-components/macro';
import React from 'react';
import { theme } from '../../../utils/theme';

export default function MenuItem({ children, ...rest }: MenuItemProps) {
  const { colorMode } = useColorMode();

  return (
    <MenuItemStyled colorMode={colorMode} {...rest}>
      {children}
    </MenuItemStyled>
  );
}

const MenuItemStyled = styled(ChakraMenuItem)<{ colorMode: ColorMode }>`
  background-color: ${({ colorMode }) => theme.colors[colorMode].bgColor} !important;

  &:hover {
    background-color: ${({ colorMode }) => theme.colors[colorMode].hoverBgColor} !important;
  }
`;
