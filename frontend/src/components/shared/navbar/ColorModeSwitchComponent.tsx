import { MoonIcon, SunIcon } from '@chakra-ui/icons';
import React from 'react';
import { ColorMode, useColorMode } from '@chakra-ui/react';
import styled from 'styled-components/macro';
import { ButtonType } from '../../../utils/constants';
import Button from '../../common/basic/Button';
import { theme } from '../../../utils/theme';

export default function ColorModeSwitchComponent() {
  const { colorMode, toggleColorMode } = useColorMode();

  return (
    <ButtonStyled
      buttonText={colorMode === 'light' ? <MoonIcon /> : <SunIcon />}
      buttonType={ButtonType.BUTTON}
      onClick={toggleColorMode}
      colorMode={colorMode}
    />
  );
}

const ButtonStyled = styled(Button)<{ colorMode: ColorMode }>`
  background-color: ${({ colorMode }) => theme.colors[colorMode].bgColor} !important;

  &:hover {
    background-color: ${({ colorMode }) => theme.colors[colorMode].hoverBgColor} !important;
  }
`;
