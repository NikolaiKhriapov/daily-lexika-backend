import { Flex, Icon, Link, useColorMode } from '@chakra-ui/react';
import { FiMenu } from 'react-icons/fi';
import { Pages } from '../../utils/constants';

export default function Sidebar() {
  const { colorMode } = useColorMode();

  const LinkItems = [
    { name: 'Daily Reviews', route: Pages.REVIEWS, icon: FiMenu },
    { name: 'Word Packs', route: Pages.WORD_PACKS, icon: FiMenu },
    { name: 'Statistics', route: Pages.STATISTICS, icon: FiMenu },
  ];

  return (
    <Flex className={`Sidebar_container ${colorMode}`}>
      <Flex className={`corner ${colorMode}`} />
      <Flex className={`space ${colorMode}`} />
      {LinkItems.map((link) => (
        <Link key={link.name} href={link.route} className='item' style={{ textDecoration: 'none' }}>
          <Flex className={`container ${colorMode}`}>
            {link.icon && (<Icon className='icon' as={link.icon} />)}{link.name}
          </Flex>
        </Link>
      ))}
    </Flex>
  );
}
