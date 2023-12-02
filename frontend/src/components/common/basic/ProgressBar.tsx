import { Progress, useColorMode } from '@chakra-ui/react';

interface ProgressBarProps {
  value: number;
  margin?: any;
}

function ProgressBar(props: ProgressBarProps) {
  const { value, margin } = props;

  const { colorMode } = useColorMode();

  return (
    <Progress
      className={`progressBar ${colorMode}`}
      value={value}
      colorScheme='gray'
      size='sm'
      rounded='md'
      margin={margin}
    />
  );
}

ProgressBar.defaultProps = {
  margin: '0',
};

export default ProgressBar;
