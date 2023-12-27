import { ColorMode } from '@chakra-ui/react';
import { Breakpoint } from './constants';
import { theme } from './theme';

export const mediaBreakpointUp: (breakpoint: Breakpoint | string) => string = (breakpoint) =>
  `@media (min-width: ${breakpoint})`;

export const borderStyles: (colorMode: ColorMode) => string = (colorMode) =>
  `${theme.stylesToDelete.borderWidth} ${theme.stylesToDelete.borderStyle} ${theme.colors[colorMode].borderColor}`;

export const hiddenScrollbar = () => `
  scrollbar-width: none; /* Firefox */
  -ms-overflow-style: none; /* Internet Explorer */
  &::-webkit-scrollbar {
    display: none; /* WebKit */
  }
`;
