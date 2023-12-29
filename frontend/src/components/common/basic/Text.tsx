import React from 'react';
import { Text as ChakraText, TextProps } from '@chakra-ui/react';
import styled from 'styled-components/macro';
import { Size } from '../../../utils/constants';

interface Props extends TextProps {
  size?: Size | { base: Size, md: Size, xl: Size };
  isCentered?: boolean;
}

export default function Text({ children, size, isCentered, ...rest }: Props) {
  return (
    <ChakraTextStyled fontSize={size} $isCentered={isCentered} {...rest}>
      {children}
    </ChakraTextStyled>
  );
}

Text.defaultProps = {
  size: Size.MD,
  isCentered: false,
};

const ChakraTextStyled = styled(ChakraText)<{ $isCentered: boolean }>`
  text-align: ${({ $isCentered }) => $isCentered && 'center'};
`;
