import { Button as ChakraButton, useColorMode } from '@chakra-ui/react';
import { ButtonSize, ButtonType } from '../../../utils/constants';

interface ButtonProps {
  content: any;
  type: ButtonType;
  onClick?: any;
  isDisabled?: boolean;
  size?: ButtonSize;
  ref?: any;
}

function Button(props: ButtonProps) {
  const { type, onClick, content, isDisabled, size, ref } = props;

  const { colorMode } = useColorMode();

  const getButtonType = () => {
    switch (type) {
      case ButtonType.SUBMIT:
        return 'submit';
      case ButtonType.RESET:
        return 'reset';
      default: return 'button';
    }
  };

  return (
    <ChakraButton
      className={`button ${type} ${colorMode}`}
      size={size}
      onClick={onClick}
      isDisabled={isDisabled}
      ref={ref}
      type={getButtonType()}
    >
      {content}
    </ChakraButton>
  );
}

Button.defaultProps = {
  onClick: null,
  isDisabled: false,
  size: ButtonSize.SMALL,
  ref: null,
};

export default Button;
