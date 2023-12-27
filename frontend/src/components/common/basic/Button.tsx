import React from 'react';
import { Button as ChakraButton, ButtonProps, ColorMode, useColorMode } from '@chakra-ui/react';
import styled from 'styled-components/macro';
import { theme } from '../../../utils/theme';
import { ButtonType } from '../../../utils/constants';

interface Props extends ButtonProps {
  buttonType: ButtonType;
  buttonText: React.ReactNode;
}

export default function Button({ buttonType, buttonText, ...rest }: Props) {
  const { colorMode } = useColorMode();

  const getButtonType = () => {
    switch (buttonType) {
      case ButtonType.SUBMIT:
        return 'submit';
      case ButtonType.RESET:
        return 'reset';
      default:
        return 'button';
    }
  };

  return (
    <ChakraButtonStyled
      className={`${buttonType}`}
      type={getButtonType()}
      colorMode={colorMode}
      {...rest}
    >
      {buttonText}
    </ChakraButtonStyled>
  );
}

const ChakraButtonStyled = styled(ChakraButton)<{ colorMode: ColorMode }>`
  color: ${({ colorMode }) => theme.colors[colorMode].buttonColor};
  background-color: ${({ colorMode }) => theme.colors[colorMode].buttonBgColor};
  
  &.standard:hover {
    background-color: ${({ colorMode }) => theme.colors[colorMode].buttonHoverBgColor};
  }

  &.redOnHover:hover {
    background-color: ${({ colorMode }) => theme.colors[colorMode].buttonRemoveHoverBgColor};
  }
`;
