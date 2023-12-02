import { Form, Formik } from 'formik';
import { Stack } from '@chakra-ui/react';
import { ButtonSize, ButtonType } from '../../../utils/constants';
import Button from '../basic/Button';

interface InputFieldsWithButtonProps {
  validateOnMount: boolean;
  initialValues: any;
  validationSchema: any;
  onSubmit: any;
  inputElements: any;
  buttonText: string;
}

function InputFieldsWithButton(props: InputFieldsWithButtonProps) {
  const { validateOnMount, initialValues, validationSchema, onSubmit, inputElements, buttonText } = props;

  return (
    <Formik
      validateOnMount={validateOnMount}
      initialValues={initialValues}
      validationSchema={validationSchema}
      onSubmit={onSubmit}
    >
      {({ isValid, isSubmitting }) => (
        <Form>
          <Stack spacing='24px'>
            {inputElements}
            <Button
              content={buttonText}
              size={ButtonSize.MEDIUM}
              type={ButtonType.SUBMIT}
              isDisabled={!(isValid) || isSubmitting}
            />
          </Stack>
        </Form>
      )}
    </Formik>
  );
}

export default InputFieldsWithButton;
