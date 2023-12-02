import { Alert, AlertIcon, Box, FormLabel, Input } from '@chakra-ui/react';
import { Field, FieldProps } from 'formik';

interface TextInputProps {
  label: string;
  name: string;
  type: string;
  placeholder: string;
}

function TextInput(props: TextInputProps) {
  const { label, name, type, placeholder } = props;

  return (
    <Box>
      <FormLabel htmlFor={name}>{label}</FormLabel>
      <Field name={name}>
        {({ field, meta }: FieldProps) => (
          <div>
            <Input
              type={type}
              name={name}
              placeholder={placeholder}
              onChange={field.onChange}
              onBlur={field.onBlur}
              value={field.value}
            />
            {meta.touched && meta.error && (
              <Alert status='error' mt={2}>
                <AlertIcon />
                {meta.error}
              </Alert>
            )}
          </div>
        )}
      </Field>
    </Box>
  );
}

export default TextInput;
