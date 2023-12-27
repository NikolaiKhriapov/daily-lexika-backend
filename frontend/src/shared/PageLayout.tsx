import { ColorMode, useColorMode } from '@chakra-ui/react';
import Content from 'src/components/shared/Content';
import styled from 'styled-components/macro';
import React from 'react';
import Sidebar from '../components/shared/Sidebar';
import Navbar from '../components/shared/Navbar';
import { theme } from '../utils/theme';

type Props = {
  children: React.ReactNode;
};

export default function PageLayout(props: Props) {
  const { children } = props;

  const { colorMode } = useColorMode();

  return (
    <Container colorMode={colorMode}>
      <Navbar />
      <Content>{children}</Content>
      <Sidebar />
    </Container>
  );
}

const Container = styled.div<{ colorMode: ColorMode }>`
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  width: 100%;
  background-color: ${({ colorMode }) => theme.colors[colorMode].background};
`;
