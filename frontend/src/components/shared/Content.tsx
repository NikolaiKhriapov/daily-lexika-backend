import { Flex } from '@chakra-ui/react';

interface ContentProps {
  body: any;
}

function Content(props: ContentProps) {
  const { body } = props;

  return (
    <Flex className='Content_container'>{body}</Flex>
  );
}

export default Content;
