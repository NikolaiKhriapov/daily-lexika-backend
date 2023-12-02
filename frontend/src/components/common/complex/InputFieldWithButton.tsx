import { Form, Formik } from 'formik';
import { Box, Flex } from '@chakra-ui/react';
import Button from '../basic/Button';
import { ButtonSize, ButtonType } from '../../../utils/constants';

interface InputFieldWithButtonProps {
  validateOnMount: boolean;
  initialValues: any;
  validationSchema: any;
  onSubmit: any;
  inputElements: any;
}

function InputFieldWithButton(props: InputFieldWithButtonProps) {
  const { validateOnMount, initialValues, validationSchema, onSubmit, inputElements } = props;

  return (
    <Formik
      validateOnMount={validateOnMount}
      initialValues={initialValues}
      validationSchema={validationSchema}
      onSubmit={onSubmit}
    >
      {({ isValid, isSubmitting, dirty }) => (
        <Flex className='InputFieldWithButton'>
          <Form>
            <Flex className='InputFieldWithButton_container'>
              <Box className='InputFieldWithButton__inputElements'>{inputElements}</Box>
              <Button
                content='Change'
                size={ButtonSize.MEDIUM}
                type={ButtonType.SUBMIT}
                isDisabled={!(isValid && dirty) || isSubmitting}
              />
            </Flex>
          </Form>
        </Flex>
      )}
    </Formik>
  );
}

export default InputFieldWithButton;
