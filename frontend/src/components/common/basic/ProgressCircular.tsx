import {
  CircularProgress,
  CircularProgressLabel,
  CircularProgressProps,
  useBreakpointValue,
  useColorMode,
} from '@chakra-ui/react';
import { theme } from '../../../utils/theme';
import Text from './Text';
import { FontWeight, Size } from '../../../utils/constants';

interface Props extends CircularProgressProps {
  text: string;
}

export default function ProgressCircular({ text, value, ...rest }: Props) {
  const { colorMode } = useColorMode();

  return (
    <CircularProgress
      value={value}
      size={useBreakpointValue({ base: '105px', md: '175px', xl: '175px' })}
      thickness='6px'
      color={theme.colors[colorMode].progressCircularColor}
      trackColor={theme.colors[colorMode].progressBarBgColor}
      {...rest}
    >
      <CircularProgressLabel>{value}%
        <Text size={{ base: Size.XS, md: Size.MD, xl: Size.MD }} fontWeight={FontWeight.SEMIBOLD}>{text}</Text>
      </CircularProgressLabel>
    </CircularProgress>
  );
}
