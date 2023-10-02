import {
    Alert, AlertIcon, Box, Button, FormLabel, Input, Modal, ModalBody, ModalCloseButton, ModalContent, ModalFooter,
    ModalOverlay, Stack
} from "@chakra-ui/react";
import {CopyIcon} from "@chakra-ui/icons";
import {Form, Formik, useField} from "formik";
import * as Yup from "yup";
import {createReview} from "../../services/review.js";
import {errorNotification, successNotification} from "../../services/popup-notification.js";

const CreateReviewWindow = ({button, isOpen, onClose, wordPackDTO, fetchAllWordPacksDTO, fetchAllReviewsDTO}) => {

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

    return <>
        {button}
        <Modal isOpen={isOpen} onClose={onClose} size={"md"} isCentered>
            <ModalOverlay/>
            <ModalContent rounded={'lg'}>
                <ModalCloseButton/>
                <ModalBody>
                    <div style={{margin: '15px 0', fontSize: '20px', fontWeight: 'bold'}}>{wordPackDTO.name}</div>
                    <div style={{margin: '10px 0'}}><CopyIcon/>{wordPackDTO.totalWords}</div>
                    <div style={{margin: '10px 0'}}>{wordPackDTO.description}</div>
                    <br/>
                    <>
                        <Formik
                            initialValues={{
                                maxNewWordsPerDay: 5,
                                maxReviewWordsPerDay: 20,
                                wordPackName: `${wordPackDTO.name}`
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
                            onSubmit={(reviewDTO, {setSubmitting}) => {
                                setSubmitting(true);
                                createReview(reviewDTO)
                                    .then(() => {
                                        successNotification("Review saved", `${wordPackDTO.name} was successfully saved`)
                                        fetchAllReviewsDTO()
                                        fetchAllWordPacksDTO()
                                    })
                                    .catch((error) => errorNotification(error.code, error.response.data.message))
                                    .finally(() => setSubmitting(false))
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
                </ModalBody>
                <ModalFooter/>
            </ModalContent>
        </Modal>
    </>
}

export default CreateReviewWindow;