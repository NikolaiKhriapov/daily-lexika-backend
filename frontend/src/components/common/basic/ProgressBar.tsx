import { ColorMode, Progress, ProgressProps, useColorMode } from '@chakra-ui/react';
import styled from 'styled-components/macro';
import { theme } from '../../../utils/theme';
import { Size } from '../../../utils/constants';

export default function ProgressBar({ ...rest }: ProgressProps) {
  const { colorMode } = useColorMode();

  return (
    <ProgressStyled
      $colorMode={colorMode}
      colorScheme='gray'
      size={Size.SM}
      rounded={Size.MD}
      {...rest}
    />
  );
}

const ProgressStyled = styled(Progress)<{ $colorMode: ColorMode }>`
  background-color: ${({ $colorMode }) => theme.colors[$colorMode].progressBarBgColor};
`;
