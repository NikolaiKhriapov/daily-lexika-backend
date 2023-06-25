import {Formik, Form, useField} from 'formik';
import * as Yup from 'yup';
import {Alert, AlertIcon, Box, Button, FormLabel, Input, Stack} from "@chakra-ui/react";
import {createReview} from "../../services/review.js";
import {successNotification, errorNotification} from "../../services/notification.js";

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

const CreateReviewForm = ({ name, fetchAllWordPacks }) => {
    return (
        <>
            <Formik
                initialValues={{
                    maxNewWordsPerDay: 5,
                    maxReviewWordsPerDay: 20,
                    wordPackName: `${name}`
                }}
                validationSchema={Yup.object({
                    maxNewWordsPerDay: Yup.number()
                        .min(1, 'Must be at least 1')
                        .max(20, 'Must be less than 20')
                        .required('Required'),
                    maxReviewWordsPerDay: Yup.number()
                        .min(1, 'Must be at least 1')
                        .max(50, 'Must be less than 50')
                        .required('Required'),
                })}
                onSubmit={(review, {setSubmitting}) => {
                    setSubmitting(true);
                    createReview(review)
                        .then((response) => {
                            console.log(response)
                            successNotification(
                                "Review saved",
                                `${name} was successfully saved`
                            )
                            fetchAllWordPacks();
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
                {({isValid, isSubmitting}) => (
                    <Form>
                        <Stack spacing={"24px"}>
                            <MyTextInput
                                label="Max New Words Per Day"
                                name="maxNewWordsPerDay"
                                type="number"
                                placeholder="1"
                            />
                            <MyTextInput
                                label="Max Review Words Per Day"
                                name="maxReviewWordsPerDay"
                                type="number"
                                placeholder="1"
                            />
                            <Button isDisabled={!(isValid) || isSubmitting} type="submit">Submit</Button>
                        </Stack>
                    </Form>
                )}
            </Formik>
        </>
    );
};

export default CreateReviewForm;