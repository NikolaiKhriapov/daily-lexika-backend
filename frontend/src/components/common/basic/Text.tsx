import { TextSize } from '../../../utils/constants';

interface TextProps {
  size: TextSize;
  text: any;
  isBold?: boolean;
}

function Text(props: TextProps) {
  const { text, size, isBold } = props;

  return (
    <span className={`text${size} ${isBold && 'isBold'}`}>{text}</span>
  );
}

Text.defaultProps = {
  isBold: false,
};

export default Text;
