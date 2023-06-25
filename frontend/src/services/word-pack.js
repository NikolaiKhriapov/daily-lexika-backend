import axios from "axios";

export const getAllWordPacks = async () => {
    try {
        return await axios.get(
            `${import.meta.env.VITE_API_BASE_URL}/api/v1/vocabulary/word-packs`
        )
    } catch (error) {
        throw error;
    }
}

export const getAllWordsForWordPack = async (wordPackName) => {
    try {
        return await axios.get(
            `${import.meta.env.VITE_API_BASE_URL}/api/v1/vocabulary/word-packs/${wordPackName}`,
            wordPackName
        )
    } catch (error) {
        throw error;
    }
}
