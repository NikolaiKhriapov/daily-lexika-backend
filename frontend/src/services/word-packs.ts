import axios from 'axios';
import { ApiEndpointsWordPacks } from '../API/apiMethods';

const getAuthConfig = () => ({
  headers: {
    Authorization: `Bearer ${localStorage.getItem('access_token')}`,
  },
});

export const getAllWordPacks = async () => {
  try {
    return await axios.get(ApiEndpointsWordPacks.getAllWordPacks(), getAuthConfig());
  } catch (error) {
    console.error(error);
    throw error;
  }
};

export const getWordPack = async (wordPackName: string) => {
  try {
    return await axios.get(ApiEndpointsWordPacks.getWordPack(wordPackName), getAuthConfig());
  } catch (error) {
    console.error(error);
    throw error;
  }
};

export const getAllWordsForWordPack = async (wordPackName: string) => {
  try {
    return await axios.get(ApiEndpointsWordPacks.getAllWordsForWordPack(wordPackName), getAuthConfig());
  } catch (error) {
    console.error(error);
    throw error;
  }
};
