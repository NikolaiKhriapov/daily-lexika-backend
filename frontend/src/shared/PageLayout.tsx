import { Flex, useColorMode } from '@chakra-ui/react';
import Content from 'src/components/shared/Content';
import Sidebar from '../components/shared/Sidebar';
import Navbar from '../components/shared/Navbar';

function PageLayout({ children }: { children: any }) {
  const { colorMode } = useColorMode();

  return (
    <Flex className={`pageLayout ${colorMode}`}>
      <Navbar />
      <Sidebar />
      <Content body={children} />
    </Flex>
  );
}

export default PageLayout;
