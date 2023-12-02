import { Flex } from '@chakra-ui/react';
import PageLayout from '../../../shared/PageLayout';
import Heading from '../basic/Heading';

function ErrorComponent() {
  return (
    <PageLayout>
      <Flex className='indexPage_container'>
        <Heading level={2} text='Oops, there was an error' />
      </Flex>
    </PageLayout>
  );
}

export default ErrorComponent;
