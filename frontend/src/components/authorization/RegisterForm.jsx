import {Formik, Form, useField} from 'formik';
import * as Yup from 'yup';
import {Alert, AlertIcon, Box, Button, FormLabel, Input, Stack} from "@chakra-ui/react";
import {register} from "../../services/authorization.js";
import {successNotification, errorNotification} from "../../services/popup-notification.js";

const MyTextInput = ({label, ...props}) => {
    const [field, meta] = useField(props);
    return (
        <Box>
            <FormLabel htmlFor={props.id || props.name}>{label}</FormLabel>
            <Input className="text-input" {...field} {...props}/>
            {meta.touched && meta.error ? (
                <Alert className="error" status={"error"} mt={2}>
                    <AlertIcon/>
                    {meta.error}
                </Alert>
            ) : null}
        </Box>
    );
};

const RegisterForm = ({onSuccess}) => {
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
                onSubmit={(user, {setSubmitting}) => {
                    setSubmitting(true);
                    register(user)
                        .then((response) => {
                            successNotification(
                                "User registered",
                                `${user.name} was successfully registered`
                            )
                            onSuccess(response.data.data.authenticationResponse.token);
                        }).catch((error) => {
                        console.log(error)
                        errorNotification(
                            error.code,
                            error.response.data.message
                        )

                    }).finally(() => {
                        setSubmitting(false);
                    })
                }}
            >
                {({isValid, isSubmitting, dirty}) => (
                    <Form>
                        <Stack spacing={"24px"}>
                            <MyTextInput
                                label="Name"
                                name="name"
                                type="text"
                                placeholder="Jane"
                            />
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
                            <Button isDisabled={!(isValid && dirty) || isSubmitting} type="submit">Submit</Button>
                        </Stack>
                    </Form>
                )}
            </Formik>
        </>
    );
};

export default RegisterForm;