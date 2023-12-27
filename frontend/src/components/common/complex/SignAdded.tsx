import React from 'react';
import styled from 'styled-components/macro';
import { FaCheck } from 'react-icons/fa';
import { ColorMode, useColorMode } from '@chakra-ui/react';
import { theme } from '../../../utils/theme';
import { borderStyles } from '../../../utils/functions';
import Text from '../basic/Text';
import { Size } from '../../../utils/constants';

export default function SignAdded() {
  const { colorMode } = useColorMode();

  return (
    <Component colorMode={colorMode}>
      <FaCheck />
      <Text size={Size.SM}>&nbsp;&nbsp;Added</Text>
    </Component>
  );
}

const Component = styled.div<{ colorMode: ColorMode }>`
  width: 90px;
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: center;
  padding: 6px;
  border: ${({ colorMode }) => borderStyles(colorMode)};
  border-radius: ${theme.stylesToDelete.borderRadius};
`;
