import { Form, Formik } from 'formik';
import { Box } from '@chakra-ui/react';
import styled from 'styled-components/macro';
import React from 'react';
import { ButtonType } from '../../../utils/constants';
import Button from '../basic/Button';

type Props = {
  validateOnMount: boolean;
  initialValues: any;
  validationSchema: any;
  onSubmit: any;
  inputElements: React.ReactNode;
  buttonText: string;
};

export default function InputFieldWithButton(props: Props) {
  const { validateOnMount, initialValues, validationSchema, onSubmit, inputElements, buttonText } = props;

  return (
    <FormikStyled
      validateOnMount={validateOnMount}
      initialValues={initialValues}
      validationSchema={validationSchema}
      onSubmit={onSubmit}
    >
      {({ isValid, isSubmitting, dirty }) => (
        <FormStyled>
          <InputElementsContainer>
            {inputElements}
          </InputElementsContainer>
          <Button
            buttonText={buttonText}
            buttonType={ButtonType.SUBMIT}
            isDisabled={!(isValid && dirty) || isSubmitting}
          />
        </FormStyled>
      )}
    </FormikStyled>
  );
}

const FormikStyled = styled(Formik)`
  display: flex;
  flex-direction: column;
`;

const FormStyled = styled(Form)`
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  align-items: flex-end;
`;

const InputElementsContainer = styled(Box)`
  flex: 1;
  margin-right: 10px;
`;
