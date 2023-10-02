import {
    Button, Flex, FormLabel, Heading, Input, Stack, Image, Box, Alert, AlertIcon, Link,
} from '@chakra-ui/react';
import {Form, Formik, useField} from "formik";
import * as Yup from 'yup';
import {useAuth} from "../context/AuthContext.jsx";
import {errorNotification} from "../../services/popup-notification.js";
import {useNavigate} from "react-router-dom";
import {useEffect} from "react";

const MyTextInput = ({label, ...props}) => {
    const [field, meta] = useField(props);
    return (
        <Box>
            <FormLabel htmlFor={props.id || props.name}>{label}</FormLabel>
            <Input className="text-input" {...field} {...props} />
            {meta.touched && meta.error ? (
                <Alert className="error" status={"error"} mt={2}>
                    <AlertIcon/>
                    {meta.error}
                </Alert>
            ) : null}
        </Box>
    );
};

const LoginForm = () => {

    const {login} = useAuth();
    const navigate = useNavigate();

    return (
        <Formik
            validateOnMount={true}
            validationSchema={
                Yup.object({
                    email: Yup.string()
                        .email('Invalid email address')
                        .required('Required'),
                    password: Yup.string()
                        .max(8, 'Must be at least 8 characters')
                        .max(20, 'Must be 20 characters or less')
                        .required('Required')
                })}
            initialValues={{email: '', password: ''}}
            onSubmit={(values, {setSubmitting}) => {
                setSubmitting(true);
                login(values)
                    .then(() => navigate("/reviews"))
                    .catch(error => errorNotification(error.code, error.response.data.message))
                    .finally(() => setSubmitting(false))
            }}
        >
            {({isValid, isSubmitting, dirty}) => (
                <Form>
                    <Stack spacing={"24px"}>
                        <MyTextInput
                            label="Email"
                            name="email"
                            type="email"
                            placeholder="jane@formik.com"
                        />

                        <MyTextInput
                            label="Password"
                            name="password"
                            type="password"
                            placeholder="password"
                        />

                        <Button isDisabled={!(isValid && dirty) || isSubmitting} type="submit">Login</Button>
                    </Stack>
                </Form>
            )}
        </Formik>
    )
}

const Login = () => {
    const {user: user} = useAuth();
    const navigate = useNavigate();

    useEffect(() => {
        if (user) {
            navigate("/reviews");
        }
    })

    return (
        <Stack minH={'100vh'} direction={{base: 'column', md: 'row'}}>
            <Flex p={8} flex={1} align={'center'} justify={'center'}>
                <Stack spacing={4} w={'full'} maxW={'md'}>
                    <Heading fontSize={'2xl'}>Log in to your account</Heading>
                    <LoginForm/>
                    <Link color={"blue.500"} href={"/register"}>
                        Don't have an account? Register now
                    </Link>
                </Stack>
            </Flex>
            <Flex flex={1}>
                <Image
                    alt={'Login Image'}
                    objectFit={'cover'}
                    src={
                        'https://images.unsplash.com/photo-1486312338219-ce68d2c6f44d?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1352&q=80'
                    }
                />
            </Flex>
        </Stack>
    );
}

export default Login;