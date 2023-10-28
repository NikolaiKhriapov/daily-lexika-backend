import axios from 'axios';

const BASE_URL = process.env.REACT_APP_BASE_URL;

const getAuthConfig = () => ({
    headers: {
        Authorization: `Bearer ${localStorage.getItem('access_token')}`,
    },
});

export const showUserAccount = async () => {
    try {
        return await axios.get(
            `${BASE_URL}/user/account`,
            getAuthConfig(),
        );
    } catch (error) {
        throw error;
    }
};

export const updateUserInfo = async (userDTO) => {
    try {
        return await axios.patch(
            `${BASE_URL}/user/account/info`,
            userDTO,
            getAuthConfig(),
        );
    } catch (error) {
        throw error;
    }
};

export const deleteAccount = async () => {
    try {
        return await axios.delete(
            `${BASE_URL}/user/account`,
            getAuthConfig(),
        );
    } catch (error) {
        throw error;
    }
};

export const getUserStatistics = async () => {
    try {
        return await axios.get(
            `${BASE_URL}/user/statistics`,
            getAuthConfig(),
        );
    } catch (error) {
        throw error;
    }
};

export const updateUserStreak = async () => {
    try {
        await axios.get(
            `${BASE_URL}/user/statistics/streak`,
            getAuthConfig(),
        );
    } catch (error) {
        throw error;
    }
};