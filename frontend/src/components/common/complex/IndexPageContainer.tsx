import React from 'react';
import styled from 'styled-components/macro';

type Props = {
  children: React.ReactNode;
};

export default function IndexPageContainer(props: Props) {
  const { children } = props;

  return (
    <Component>
      {children}
    </Component>
  );
}

const Component = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 40px;
`;
