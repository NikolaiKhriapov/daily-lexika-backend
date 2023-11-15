import React, { useEffect } from 'react';
import {
  Button, Flex, FormLabel, Heading, Input, Stack, Box, Alert, AlertIcon, Link, useColorModeValue,
} from '@chakra-ui/react';
import { Field, FieldProps, Form, Formik } from 'formik';
import * as Yup from 'yup';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { errorNotification } from '../../services/popup-notification';

function MyTextInput({ ...props }) {
  return (
    <Box>
      <FormLabel htmlFor={props.name}>{props.label}</FormLabel>
      <Field name={props.name}>
        {({ field, meta }: FieldProps) => (
          <div>
            <Input
              className='text-input'
              type={props.type}
              name={props.name}
              placeholder={props.placeholder}
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

function LoginForm() {
  const { login } = useAuth();
  const navigate = useNavigate();

  return (
    <Formik
      validateOnMount
      initialValues={{
        email: '',
        password: '',
      }}
      validationSchema={
        Yup.object({
          email: Yup.string()
            .email('Invalid email address')
            .required('Required'),
          password: Yup.string()
            .max(8, 'Must be at least 8 characters')
            .max(20, 'Must be 20 characters or less')
            .required('Required'),
        })
      }
      onSubmit={(values, { setSubmitting }) => {
        setSubmitting(true);
        login(values)
          .then(() => navigate('/reviews'))
          .catch((error: any) => errorNotification(error.code, error.response.data.message))
          .finally(() => setSubmitting(false));
      }}
    >
      {({ isValid, isSubmitting, dirty }) => (
        <Form>
          <Stack spacing='24px'>
            <MyTextInput
              label='Email'
              name='email'
              type='email'
              placeholder='Email address'
            />

            <MyTextInput
              label='Password'
              name='password'
              type='password'
              placeholder='Password'
            />

            <Button isDisabled={!(isValid && dirty) || isSubmitting} type='submit'>Login</Button>
          </Stack>
        </Form>
      )}
    </Formik>
  );
}

function Login() {
  const { user } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    if (user) {
      navigate('/reviews');
    }
  });

  return (
    <Flex
      minH='100vh'
      align='center'
      justify='center'
      bg={useColorModeValue('rgba(237,238,240)', 'rgba(20,20,20)')}
    >
      <Stack spacing={8} mx='auto' w='430px' py={12} px={6}>
        <Stack align='center'>
          <Heading fontSize='4xl'>Sign in to your account</Heading>
        </Stack>
        <Box
          rounded='2xl'
          boxShadow='2xl'
          p={8}
          bg={useColorModeValue('white', 'rgba(40,40,40)')}
        >
          <LoginForm />
        </Box>
        <Link href='/register' color='blue.500' textAlign='center'>
          Don&apos;t have an account? Register now
        </Link>
      </Stack>
    </Flex>
  );
}

export default Login;
