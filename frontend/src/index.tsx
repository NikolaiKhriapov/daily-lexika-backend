import React from 'react';
import ReactDOM from 'react-dom/client';
import { ChakraProvider, createStandaloneToast } from '@chakra-ui/react';
import { createBrowserRouter, Navigate, RouterProvider } from 'react-router-dom';
import WordPack from './pages/word-packs/WordPack';
import Review from './pages/reviews/Review';
import Statistics from './pages/statistics/Statistics';
import AuthProvider from './components/context/AuthContext';
import ProtectedRoute from './shared/ProtectedRoute';
import { Pages } from './utils/constants';
import AuthPage from './pages/auth/AuthPage';
import './styles/index.scss';

const { ToastContainer } = createStandaloneToast();

const router = createBrowserRouter([
  {
    path: Pages.REGISTER,
    element: <AuthPage />,
  },
  {
    path: Pages.LOGIN,
    element: <AuthPage />,
  },
  {
    path: Pages.REVIEWS,
    element: <ProtectedRoute><Review /></ProtectedRoute>,
  },
  {
    path: Pages.WORD_PACKS,
    element: <ProtectedRoute><WordPack /></ProtectedRoute>,
  },
  {
    path: Pages.STATISTICS,
    element: <ProtectedRoute><Statistics /></ProtectedRoute>,
  },
  {
    path: '/',
    element: <Navigate to={Pages.LOGIN} />,
  },
]);

const rootElement = document.getElementById('root') as HTMLElement;

if (rootElement) {
  ReactDOM
    .createRoot(rootElement)
    .render(
      <React.StrictMode>
        <ChakraProvider>
          <AuthProvider>
            <RouterProvider router={router} />
          </AuthProvider>
          <ToastContainer />
        </ChakraProvider>
      </React.StrictMode>,
    );
}
