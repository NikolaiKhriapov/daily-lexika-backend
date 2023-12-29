import { Badge } from '@chakra-ui/react';
import styled from 'styled-components/macro';
import Text from './Text';
import { Size } from '../../../utils/constants';

type Props = {
  colorScheme: string;
  text: string;
  isInTopRight?: boolean;
};

export default function StatusBadge(props: Props) {
  const { colorScheme, text, isInTopRight } = props;

  return (
    <Component>
      <StyledBadge
        $isInTopRight={isInTopRight || false}
        colorScheme={colorScheme}
      >
        <Text size={{ base: Size.XXS, md: Size.SM, xl: Size.SM }}>{text}</Text>
      </StyledBadge>
    </Component>
  );
}

StatusBadge.defaultProps = {
  isInTopRight: false,
};

const Component = styled.div`
  flex-direction: column;
`;

const StyledBadge = styled(Badge)<{ $isInTopRight: boolean }>`
  position: ${({ $isInTopRight }) => $isInTopRight && 'absolute'};
  top: ${({ $isInTopRight }) => $isInTopRight && '10px'};
  right: ${({ $isInTopRight }) => $isInTopRight && '10px'};
`;
