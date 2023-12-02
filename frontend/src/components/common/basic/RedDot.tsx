import { Flex } from '@chakra-ui/react';

interface RedDotProps {
  top: string;
  right: string;
}

function RedDot(props: RedDotProps) {
  const { top, right } = props;

  return (
    <Flex className='redDot' top={top} right={right} />
  );
}

export default RedDot;
