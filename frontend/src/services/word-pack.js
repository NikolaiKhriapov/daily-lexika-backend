import axios from "axios";

const getAuthConfig = () => ({
    headers: {
        Authorization: `Bearer ${localStorage.getItem("access_token")}`
    }
})

export const getAllWordPacks = async () => {
    try {
        return await axios.get(
            `${import.meta.env.VITE_API_BASE_URL}/api/v1/vocabulary/word-packs`,
            getAuthConfig()
        )
    } catch (error) {
        throw error;
    }
}

export const getWordPack = async (wordPackName) => {
    try {
        return await axios.get(
            `${import.meta.env.VITE_API_BASE_URL}/api/v1/vocabulary/word-packs/${wordPackName}`,
            getAuthConfig()
        )
    } catch (error) {
        throw error;
    }
}

export const getAllWordsForWordPack = async (wordPackName) => {
    try {
        return await axios.get(
            `${import.meta.env.VITE_API_BASE_URL}/api/v1/vocabulary/word-packs/${wordPackName}/words`,
            getAuthConfig()
        )
    } catch (error) {
        throw error;
    }
}
