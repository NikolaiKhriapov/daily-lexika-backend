import { CircularProgress, CircularProgressLabel, Flex, useColorMode } from '@chakra-ui/react';
import { TextSize } from '../../../utils/constants';
import Text from './Text';

interface ProgressCircularProps {
  value: any;
  text: string;
}

function ProgressCircular(props: ProgressCircularProps) {
  const { value, text } = props;

  const { colorMode } = useColorMode();

  return (
    <CircularProgress
      value={value}
      color={colorMode === 'light' ? '#8088A0' : '#E4E8EE'}
      trackColor={colorMode === 'light' ? '#E4E8EE' : '#505050'}
      size='175px'
      thickness='6px'
    >
      <CircularProgressLabel>{value}%
        <Flex className='text_container'>
          <Text size={TextSize.SMALL} text={text} isBold />
        </Flex>
      </CircularProgressLabel>
    </CircularProgress>
  );
}

export default ProgressCircular;
