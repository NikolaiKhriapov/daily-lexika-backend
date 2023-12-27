import React from 'react';
import ReactDOM from 'react-dom/client';
import { ChakraProvider, createStandaloneToast } from '@chakra-ui/react';
import { createBrowserRouter, Navigate, RouterProvider } from 'react-router-dom';
import WordPack from './pages/word-packs/WordPack';
import Review from './pages/reviews/Review';
import Statistics from './pages/statistics/Statistics';
import AuthProvider from './components/context/AuthContext';
import ProtectedRoute from './shared/ProtectedRoute';
import { Page } from './utils/constants';
import AuthPage from './pages/auth/AuthPage';
import { theme } from './utils/theme';

const { ToastContainer } = createStandaloneToast();

const router = createBrowserRouter([
  {
    path: Page.REGISTER,
    element: <AuthPage />,
  },
  {
    path: Page.LOGIN,
    element: <AuthPage />,
  },
  {
    path: Page.REVIEWS,
    element: <ProtectedRoute><Review /></ProtectedRoute>,
  },
  {
    path: Page.WORD_PACKS,
    element: <ProtectedRoute><WordPack /></ProtectedRoute>,
  },
  {
    path: Page.STATISTICS,
    element: <ProtectedRoute><Statistics /></ProtectedRoute>,
  },
  {
    path: '/',
    element: <Navigate to={Page.LOGIN} />,
  },
]);

const rootElement = document.getElementById('root') as HTMLElement;

if (rootElement) {
  ReactDOM
    .createRoot(rootElement)
    .render(
      <React.StrictMode>
        <ChakraProvider theme={theme}>
          <AuthProvider>
            <RouterProvider router={router} />
          </AuthProvider>
          <ToastContainer />
        </ChakraProvider>
      </React.StrictMode>,
    );
}
