const BASE_URL = process.env.REACT_APP_BASE_URL as string;

const URL_AUTH = `${BASE_URL}/auth`;
export const ApiEndpointsAuthorization = {
  register: `${URL_AUTH}/register`,
  login: `${URL_AUTH}/login`,
};

const URL_NOTIFICATIONS = `${BASE_URL}/notifications`;
export const ApiEndpointsNotifications = {
  getAllNotifications: () => `${URL_NOTIFICATIONS}`,
  readNotification: (notificationId: number) => `${URL_NOTIFICATIONS}/read/${notificationId}`,
};

const URL_REVIEWS = `${BASE_URL}/chinese-flashcards/reviews`;
export const ApiEndpointsReviews = {
  getAllReviews: () => `${URL_REVIEWS}`,
  getReview: (reviewId: number) => `${URL_REVIEWS}/${reviewId}`,
  createReview: () => `${URL_REVIEWS}`,
  refreshReview: (reviewId: number) => `${URL_REVIEWS}/${reviewId}`,
  removeReview: (reviewId: number) => `${URL_REVIEWS}/${reviewId}`,
  processReviewAction: (reviewId: number, answer: string | null = null) => `${URL_REVIEWS}/${reviewId}/action${answer !== null ? `?answer=${answer}` : ''}`,
  getWordsForReview: (reviewId: number) => `${URL_REVIEWS}/${reviewId}/words`,
  getReviewStatistics: (reviewId: number) => `${URL_REVIEWS}/statistics/${reviewId}`,
};

const URL_USERS = `${BASE_URL}/user`;
export const ApiEndpointsUsers = {
  showUserAccount: () => `${URL_USERS}/account`,
  updateUserInfo: () => `${URL_USERS}/account/info`,
  deleteAccount: () => `${URL_USERS}/account`,
  getUserStatistics: () => `${URL_USERS}/statistics`,
  updateUserStreak: () => `${URL_USERS}/statistics/streak`,
};

const URL_WORD_PACKS = `${BASE_URL}/chinese-flashcards/word-packs`;
export const ApiEndpointsWordPacks = {
  getAllWordPacks: () => `${URL_WORD_PACKS}`,
  getWordPack: (wordPackName: string) => `${URL_WORD_PACKS}/${wordPackName}`,
  getAllWordsForWordPack: (wordPackName: string) => `${URL_WORD_PACKS}/${wordPackName}/words`,
};

const URL_WORDS = `${BASE_URL}/chinese-flashcards/words`;
export const ApiEndpointsWords = {
  getWordStatistics: () => `${URL_WORDS}/statistics`,
};
