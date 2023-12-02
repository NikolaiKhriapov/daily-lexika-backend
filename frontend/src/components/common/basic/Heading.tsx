import { Flex } from '@chakra-ui/react';

interface HeadingProps {
  text: any;
  level: 1 | 2 | 3 | 4;
}

function Heading(props: HeadingProps) {
  const { text, level } = props;

  return (
    <Flex className={`heading${level}`}>{text}</Flex>
  );
}

export default Heading;
