export interface AuthenticatedUser {
  username: string;
}

export interface RegistrationRequest {
  name: string;
  email: string;
  password: string;
}

export interface AuthenticationRequest {
  email: string;
  password: string;
}

export interface UserDTO {
  id?: number;
  name?: string;
  email?: string;
  password?: string;
  roles?: string[];
  currentStreak?: number;
  dateOfLastStreak?: string;
  recordStreak?: number;
}

export interface ReviewDTO {
  id?: number;
  userId?: number;
  maxNewWordsPerDay: number;
  maxReviewWordsPerDay: number;
  wordPackName: string;
  listOfWordId?: number[];
  dateLastCompleted?: string;
  dateGenerated?: string;
}

export interface ReviewStatisticsDTO {
  wordsNew: number;
  wordsInReview: number;
  wordsKnown: number;
  wordsTotal: number;

}

export interface WordDTO {
  id: number;
  nameChineseSimplified: string;
  nameChineseTraditional: string;
  pinyin: string;
  nameEnglish: string;
  nameRussian: string;
  status: Status;
  currentStreak: number;
  totalStreak: number;
  occurrence: number;
  dateOfLastOccurrence: string;
  listOfReviewId: number[];
  listOfChineseCharacterId: number[];
  listOfWordPackNames: string[];
}

export interface WordStatisticsDTO {
  wordsKnown: number;
}

export interface WordPackDTO {
  name: string;
  description: string;
  category: Category;
  totalWords: number;
  review: ReviewDTO;
}

export interface NotificationDTO {
  notificationId: number;
  toUserId: number;
  toUserEmail: string;
  sender: string;
  subject: string;
  message: string;
  sentAt: string;
  isRead: boolean;
}

export enum Status {
  NEW = 'NEW',
  IN_REVIEW = 'IN_REVIEW',
  KNOWN = 'KNOWN',
}

export enum Category {
  HSK,
  WORK,
  NEWS,
  SPORT,
  FOOD,
  TRAVEL,
}
