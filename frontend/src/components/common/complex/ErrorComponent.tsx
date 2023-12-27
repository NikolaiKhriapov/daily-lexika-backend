import PageLayout from '../../../shared/PageLayout';
import Heading from '../basic/Heading';
import IndexPageContainer from './IndexPageContainer';

export default function ErrorComponent() {
  return (
    <PageLayout>
      <IndexPageContainer>
        <Heading>Oops, there was an error</Heading>
      </IndexPageContainer>
    </PageLayout>
  );
}
