import axios from 'axios';

const BASE_URL = process.env.REACT_APP_BASE_URL;

const getAuthConfig = () => ({
    headers: {
        Authorization: `Bearer ${localStorage.getItem('access_token')}`,
    },
});

export const getAllNotifications = async () => {
    try {
        return await axios.get(
            `${BASE_URL}/notifications`,
            getAuthConfig(),
        );
    } catch (error) {
        throw error;
    }
};

export const readNotification = async (notificationId) => {
    try {
        return await axios.patch(
            `${BASE_URL}/notifications/read/${notificationId}`,
            '',
            getAuthConfig(),
        );
    } catch (error) {
        throw error;
    }
};