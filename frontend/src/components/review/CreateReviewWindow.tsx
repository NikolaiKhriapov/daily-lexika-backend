import {
  Alert, AlertIcon, Box, Button, FormLabel, Input, Modal, ModalBody, ModalCloseButton, ModalContent, ModalFooter,
  ModalOverlay, Stack, useColorModeValue,
} from '@chakra-ui/react';
import { CopyIcon } from '@chakra-ui/icons';
import { Field, FieldProps, Form, Formik } from 'formik';
import * as Yup from 'yup';
import React from 'react';
import { createReview } from '../../services/reviews';
import { errorNotification, successNotification } from '../../services/popup-notification';
import { WordPackDTO } from '../../types/types';

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

function CreateReviewWindow({ button, isOpen, onClose, wordPackDTO, fetchAllWordPacksDTO, fetchAllReviewsDTO }: {
  button: any;
  isOpen: boolean;
  onClose: any;
  wordPackDTO: WordPackDTO;
  fetchAllWordPacksDTO: any;
  fetchAllReviewsDTO: any;
}) {
  const color = useColorModeValue('black', 'white');
  const bg = useColorModeValue('gray.300', 'rgba(60,60,60)');
  const hoverBg = useColorModeValue('gray.400', 'rgba(80,80,80)');

  return (
    <>
      {button}
      <Modal isOpen={isOpen} onClose={onClose} size='md' isCentered>
        <ModalOverlay />
        <ModalContent
          shadow='2xl'
          border='1px solid'
          rounded='lg'
          borderColor={useColorModeValue('gray.400', 'rgba(80,80,80)')}
          bg={useColorModeValue('gray.100', 'rgba(40,40,40)')}
        >
          <ModalCloseButton />
          <ModalBody>
            <div style={{ margin: '15px 0', fontSize: '20px', fontWeight: 'bold' }}>
              {wordPackDTO.name}
            </div>
            <div style={{ margin: '10px 0' }}><CopyIcon />{wordPackDTO.totalWords}</div>
            <div style={{ margin: '10px 0' }}>{wordPackDTO.description}</div>
            <br />
            <Formik
              initialValues={{
                maxNewWordsPerDay: 5,
                maxReviewWordsPerDay: 20,
                wordPackName: `${wordPackDTO.name}`,
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
              onSubmit={(reviewDTO, { setSubmitting }) => {
                setSubmitting(true);
                createReview(reviewDTO)
                  .then(() => {
                    successNotification('Review saved', `${wordPackDTO.name} was successfully saved`);
                    fetchAllReviewsDTO();
                    fetchAllWordPacksDTO();
                  })
                  .catch((error) => errorNotification(error.code, error.response.data.message))
                  .finally(() => setSubmitting(false));
              }}
            >
              {({ isValid, isSubmitting }) => (
                <Form>
                  <Stack spacing='24px'>
                    <MyTextInput
                      label='Max New Words Per Day'
                      name='maxNewWordsPerDay'
                      type='number'
                      placeholder='1'
                    />
                    <MyTextInput
                      label='Max Review Words Per Day'
                      name='maxReviewWordsPerDay'
                      type='number'
                      placeholder='1'
                    />
                    <Button
                      isDisabled={!(isValid) || isSubmitting}
                      type='submit'
                      shadow='2xl'
                      color={color}
                      bg={bg}
                      _hover={{ bg: hoverBg }}
                    >Submit
                    </Button>
                  </Stack>
                </Form>
              )}
            </Formik>
          </ModalBody>
          <ModalFooter />
        </ModalContent>
      </Modal>
    </>
  );
}

export default CreateReviewWindow;
