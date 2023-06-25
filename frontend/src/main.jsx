import React from 'react'
import ReactDOM from 'react-dom/client'
import {ChakraProvider} from '@chakra-ui/react'
import {createStandaloneToast} from '@chakra-ui/react'
import {createBrowserRouter, RouterProvider} from "react-router-dom";
import WordPack from "./WordPack.jsx";
import Review from "./Review.jsx";
import Account from "./Account.jsx";

const {ToastContainer} = createStandaloneToast()

const router = createBrowserRouter([
    {
        path: "/word-packs",
        element: <WordPack/>
    },
    {
        path: "/reviews",
        element: <Review/>
    },
    {
        path: "/account",
        element: <Account/>
    },
])

ReactDOM
    .createRoot(document.getElementById('root'))
    .render(
        <React.StrictMode>
            <ChakraProvider>
                <RouterProvider router={router}/>
                <ToastContainer/>
            </ChakraProvider>
        </React.StrictMode>
    )
