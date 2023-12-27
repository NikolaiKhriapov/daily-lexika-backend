import { Box, FormLabel } from '@chakra-ui/react';
import { Field, FieldProps, useFormikContext } from 'formik';
import Input from '../basic/Input';
import { theme } from '../../../utils/theme';

type Props = {
  label: string;
  name: string;
  type: string;
  placeholder: string;
};

export default function TextInput(props: Props) {
  const { label, name, type, placeholder } = props;

  const { errors, touched } = useFormikContext<any>();

  return (
    <Box>
      <FormLabel htmlFor={name}>{label}</FormLabel>
      <Field name={name}>
        {({ field }: FieldProps) => (
          <Input
            type={type}
            name={name}
            placeholder={placeholder}
            onChange={field.onChange}
            onBlur={field.onBlur}
            value={field.value}
            borderColor={errors[name] && touched[name] ? theme.colors.red['400'] : 'inherit'}
          />
        )}
      </Field>
    </Box>
  );
}
