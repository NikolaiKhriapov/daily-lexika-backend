import {
  ColorMode,
  Modal as ChakraModal,
  ModalBody,
  ModalCloseButton,
  ModalContent,
  ModalFooter,
  ModalHeader,
  ModalOverlay, ModalProps,
  useColorMode,
} from '@chakra-ui/react';
import styled from 'styled-components/macro';
import React from 'react';
import { theme } from '../../../utils/theme';
import { borderStyles, hiddenScrollbar, mediaBreakpointUp } from '../../../utils/functions';
import { Breakpoint, Size } from '../../../utils/constants';

interface Props extends Omit<ModalProps, 'children'> {
  header: React.ReactNode;
  body: React.ReactNode;
}

export default function Modal({ header, body, ...rest }: Props) {
  const { colorMode } = useColorMode();

  return (
    <ChakraModal isCentered {...rest} size={Size.XXXL}>
      <ModalOverlay />
      <ModalContentStyled $colorMode={colorMode}>
        <ModalCloseButton />
        <ModalHeaderStyled fontSize={Size.XL}>{header}</ModalHeaderStyled>
        <ModalBodyStyled>{body}</ModalBodyStyled>
        <ModalFooterStyled />
      </ModalContentStyled>
    </ChakraModal>
  );
}

const ModalContentStyled = styled(ModalContent)<{ $colorMode: ColorMode }>`
  padding: 6px;
  background-color: ${({ $colorMode }) => theme.colors[$colorMode].bgColor} !important;
  border: ${({ $colorMode }) => borderStyles($colorMode)};
  border-radius: ${theme.stylesToDelete.borderRadius};
  max-width: 90% !important;
  max-height: 90vh;
  width: fit-content !important;
  overflow-y: auto;
  ${hiddenScrollbar};
`;

const ModalHeaderStyled = styled(ModalHeader)`
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  margin-right: 30px;
  padding: 8px 12px !important;

  ${mediaBreakpointUp(Breakpoint.TABLET)} {
    padding: 16px 24px !important;
  }
`;

const ModalBodyStyled = styled(ModalBody)`
  display: flex;
  flex-direction: column;
  gap: 20px;
  padding: 8px 12px !important;

  ${mediaBreakpointUp(Breakpoint.TABLET)} {
    padding: 8px 24px !important;
  }
`;

const ModalFooterStyled = styled(ModalFooter)`
  padding: 4px 12px !important;

  ${mediaBreakpointUp(Breakpoint.TABLET)} {
    padding: 8px 24px !important;
  }
`;
