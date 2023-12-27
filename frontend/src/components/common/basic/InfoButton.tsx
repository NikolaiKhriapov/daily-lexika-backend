import { AiOutlineQuestionCircle } from 'react-icons/ai';
import styled from 'styled-components/macro';

type Props = {
  onClick: () => void;
};

export default function InfoButton(props: Props) {
  const { onClick } = props;

  return (
    <Component onClick={onClick} />
  );
}

const Component = styled(AiOutlineQuestionCircle)`
  font-size: 16px;
  cursor: pointer;
`;
