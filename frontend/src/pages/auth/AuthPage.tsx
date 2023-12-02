import { useEffect } from 'react';
import { Flex, useColorMode } from '@chakra-ui/react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../components/context/AuthContext';
import { AuthFormType, LocalStorage, Pages } from '../../utils/constants';
import AuthForm from '../../components/auth/AuthForm';
import Link from '../../components/common/basic/Link';
import Heading from '../../components/common/basic/Heading';

function AuthPage() {
  const { colorMode } = useColorMode();
  const { user, setUserFromToken } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    if (user) {
      navigate(Pages.REVIEWS);
    }
  });

  const authPageMapping = {
    [Pages.LOGIN]: {
      heading: 'Sign in to your account',
      link: { href: Pages.REGISTER, text: 'Don\'t have an account? Register now' },
      authForm: { formType: AuthFormType.LOGIN, onSuccess: null },
    },
    [Pages.REGISTER]: {
      heading: 'Register account',
      link: { href: Pages.LOGIN, text: 'Already have an account? Log in now' },
      authForm: {
        formType: AuthFormType.REGISTER,
        onSuccess: (token: string) => {
          localStorage.setItem(LocalStorage.ACCESS_TOKEN, token);
          setUserFromToken();
          navigate(Pages.REVIEWS);
        },
      },
    },
  };
  const authPageData = authPageMapping[window.location.pathname as keyof typeof authPageMapping];

  return (
    <Flex className={`AuthPage_container ${colorMode}`}>
      <Heading level={1} text={authPageData.heading} />
      <AuthForm formType={authPageData.authForm.formType} onSuccess={authPageData.authForm.onSuccess} />
      <Link href={authPageData.link.href} text={authPageData.link.text} />
    </Flex>
  );
}

export default AuthPage;
