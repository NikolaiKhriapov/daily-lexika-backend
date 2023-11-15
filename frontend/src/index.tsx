import React from 'react';
import ReactDOM from 'react-dom/client';
import { ChakraProvider, createStandaloneToast } from '@chakra-ui/react';
import { createBrowserRouter, Navigate, RouterProvider } from 'react-router-dom';
import WordPack from './WordPack';
import Review from './Review';
import Statistics from './Statistics';
import Login from './components/authorization/Login';
import AuthProvider from './components/context/AuthContext';
import ProtectedRoute from './shared/ProtectedRoute';
import Register from './components/authorization/Register';

const { ToastContainer } = createStandaloneToast();

const router = createBrowserRouter([
  {
    path: '/register',
    element: <Register />,
  },
  {
    path: '/login',
    element: <Login />,
  },
  {
    path: '/word-packs',
    element: <ProtectedRoute><WordPack /></ProtectedRoute>,
  },
  {
    path: '/reviews',
    element: <ProtectedRoute><Review /></ProtectedRoute>,
  },
  {
    path: '/statistics',
    element: <ProtectedRoute><Statistics /></ProtectedRoute>,
  },
  {
    path: '/',
    element: <Navigate to='/login' />,
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
