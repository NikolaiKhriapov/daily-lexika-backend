import React, { useEffect } from 'react';
import { ColorMode, useColorMode } from '@chakra-ui/react';
import { useNavigate } from 'react-router-dom';
import styled from 'styled-components/macro';
import { useAuth } from '../../components/context/AuthContext';
import { AuthFormType, Breakpoint, LocalStorage, Page, Size } from '../../utils/constants';
import AuthForm from '../../components/auth/AuthForm';
import { mediaBreakpointUp } from '../../utils/functions';
import Link from '../../components/common/basic/Link';
import Heading from '../../components/common/basic/Heading';
import { theme } from '../../utils/theme';

export default function AuthPage() {
  const { colorMode } = useColorMode();
  const { user, setUserFromToken } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    if (user) {
      navigate(Page.REVIEWS);
    }
  });

  const authPageMapping = {
    [Page.LOGIN]: {
      heading: 'Sign in to your account',
      link: { href: Page.REGISTER, text: 'Don\'t have an account? Register now' },
      authForm: { formType: AuthFormType.LOGIN, onSuccess: null },
    },
    [Page.REGISTER]: {
      heading: 'Register account',
      link: { href: Page.LOGIN, text: 'Already have an account? Log in now' },
      authForm: {
        formType: AuthFormType.REGISTER,
        onSuccess: (token: string) => {
          localStorage.setItem(LocalStorage.ACCESS_TOKEN, token);
          setUserFromToken();
          navigate(Page.REVIEWS);
        },
      },
    },
  };
  const authPageData = authPageMapping[window.location.pathname as keyof typeof authPageMapping];

  return (
    <Container $colorMode={colorMode}>
      <Heading size={Size.LG}>{authPageData.heading}</Heading>
      <AuthForm formType={authPageData.authForm.formType} onSuccess={authPageData.authForm.onSuccess} />
      <Link href={authPageData.link.href}>{authPageData.link.text}</Link>
    </Container>
  );
}

const Container = styled.div<{ $colorMode: ColorMode }>`
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background-color: ${({ $colorMode }) => theme.colors[$colorMode].background};
  gap: 30px;

  ${mediaBreakpointUp(Breakpoint.TABLET)} {
    gap: 35px;
  }

  ${mediaBreakpointUp(Breakpoint.DESKTOP)} {
    gap: 40px;
  }
`;
