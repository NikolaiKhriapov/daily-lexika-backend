import axios from 'axios';

const BASE_URL = process.env.REACT_APP_BASE_URL;

const getAuthConfig = () => ({
    headers: {
        Authorization: `Bearer ${localStorage.getItem('access_token')}`,
    },
});

export const getAllWordPacks = async () => {
    try {
        return await axios.get(
            `${BASE_URL}/chinese-flashcards/word-packs`,
            getAuthConfig(),
        );
    } catch (error) {
        throw error;
    }
};

export const getWordPack = async (wordPackName) => {
    try {
        return await axios.get(
            `${BASE_URL}/chinese-flashcards/word-packs/${wordPackName}`,
            getAuthConfig(),
        );
    } catch (error) {
        throw error;
    }
};

export const getAllWordsForWordPack = async (wordPackName) => {
    try {
        return await axios.get(
            `${BASE_URL}/chinese-flashcards/word-packs/${wordPackName}/words`,
            getAuthConfig(),
        );
    } catch (error) {
        throw error;
    }
};
