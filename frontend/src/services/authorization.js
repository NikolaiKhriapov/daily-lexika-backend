import axios from 'axios';

const BASE_URL = process.env.REACT_APP_BASE_URL;

export const register = async (user) => {
    try {
        return await axios.post(
            `${BASE_URL}/auth/register`,
            user,
        );
    } catch (error) {
        throw error;
    }
};

export const login = async (emailAndPassword) => {
    try {
        return await axios.post(
            `${BASE_URL}/auth/login`,
            emailAndPassword,
        );
    } catch (error) {
        throw error;
    }
};
