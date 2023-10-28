import React, { useEffect } from 'react';
import {
    Button, Flex, FormLabel, Heading, Input, Stack, Box, Alert, AlertIcon, Link, useColorModeValue,
} from '@chakra-ui/react';
import { Form, Formik, useField } from 'formik';
import * as Yup from 'yup';
import { useAuth } from '../context/AuthContext.jsx';
import { errorNotification } from '../../services/popup-notification.js';
import { useNavigate } from 'react-router-dom';

const MyTextInput = ({ label, ...props }) => {
    const [field, meta] = useField(props);
    return (
        <Box>
            <FormLabel htmlFor={props.id || props.name}>{label}</FormLabel>
            <Input className='text-input' {...field} {...props} />
            {meta.touched && meta.error ? (
                <Alert className='error' status={'error'} mt={2}>
                    <AlertIcon />
                    {meta.error}
                </Alert>
            ) : null}
        </Box>
    );
};

const LoginForm = () => {

    const { login } = useAuth();
    const navigate = useNavigate();

    return (
        <Formik
            validateOnMount={true}
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
                })}
            onSubmit={(values, { setSubmitting }) => {
                setSubmitting(true);
                login(values)
                    .then(() => navigate('/reviews'))
                    .catch(error => errorNotification(error.code, error.response.data.message))
                    .finally(() => setSubmitting(false));
            }}
        >
            {({ isValid, isSubmitting, dirty }) => (
                <Form>
                    <Stack spacing={'24px'}>
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
};

const Login = () => {
    const { user: user } = useAuth();
    const navigate = useNavigate();

    useEffect(() => {
        if (user) {
            navigate('/reviews');
        }
    });

    return (
        <Flex
            minH={'100vh'} align={'center'} justify={'center'}
            bg={useColorModeValue('rgba(237,238,240)', 'rgba(20,20,20)')}
        >
            <Stack spacing={8} mx={'auto'} w={'430px'} py={12} px={6}>
                <Stack align={'center'}>
                    <Heading fontSize={'4xl'}>Sign in to your account</Heading>
                </Stack>
                <Box rounded={'2xl'} boxShadow={'2xl'} p={8}
                     bg={useColorModeValue('white', 'rgba(40,40,40)')}>
                    <LoginForm />
                </Box>
                <Link href={'/register'} color={'blue.500'} align={'center'}>
                    Don't have an account? Register now
                </Link>
            </Stack>
        </Flex>
    );

};

export default Login;