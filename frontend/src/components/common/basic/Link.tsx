import { Link as ChakraLink } from '@chakra-ui/react';

interface LinkProps {
  href: string;
  text: string;
}

function Link(props: LinkProps) {
  const { href, text } = props;

  return (
    <ChakraLink
      href={href}
      color='blue.500'
    >
      {text}
    </ChakraLink>
  );
}

export default Link;
