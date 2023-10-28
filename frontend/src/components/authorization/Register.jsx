import React, { useEffect } from 'react';
import {
    Alert, AlertIcon, Box, Button, Flex, FormLabel, Heading, Input, Link, Stack, useColorModeValue,
} from '@chakra-ui/react';
import { useAuth } from '../context/AuthContext.jsx';
import { useNavigate } from 'react-router-dom';
import { Form, Formik, useField } from 'formik';
import * as Yup from 'yup';
import { register } from '../../services/authorization.js';
import { errorNotification, successNotification } from '../../services/popup-notification.js';

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

const RegisterForm = ({ onSuccess }) => {
    return (
        <>
            <Formik
                initialValues={{
                    name: '',
                    email: '',
                    password: '',
                }}
                validationSchema={Yup.object({
                    name: Yup.string()
                        .max(15, 'Must be 15 characters or less')
                        .required('Required'),
                    email: Yup.string()
                        .email('Invalid email address')
                        .required('Required'),
                    password: Yup.string()
                        .max(8, 'Must be at least 8 characters')
                        .max(20, 'Must be 20 characters or less')
                        .required('Required'),
                })}
                onSubmit={(user, { setSubmitting }) => {
                    setSubmitting(true);
                    register(user)
                        .then((response) => {
                            successNotification(
                                'User registered',
                                `${user.name} was successfully registered`,
                            );
                            onSuccess(response.data.data.authenticationResponse.token);
                        }).catch((error) => {
                        console.log(error);
                        errorNotification(
                            error.code,
                            error.response.data.message,
                        );

                    }).finally(() => {
                        setSubmitting(false);
                    });
                }}
            >
                {({ isValid, isSubmitting, dirty }) => (
                    <Form>
                        <Stack spacing={'24px'}>
                            <MyTextInput
                                label='Name'
                                name='name'
                                type='text'
                                placeholder='Name'
                            />
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
                            <Button isDisabled={!(isValid && dirty) || isSubmitting} type='submit'>Register</Button>
                        </Stack>
                    </Form>
                )}
            </Formik>
        </>
    );
};

const Register = () => {
    const { user, setUserFromToken } = useAuth();
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
                    <Heading fontSize={'4xl'}>Register account</Heading>
                </Stack>
                <Box rounded={'2xl'} boxShadow={'2xl'} p={8}
                     bg={useColorModeValue('white', 'rgba(40,40,40)')}>
                    <RegisterForm
                        onSuccess={(token) => {
                            localStorage.setItem('access_token', token);
                            setUserFromToken();
                            navigate('/reviews');
                        }}
                    />
                </Box>
                <Link href={'/login'} color={'blue.500'} align={'center'}>
                    Already have an account? Log in now
                </Link>
            </Stack>
        </Flex>
    );

};

export default Register;