import axios from "axios"

const getAuthConfig = () => ({
    headers: {
        Authorization: `Bearer ${localStorage.getItem("access_token")}`
    }
})

export const getAllReviews = async () => {
    try {
        return await axios.get(
            `${import.meta.env.VITE_API_BASE_URL}/api/v1/chinese-flashcards/reviews`,
            getAuthConfig()
        )
    } catch (error) {
        throw error
    }
}

export const getReview = async (reviewId) => {
    try {
        return await axios.get(
            `${import.meta.env.VITE_API_BASE_URL}/api/v1/chinese-flashcards/reviews/${reviewId}`,
            getAuthConfig()
        )
    } catch (error) {
        throw error
    }
}

export const createReview = async (review) => {
    try {
        return await axios.post(
            `${import.meta.env.VITE_API_BASE_URL}/api/v1/chinese-flashcards/reviews`,
            review,
            getAuthConfig()
        )
    } catch (error) {
        throw error
    }
}

export const removeReview = async (reviewId) => {
    try {
        return await axios.delete(
            `${import.meta.env.VITE_API_BASE_URL}/api/v1/chinese-flashcards/reviews/${reviewId}`,
            getAuthConfig()
        )
    } catch (error) {
        throw error
    }
}

export const processReviewAction = async (reviewId, answer = null) => {
    try {
        let url = `${import.meta.env.VITE_API_BASE_URL}/api/v1/chinese-flashcards/reviews/${reviewId}/action`

        if (answer !== null) {
            url += `?answer=${answer}`
        }

        return await axios.get(url, getAuthConfig())
    } catch (error) {
        throw error
    }
}

export const getWordsForReview = async (reviewId) => {
    try {
        return await axios.get(
            `${import.meta.env.VITE_API_BASE_URL}/api/v1/chinese-flashcards/reviews/${reviewId}/words`,
            getAuthConfig()
        )
    } catch (error) {
        throw error
    }
}

export const getReviewStatistics = async (reviewId) => {
    try {
        return await axios.get(
            `${import.meta.env.VITE_API_BASE_URL}/api/v1/chinese-flashcards/reviews/statistics/${reviewId}`,
            getAuthConfig()
        )
    } catch (error) {
        throw erro
    }
}
