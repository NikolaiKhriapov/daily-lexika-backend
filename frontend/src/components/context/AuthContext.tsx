import { createContext, useContext, useEffect, useState } from 'react';
import { jwtDecode, JwtPayload } from 'jwt-decode';
import { login as performLogin } from '../../services/authorization';
import { AuthenticatedUser, AuthenticationRequest } from '../../utils/types';
import { LocalStorage } from '../../utils/constants';

type Props = {
  user: AuthenticatedUser | null;
  login: (authenticationRequest: AuthenticationRequest) => Promise<void>;
  logout: () => void;
  isUserAuthenticated: () => boolean;
  setUserFromToken: () => void;
};

const AuthContext = createContext<Props>({
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
    const token = localStorage.getItem(LocalStorage.ACCESS_TOKEN);
    if (token) {
      const jwtToken: JwtPayload = jwtDecode(token);
      setUser({ username: jwtToken.sub! });
    }
  };

  useEffect(() => {
    setUserFromToken();
  }, []);

  const login = async (authenticationRequest: AuthenticationRequest): Promise<void> =>
    new Promise((resolve, reject) => {
      performLogin(authenticationRequest)
        .then((response) => {
          let jwtToken = response.data.data.authenticationResponse.token;
          localStorage.setItem(LocalStorage.ACCESS_TOKEN, jwtToken);

          jwtToken = jwtDecode(jwtToken);

          setUser({ username: jwtToken.sub });
          resolve();
        })
        .catch((error) => reject(error));
    });

  const logout = () => {
    localStorage.removeItem(LocalStorage.ACCESS_TOKEN);
    setUser(null);
  };

  const isUserAuthenticated = () => {
    const token = localStorage.getItem(LocalStorage.ACCESS_TOKEN);
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
