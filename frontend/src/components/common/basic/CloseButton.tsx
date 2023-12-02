import { AiOutlineCloseCircle } from 'react-icons/ai';

interface CloseButtonProps {
  onClick: any;
}

function CloseButton(props: CloseButtonProps) {
  const { onClick } = props;

  return (
    <AiOutlineCloseCircle
      className='closeButton'
      onClick={onClick}
    />
  );
}

export default CloseButton;
