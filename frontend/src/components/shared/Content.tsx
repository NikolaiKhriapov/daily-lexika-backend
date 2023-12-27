import React from 'react';
import styled from 'styled-components/macro';
import { mediaBreakpointUp } from '../../utils/functions';
import { Breakpoint } from '../../utils/constants';

type Props = {
  children: React.ReactNode;
};

export default function Content(props: Props) {
  const { children } = props;

  return (
    <Container>
      {children}
    </Container>
  );
}

const Container = styled.div`
  padding: 20px;
  display: flex;
  justify-content: center;
  margin-top: 50px;
  margin-bottom: 50px;

  ${mediaBreakpointUp(Breakpoint.TABLET)} {
    padding: 50px;
    margin-top: 70px;
    margin-bottom: 70px;
  }
  
  ${mediaBreakpointUp(Breakpoint.DESKTOP)} {
    margin-left: 210px;
  }
`;
