import axios from "axios";

const getAuthConfig = () => ({
    headers: {
        Authorization: `Bearer ${localStorage.getItem("access_token")}`
    }
})

export const getAllNotifications = async () => {
    try {
        return await axios.get(
            `${import.meta.env.VITE_API_BASE_URL}/api/v1/notifications`,
            getAuthConfig()
        )
    } catch (error) {
        throw error;
    }
}

export const readNotification = async (notificationId) => {
    try {
        return await axios.patch(
            `${import.meta.env.VITE_API_BASE_URL}/api/v1/notifications/read/${notificationId}`,
            '',
            getAuthConfig()
        )
    } catch (error) {
        throw error;
    }
}