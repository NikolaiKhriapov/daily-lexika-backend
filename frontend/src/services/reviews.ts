import axios from 'axios';
import { ApiEndpointsReviews } from '../API/apiMethods';
import { ReviewDTO } from '../types/types';
import { LocalStorage } from '../utils/constants';

const getAuthConfig = () => ({
  headers: {
    Authorization: `Bearer ${localStorage.getItem(LocalStorage.ACCESS_TOKEN)}`,
  },
});

export const getAllReviews = async () => {
  try {
    return await axios.get(ApiEndpointsReviews.getAllReviews(), getAuthConfig());
  } catch (error) {
    console.error(error);
    throw error;
  }
};

export const getReview = async (reviewId: number) => {
  try {
    return await axios.get(ApiEndpointsReviews.getReview(reviewId), getAuthConfig());
  } catch (error) {
    console.error(error);
    throw error;
  }
};

export const createReview = async (reviewDTO: ReviewDTO) => {
  try {
    return await axios.post(ApiEndpointsReviews.createReview(), reviewDTO, getAuthConfig());
  } catch (error) {
    console.error(error);
    throw error;
  }
};

export const refreshReview = async (reviewId: number) => {
  try {
    return await axios.patch(ApiEndpointsReviews.refreshReview(reviewId), '', getAuthConfig());
  } catch (error) {
    console.error(error);
    throw error;
  }
};

export const removeReview = async (reviewId: number) => {
  try {
    return await axios.delete(ApiEndpointsReviews.removeReview(reviewId), getAuthConfig());
  } catch (error) {
    console.error(error);
    throw error;
  }
};

export const processReviewAction = async (reviewId: number, answer: string | null = null) => {
  try {
    return await axios.get(ApiEndpointsReviews.processReviewAction(reviewId, answer), getAuthConfig());
  } catch (error) {
    console.error(error);
    throw error;
  }
};

export const getWordsForReview = async (reviewId: number) => {
  try {
    return await axios.get(ApiEndpointsReviews.getWordsForReview(reviewId), getAuthConfig());
  } catch (error) {
    console.error(error);
    throw error;
  }
};

export const getReviewStatistics = async (reviewId: number) => {
  try {
    return await axios.get(ApiEndpointsReviews.getReviewStatistics(reviewId), getAuthConfig());
  } catch (error) {
    console.error(error);
    throw error;
  }
};
