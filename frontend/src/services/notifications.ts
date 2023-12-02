import axios from 'axios';
import { ApiEndpointsNotifications } from '../API/apiMethods';
import { LocalStorage } from '../utils/constants';

const getAuthConfig = () => ({
  headers: {
    Authorization: `Bearer ${localStorage.getItem(LocalStorage.ACCESS_TOKEN)}`,
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
