import { Flex } from '@chakra-ui/react';
import * as Yup from 'yup';
import { TbCards } from 'react-icons/tb';
import { createReview } from '../../services/reviews';
import { errorNotification, successNotification } from '../../services/popup-notification';
import { ReviewDTO, WordPackDTO } from '../../types/types';
import TextInput from '../common/complex/TextInput';
import InputFieldsWithButton from '../common/complex/InputFieldsWithButton';
import Modal from '../common/complex/Modal';
import Text from '../common/basic/Text';
import { TextSize } from '../../utils/constants';

interface CreateReviewWindowProps {
  button: any;
  isOpen: boolean;
  onClose: any;
  wordPackDTO: WordPackDTO;
  fetchAllWordPacksDTO: any;
  fetchAllReviewsDTO: any;
}

function CreateReviewWindow(props: CreateReviewWindowProps) {
  const { button, isOpen, onClose, wordPackDTO, fetchAllWordPacksDTO, fetchAllReviewsDTO } = props;

  const initialValues = {
    maxNewWordsPerDay: 5,
    maxReviewWordsPerDay: 20,
    wordPackName: `${wordPackDTO.name}`,
  };

  const validationSchema = Yup.object({
    maxNewWordsPerDay: Yup.number()
      .min(1, 'Must be at least 1')
      .max(20, 'Must be less than 20')
      .required('Required'),
    maxReviewWordsPerDay: Yup.number()
      .min(1, 'Must be at least 1')
      .max(50, 'Must be less than 50')
      .required('Required'),
  });

  const handleOnSubmit = (reviewDTO: ReviewDTO, { setSubmitting }: any) => {
    setSubmitting(true);
    createReview(reviewDTO)
      .then(() => {
        successNotification('Review saved', `${wordPackDTO.name} was successfully saved`);
        fetchAllReviewsDTO();
        fetchAllWordPacksDTO();
      })
      .catch((error) => errorNotification(error.code, error.response.data.message))
      .finally(() => setSubmitting(false));
  };

  return (
    <>
      {button}
      <Modal
        size='md'
        isOpen={isOpen}
        onClose={onClose}
        header={wordPackDTO.name}
        body={(
          <>
            <Flex className='totalWords_container'>
              <TbCards /><Text size={TextSize.MEDIUM} text={wordPackDTO.totalWords} />
            </Flex>
            <Flex className='description_container'>
              <Text size={TextSize.MEDIUM} text={wordPackDTO.description} />
            </Flex>
            <InputFieldsWithButton
              validateOnMount
              initialValues={initialValues}
              validationSchema={validationSchema}
              inputElements={(
                <>
                  <TextInput
                    label='Max New Words Per Day'
                    name='maxNewWordsPerDay'
                    type='number'
                    placeholder='1'
                  />
                  <TextInput
                    label='Max Review Words Per Day'
                    name='maxReviewWordsPerDay'
                    type='number'
                    placeholder='1'
                  />
                </>
              )}
              buttonText='Submit'
              onSubmit={handleOnSubmit}
            />
          </>
        )}
      />
    </>
  );
}

export default CreateReviewWindow;
