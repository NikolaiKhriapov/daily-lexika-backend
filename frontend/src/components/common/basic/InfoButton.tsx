import { AiOutlineQuestionCircle } from 'react-icons/ai';

interface InfoButtonProps {
  onOpen: any;
}

function InfoButton(props: InfoButtonProps) {
  const { onOpen } = props;

  return (
    <AiOutlineQuestionCircle
      className='infoButton'
      onClick={onOpen}
    />
  );
}

export default InfoButton;
