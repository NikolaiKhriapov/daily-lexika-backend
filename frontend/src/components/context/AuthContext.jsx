import React, { createContext, useContext, useEffect, useState } from "react";
import { login as performLogin } from "../../services/authorization";
import jwtDecode from "jwt-decode";

const AuthContext = createContext({});

const AuthProvider = ({children}) => {

    const [user, setUser] = useState(null);

    const setUserFromToken = () => {
        let token = localStorage.getItem("access_token");
        if (token) {
            token = jwtDecode(token);
            setUser({username: token.sub})
        }
    }

    useEffect(() => {
        setUserFromToken();
    }, [])

    const login = async (usernameAndPassword) => {
        return new Promise((resolve, reject) => {
            performLogin(usernameAndPassword).then(response => {
                const jwtToken = response.data.data.authenticationResponse.token;
                localStorage.setItem("access_token", jwtToken);

                const decodedToken = jwtDecode(jwtToken);

                setUser({username: decodedToken.sub})
                resolve(response);
            }).catch(error => {
                reject(error);
            })
        })
    }

    const logout = () => {
        localStorage.removeItem("access_token");
        setUser(null);
    }

    const isUserAuthenticated = () => {
        const token = localStorage.getItem("access_token");
        if (!token) {
            return false;
        }
        const {exp: expiration} = jwtDecode(token);
        if (Date.now() > expiration * 1000) {
            logout();
            return false;
        }
        return true;
    }

    return (
        <AuthContext.Provider value={{
            user,
            login,
            logout,
            isUserAuthenticated,
            setUserFromToken
        }}>
            {children}
        </AuthContext.Provider>
    )
}

export const useAuth = () => useContext(AuthContext);
export default AuthProvider;