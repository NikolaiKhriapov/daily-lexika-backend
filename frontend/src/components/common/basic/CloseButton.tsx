import { AiOutlineCloseCircle } from 'react-icons/ai';
import styled from 'styled-components/macro';

type Props = {
  onClick: () => void;
};

export default function CloseButton(props: Props) {
  const { onClick } = props;

  return (
    <Component onClick={onClick} />
  );
}

const Component = styled(AiOutlineCloseCircle)`
  font-size: 16px;
  cursor: pointer;
`;
