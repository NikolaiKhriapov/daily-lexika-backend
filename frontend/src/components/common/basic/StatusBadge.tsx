import { Badge, Flex } from '@chakra-ui/react';

interface StatusBadgeProps {
  colorScheme: string;
  text: string;
  isInTopRight?: boolean;
}

function StatusBadge(props: StatusBadgeProps) {
  const { colorScheme, text, isInTopRight } = props;

  return (
    <Flex className='statusBadge'>
      <Badge className={`${isInTopRight && 'isInTopRight'}`} colorScheme={colorScheme}>{text}</Badge>
    </Flex>
  );
}

StatusBadge.defaultProps = {
  isInTopRight: false,
};

export default StatusBadge;
