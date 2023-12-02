import { Box, Flex, useColorMode } from '@chakra-ui/react';
import { TextSize } from '../../utils/constants';
import Heading from '../common/basic/Heading';
import Text from '../common/basic/Text';

interface StatsCardProps {
  icon: any;
  stat?: number;
  title: string;
}

function StatsCard(props: StatsCardProps) {
  const { icon, stat, title } = props;

  const { colorMode } = useColorMode();

  return (
    <Box>
      <Flex className={`StatsCard_container ${colorMode}`} shadow='2xl'>
        <Flex className={`icon ${colorMode}`}>{icon}</Flex>
        <Flex className='stats'>
          <Heading level={3} text={stat} />
          <Text size={TextSize.SMALL} text={title} isBold />
        </Flex>
      </Flex>
    </Box>
  );
}

StatsCard.defaultProps = {
  stat: '–––',
};

export default StatsCard;
