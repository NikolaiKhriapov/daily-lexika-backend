import React from 'react';
import { LinkProps } from '@chakra-ui/layout/dist/link';
import { Link as ChakraLink } from '@chakra-ui/react';
import { theme } from '../../../utils/theme';

export default function Link({ children, ...rest }: LinkProps) {
  return (
    <ChakraLink
      color={theme.stylesToDelete.link}
      {...rest}
    >
      {children}
    </ChakraLink>
  );
}
