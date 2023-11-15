import React, { createContext, useContext, useEffect, useState } from 'react';
import jwtDecode, { JwtPayload } from 'jwt-decode';
import { login as performLogin } from '../../services/authorization';
import { AuthenticatedUser, AuthenticationRequest } from '../../types/types';

interface AuthContextProps {
  user: AuthenticatedUser | null;
  login: (authenticationRequest: AuthenticationRequest) => Promise<void>;
  logout: () => void;
  isUserAuthenticated: () => boolean;
  setUserFromToken: () => void;
}

const AuthContext = createContext<AuthContextProps>({
  user: null,
  login: async () => {
  },
  logout: () => {
  },
  isUserAuthenticated: () => false,
  setUserFromToken: () => {
  },
});

function AuthProvider({ children }: {
  children: any
}) {
  const [user, setUser] = useState<AuthenticatedUser | null>(null);

  const setUserFromToken = () => {
    const token = localStorage.getItem('access_token');
    if (token) {
      const jwtToken: JwtPayload = jwtDecode(token);
      setUser({ username: jwtToken.sub! });
    }
  };

  useEffect(() => {
    setUserFromToken();
  }, []);

  const login = async (authenticationRequest: AuthenticationRequest): Promise<void> => new Promise((resolve, reject) => {
    performLogin(authenticationRequest)
      .then((response) => {
        let jwtToken = response.data.data.authenticationResponse.token;
        localStorage.setItem('access_token', jwtToken);

        jwtToken = jwtDecode(jwtToken);

        setUser({ username: jwtToken.sub });
        resolve();
      })
      .catch((error) => reject(error));
  });

  const logout = () => {
    localStorage.removeItem('access_token');
    setUser(null);
  };

  const isUserAuthenticated = () => {
    const token = localStorage.getItem('access_token');
    if (!token) {
      return false;
    }
    const expiration = (jwtDecode(token) as JwtPayload).exp;
    if (Date.now() > expiration! * 1000) {
      logout();
      return false;
    }
    return true;
  };

  return (
    <AuthContext.Provider value={{ user, login, logout, isUserAuthenticated, setUserFromToken }}>
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => useContext(AuthContext);
export default AuthProvider;
