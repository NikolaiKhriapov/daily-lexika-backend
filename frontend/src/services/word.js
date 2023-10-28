import axios from 'axios';

const BASE_URL = process.env.REACT_APP_BASE_URL;

const getAuthConfig = () => ({
    headers: {
        Authorization: `Bearer ${localStorage.getItem('access_token')}`,
    },
});

export const getWordStatistics = async () => {
    try {
        return await axios.get(
            `${BASE_URL}/chinese-flashcards/words/statistics`,
            getAuthConfig(),
        );
    } catch (error) {
        throw error;
    }
};