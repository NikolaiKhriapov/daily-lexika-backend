import axios from 'axios';
import { ApiEndpointsNotifications } from '../API/apiMethods';

const getAuthConfig = () => ({
  headers: {
    Authorization: `Bearer ${localStorage.getItem('access_token')}`,
  },
});

export const getAllNotifications = async () => {
  try {
    return await axios.get(ApiEndpointsNotifications.getAllNotifications(), getAuthConfig());
  } catch (error) {
    console.error(error);
    throw error;
  }
};

export const readNotification = async (notificationId: number) => {
  try {
    return await axios.patch(ApiEndpointsNotifications.readNotification(notificationId), '', getAuthConfig());
  } catch (error) {
    console.error(error);
    throw error;
  }
};
