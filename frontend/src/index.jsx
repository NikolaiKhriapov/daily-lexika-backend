import React from 'react';
import ReactDOM from 'react-dom/client';
import { ChakraProvider } from '@chakra-ui/react';
import { createStandaloneToast } from '@chakra-ui/react';
import { createBrowserRouter, Navigate, RouterProvider } from 'react-router-dom';
import WordPack from './WordPack.jsx';
import Review from './Review.jsx';
import Statistics from './Statistics.jsx';
import Login from './components/authorization/Login.jsx';
import AuthProvider from './components/context/AuthContext.jsx';
import ProtectedRoute from './shared/ProtectedRoute.js';
import Register from './components/authorization/Register.jsx';

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

ReactDOM
    .createRoot(document.getElementById('root'))
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
