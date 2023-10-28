import axios from 'axios';

const BASE_URL = process.env.REACT_APP_BASE_URL;

const getAuthConfig = () => ({
    headers: {
        Authorization: `Bearer ${localStorage.getItem('access_token')}`,
    },
});

export const getAllReviews = async () => {
    try {
        return await axios.get(
            `${BASE_URL}/chinese-flashcards/reviews`,
            getAuthConfig(),
        );
    } catch (error) {
        throw error;
    }
};

export const getReview = async (reviewId) => {
    try {
        return await axios.get(
            `${BASE_URL}/chinese-flashcards/reviews/${reviewId}`,
            getAuthConfig(),
        );
    } catch (error) {
        throw error;
    }
};

export const createReview = async (review) => {
    try {
        return await axios.post(
            `${BASE_URL}/chinese-flashcards/reviews`,
            review,
            getAuthConfig(),
        );
    } catch (error) {
        throw error;
    }
};

export const refreshReview = async (reviewId) => {
    try {
        return await axios.patch(
            `${BASE_URL}/chinese-flashcards/reviews/${reviewId}`,
            '',
            getAuthConfig(),
        );
    } catch (error) {
        throw error;
    }
};

export const removeReview = async (reviewId) => {
    try {
        return await axios.delete(
            `${BASE_URL}/chinese-flashcards/reviews/${reviewId}`,
            getAuthConfig(),
        );
    } catch (error) {
        throw error;
    }
};

export const processReviewAction = async (reviewId, answer = null) => {
    try {
        let url = `${BASE_URL}/chinese-flashcards/reviews/${reviewId}/action`;

        if (answer !== null) {
            url += `?answer=${answer}`;
        }

        return await axios.get(url, getAuthConfig());
    } catch (error) {
        throw error;
    }
};

export const getWordsForReview = async (reviewId) => {
    try {
        return await axios.get(
            `${BASE_URL}/chinese-flashcards/reviews/${reviewId}/words`,
            getAuthConfig(),
        );
    } catch (error) {
        throw error;
    }
};

export const getReviewStatistics = async (reviewId) => {
    try {
        return await axios.get(
            `${BASE_URL}/chinese-flashcards/reviews/statistics/${reviewId}`,
            getAuthConfig(),
        );
    } catch (error) {
        throw error;
    }
};
