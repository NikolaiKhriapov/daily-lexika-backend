import { useNavigate } from 'react-router-dom';
import * as Yup from 'yup';
import { Box, ColorMode, useColorMode } from '@chakra-ui/react';
import styled from 'styled-components/macro';
import { useAuth } from '../context/AuthContext';
import { register } from '../../services/authorization';
import { errorNotification, successNotification } from '../../services/popup-notification';
import TextInput from '../common/complex/TextInput';
import { AuthFormType, Breakpoint, Page } from '../../utils/constants';
import { AuthenticationRequest, RegistrationRequest } from '../../utils/types';
import InputFieldsWithButton from '../common/complex/InputFieldsWithButton';
import { borderStyles, mediaBreakpointUp } from '../../utils/functions';
import { theme } from '../../utils/theme';

type Props = {
  formType: AuthFormType;
  onSuccess: any;
};

export default function AuthForm(props: Props) {
  const { formType, onSuccess } = props;

  const { login } = useAuth();
  const { colorMode } = useColorMode();
  const navigate = useNavigate();

  const authFormMapping = {
    [AuthFormType.LOGIN]: {
      initialValues: { email: '', password: '' },
      buttonText: 'Login',
      validationSchema: Yup.object({
        email: Yup.string()
          .email('Invalid email address')
          .required('Required'),
        password: Yup.string()
          .min(8, 'Must be at least 8 characters')
          .max(20, 'Must be 20 characters or less')
          .required('Required'),
      }),
      handleOnSubmit: (values: RegistrationRequest | AuthenticationRequest, { setSubmitting }: any) => {
        setSubmitting(true);
        login(values)
          .then(() => navigate(Page.REVIEWS))
          .catch((error: any) => errorNotification(error.code, error.response.data.message))
          .finally(() => setSubmitting(false));
      },
    },
    [AuthFormType.REGISTER]: {
      initialValues: { name: '', email: '', password: '' },
      buttonText: 'Register',
      validationSchema: Yup.object({
        name: Yup.string()
          .max(15, 'Must be 15 characters or less')
          .required('Required'),
        email: Yup.string()
          .email('Invalid email address')
          .required('Required'),
        password: Yup.string()
          .min(8, 'Must be at least 8 characters')
          .max(20, 'Must be 20 characters or less')
          .required('Required'),
      }),
      handleOnSubmit: (values: any, { setSubmitting }: any) => {
        setSubmitting(true);
        register(values)
          .then((response: any) => {
            successNotification('User registered', `${values.name} was successfully registered`);
            onSuccess(response.data.data.authenticationResponse.token);
          })
          .catch((error: any) => errorNotification(error.code, error.response.data.message))
          .finally(() => {
            setSubmitting(false);
          });
      },
    },
  };

  const authFormData = authFormMapping[formType];

  return (
    <BoxStyled boxShadow='2xl' $colorMode={colorMode}>
      <InputFieldsWithButton
        validateOnMount
        initialValues={authFormData.initialValues}
        validationSchema={authFormData.validationSchema}
        onSubmit={authFormData.handleOnSubmit}
        inputElements={(
          <InputElements>
            {formType === AuthFormType.REGISTER && (
              <TextInput label='Name' name='name' type='text' placeholder='Name' />
            )}
            <TextInput label='Email' name='email' type='email' placeholder='Email address' />
            <TextInput label='Password' name='password' type='password' placeholder='Password' />
          </InputElements>
        )}
        buttonText='Submit'
      />
    </BoxStyled>
  );
}

const BoxStyled = styled(Box)<{ $colorMode: ColorMode }>`
  width: 250px;
  max-width: 80%;
  padding: 20px;
  background-color: ${({ $colorMode }) => theme.colors[$colorMode].bgColor};
  border: ${({ $colorMode }) => borderStyles($colorMode)};
  border-radius: ${theme.stylesToDelete.borderRadius};

  ${mediaBreakpointUp(Breakpoint.TABLET)} {
    width: 310px;
    padding: 30px;
  }

  ${mediaBreakpointUp(Breakpoint.DESKTOP)} {
    width: 370px;
    padding: 30px;
  }
`;

const InputElements = styled.div`
  display: flex;
  flex-direction: column;
  gap: 15px;

  ${mediaBreakpointUp(Breakpoint.TABLET)} {
    gap: 20px;
  }

  ${mediaBreakpointUp(Breakpoint.DESKTOP)} {
    gap: 25px;
  }
`;
