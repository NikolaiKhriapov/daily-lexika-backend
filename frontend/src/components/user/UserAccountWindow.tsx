import {
  Alert, AlertDialog, AlertDialogBody, AlertDialogContent, AlertDialogFooter, AlertDialogHeader, AlertDialogOverlay,
  AlertIcon, Avatar, Box, Button, Flex, FormLabel, Input, Modal, ModalBody, ModalCloseButton, ModalContent,
  ModalHeader, ModalOverlay, Stack, useDisclosure,
} from '@chakra-ui/react';
import { Field, FieldProps, Form, Formik } from 'formik';
import * as Yup from 'yup';
import React, { RefObject, useRef } from 'react';
import { errorNotification, successNotification } from '../../services/popup-notification';
import { deleteAccount, updateUserInfo } from '../../services/user';
import { UserDTO } from '../../types/types';

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

function UserAccountWindow({ button, isOpen, onClose, userDTO, updateUserAndName }: {
  button: any;
  isOpen: boolean;
  onClose: any;
  userDTO: UserDTO;
  updateUserAndName: any;

}) {
  const {
    isOpen: isOpenDeleteAccountButton,
    onOpen: onOpenDeleteAccountButton,
    onClose: onCloseDeleteAccountButton,
  } = useDisclosure();

  const cancelRef: RefObject<HTMLButtonElement> = useRef(null);

  const handleChangeInfo = (userUpdatedInfoDTO: UserDTO, setSubmitting: any) => {
    updateUserInfo(userUpdatedInfoDTO)
      .then(() => successNotification('User information updated successfully', ''))
      .catch((error) => errorNotification(error.code, error.response.data.message))
      .finally(() => setSubmitting(false));
  };

  const handleChangePassword = (userUpdatedInfoDTO: UserDTO, setSubmitting: any) => {
    updateUserInfo(userUpdatedInfoDTO)
      .then(() => successNotification('Password updated successfully', ''))
      .catch((error) => errorNotification(error.code, error.response.data.message))
      .finally(() => setSubmitting(false));
  };

  const deleteAccountButton = (
    <Button
      bg='red.400'
      color='white'
      _hover={{ transform: 'translateY(-2px)', boxShadow: 'lg' }}
      onClick={onOpenDeleteAccountButton}
    >
      Delete Account
    </Button>
  );

  return (
    <>
      {button}
      <Modal isOpen={isOpen} onClose={onClose} size='md' isCentered>
        <ModalOverlay />
        <ModalContent rounded='lg'>
          <ModalCloseButton />
          <ModalHeader>Account</ModalHeader>
          <ModalBody>
            <Flex justifyContent='center' alignItems='baseline'>
              <Avatar size='2xl' />
            </Flex>
            <br />
            <Formik
              validateOnMount
              initialValues={{ name: userDTO && userDTO.name }}
              validationSchema={
                Yup.object({
                  name: Yup.string()
                    .max(15, 'Must be 15 characters or less'),
                })
              }
              onSubmit={(userUpdatedInfoDTO, { setSubmitting }) => {
                setSubmitting(true);
                handleChangeInfo(userUpdatedInfoDTO, setSubmitting);
                const updatedUserDTO = { ...userDTO, name: userUpdatedInfoDTO.name };
                updateUserAndName(updatedUserDTO);
                onClose();
              }}
            >
              {({ isValid, isSubmitting, dirty }) => (
                <Form>
                  <Stack spacing='24px'>
                    <Flex alignItems='end' justifyContent='space-between'>
                      <Box flex='1' mr='3'>
                        <MyTextInput label='Name' name='name' type='text' />
                      </Box>
                      <Button
                        isDisabled={!(isValid && dirty) || isSubmitting}
                        type='submit'
                      >Change
                      </Button>
                    </Flex>
                  </Stack>
                </Form>
              )}
            </Formik>
            <br />
            <Formik
              validateOnMount={false}
              initialValues={{ email: userDTO && userDTO.email }}
              validationSchema={
                Yup.object({
                  email: Yup.string()
                    .email('Invalid email address'),
                })
              }
              onSubmit={(userUpdatedInfoDTO, { setSubmitting }) => {
                setSubmitting(true);
                handleChangeInfo(userUpdatedInfoDTO, setSubmitting);
                localStorage.removeItem('access_token');
              }}
            >
              {({ isValid, isSubmitting, dirty }) => (
                <Form>
                  <Stack spacing='24px'>
                    <Flex alignItems='end' justifyContent='space-between'>
                      <Box flex='1' mr='3'>
                        <MyTextInput label='Email' name='email' type='email' />
                      </Box>
                      <Button
                        isDisabled={!(isValid && dirty) || isSubmitting}
                        type='submit'
                      >Change
                      </Button>
                    </Flex>
                  </Stack>
                </Form>
              )}
            </Formik>
            <br />
            <Formik
              validateOnMount={false}
              initialValues={{
                password: '',
                repeatPassword: '',
              }}
              validationSchema={
                Yup.object({
                  password: Yup.string()
                    .required('Password is required')
                    .min(8, 'Must be at least 8 characters')
                    .max(20, 'Must be 20 characters or less'),
                  repeatPassword: Yup.string()
                    .oneOf([Yup.ref('password')], 'Passwords must match'),
                })
              }
              onSubmit={(userUpdatedInfoDTO, { setSubmitting }) => {
                setSubmitting(true);
                handleChangePassword(userUpdatedInfoDTO, setSubmitting);
              }}
            >
              {({ isValid, isSubmitting, dirty }) => (
                <Form>
                  <Stack spacing='24px'>
                    <Flex alignItems='end' justifyContent='space-between'>
                      <Box flex='1' mr='3'>
                        <MyTextInput
                          label='Password'
                          name='password'
                          type='password'
                          placeholder='New password'
                        />
                        <br />
                        <MyTextInput
                          label='Repeat Password'
                          name='repeatPassword'
                          type='password'
                          placeholder='Repeat new password'
                        />
                      </Box>
                      <Button isDisabled={!(isValid && dirty) || isSubmitting} type='submit'>
                        Change
                      </Button>
                    </Flex>
                  </Stack>
                </Form>
              )}
            </Formik>
            <br />
            <Flex justifyContent='center'>
              <Stack ml={5}>
                {deleteAccountButton}
                <AlertDialog
                  isOpen={isOpenDeleteAccountButton}
                  onClose={onCloseDeleteAccountButton}
                  leastDestructiveRef={cancelRef}
                >
                  <AlertDialogOverlay>
                    <AlertDialogContent>
                      <AlertDialogHeader fontSize='lg' fontWeight='bold'>
                        Delete Account
                      </AlertDialogHeader>
                      <AlertDialogBody>
                        Are you sure you want to delete account?
                        You can&apos;t undo this action.
                      </AlertDialogBody>
                      <AlertDialogFooter>
                        <Button ref={cancelRef} onClick={onCloseDeleteAccountButton}>
                          Cancel
                        </Button>
                        <Button
                          colorScheme='red'
                          onClick={() => {
                            deleteAccount()
                              .then(() => {
                                successNotification('Account deleted successfully', '');
                                localStorage.removeItem('access_token');
                                onClose();
                              })
                              .catch((error) => {
                                errorNotification(error.code, error.response.data.message);
                              });
                          }}
                          ml={3}
                        >
                          Delete Account
                        </Button>
                      </AlertDialogFooter>
                    </AlertDialogContent>
                  </AlertDialogOverlay>
                </AlertDialog>
              </Stack>
            </Flex>
            <br />
          </ModalBody>
        </ModalContent>
      </Modal>
    </>
  );
}

export default UserAccountWindow;
