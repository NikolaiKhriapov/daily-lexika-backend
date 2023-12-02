import axios from 'axios';
import { ApiEndpointsWords } from '../API/apiMethods';
import { LocalStorage } from '../utils/constants';

const getAuthConfig = () => ({
  headers: {
    Authorization: `Bearer ${localStorage.getItem(LocalStorage.ACCESS_TOKEN)}`,
  },
});

export const getWordStatistics = async () => {
  try {
    return await axios.get(ApiEndpointsWords.getWordStatistics(), getAuthConfig());
  } catch (error) {
    console.error(error);
    throw error;
  }
};
