import axios from 'axios';
import { AuthenticationRequest, RegistrationRequest } from '../utils/types';
import { ApiEndpointsAuthorization } from '../API/apiMethods';

export const register = async (registrationRequest: RegistrationRequest) => {
  try {
    return await axios.post(ApiEndpointsAuthorization.register, registrationRequest);
  } catch (error) {
    console.error(error);
    throw error;
  }
};

export const login = async (authenticationRequest: AuthenticationRequest) => {
  try {
    return await axios.post(ApiEndpointsAuthorization.login, authenticationRequest);
  } catch (error) {
    console.error(error);
    throw error;
  }
};
