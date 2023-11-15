import axios from 'axios';
import { ApiEndpointsWords } from '../API/apiMethods';

const getAuthConfig = () => ({
  headers: {
    Authorization: `Bearer ${localStorage.getItem('access_token')}`,
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
