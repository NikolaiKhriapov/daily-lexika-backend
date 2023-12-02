import { useNavigate } from 'react-router-dom';
import * as Yup from 'yup';
import { Box, useColorMode } from '@chakra-ui/react';
import { useAuth } from '../context/AuthContext';
import { register } from '../../services/authorization';
import { errorNotification, successNotification } from '../../services/popup-notification';
import TextInput from '../common/complex/TextInput';
import { AuthFormType, Pages } from '../../utils/constants';
import { AuthenticationRequest, RegistrationRequest } from '../../types/types';
import InputFieldsWithButton from '../common/complex/InputFieldsWithButton';

interface AuthFormProps {
  formType: AuthFormType;
  onSuccess: any;
}

function AuthForm(props: AuthFormProps) {
  const { formType, onSuccess } = props;

  const { colorMode } = useColorMode();
  const { login } = useAuth();
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
          .then(() => navigate(Pages.REVIEWS))
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
    <Box className={`authFormContainer ${colorMode}`} boxShadow='2xl'>
      <InputFieldsWithButton
        validateOnMount
        initialValues={authFormData.initialValues}
        validationSchema={authFormData.validationSchema}
        onSubmit={authFormData.handleOnSubmit}
        inputElements={(
          <>
            {formType === AuthFormType.REGISTER && (
              <TextInput label='Name' name='name' type='text' placeholder='Name' />
            )}
            <TextInput label='Email' name='email' type='email' placeholder='Email address' />
            <TextInput label='Password' name='password' type='password' placeholder='Password' />
          </>
        )}
        buttonText='Submit'
      />
    </Box>
  );
}

export default AuthForm;
