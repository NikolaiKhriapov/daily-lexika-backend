import { extendTheme } from '@chakra-ui/react';
import { Breakpoint } from './constants';

const customColors = {
  light: {
    background: 'rgb(237,238,240)',
    borderColor: '#CBD5E0',
    bgColor: '#F7FAFC',
    hoverBgColor: '#E3E8EF',
    buttonColor: '#000000',
    buttonBgColor: '#CBD5E0',
    buttonHoverBgColor: '#A0AEC0',
    buttonRemoveHoverBgColor: '#F56565',
    reviewWordCardBgColor: '#E3E8EF',
    reviewWordCardBadgeRedColor: '#EF4444',
    progressBarBgColor: '#E4E8EE',
    progressCircularColor: '#8088A0',
    alert: '#F56565',
  },
  dark: {
    background: 'rgb(20,20,20)',
    borderColor: '#505050',
    bgColor: '#282828',
    hoverBgColor: '#3C3C3C',
    buttonColor: '#FFFFFF',
    buttonBgColor: '#3C3C3C',
    buttonHoverBgColor: '#505050',
    buttonRemoveHoverBgColor: '#EF4444',
    reviewWordCardBgColor: '#1E1E1E',
    reviewWordCardBadgeRedColor: '#800000',
    progressBarBgColor: '#505050',
    progressCircularColor: '#E4E8EE',
    alert: '#EF4444',
  },
};

const customBreakpoints = {
  base: Breakpoint.BASE,
  sm: Breakpoint.PHONE,
  md: Breakpoint.TABLET,
  lg: Breakpoint.LG,
  xl: Breakpoint.DESKTOP,
  '2xl': Breakpoint.XXL,
};

// TODO::: remove
const stylesToDelete = {
  borderWidth: '1px',
  borderStyle: 'solid',
  borderRadius: '10px',
  boxShadow: '0 6px 24px rgba(0, 0, 0, 0.1)',
  link: 'blue.500',
};

const theme = extendTheme({
  colors: customColors,
  fonts: {},
  fontSizes: {},
  breakpoints: customBreakpoints,
  stylesToDelete,
});

export { theme };
