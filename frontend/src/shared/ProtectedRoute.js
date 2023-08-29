import {useEffect} from "react";
import {useAuth} from "../components/context/AuthContext.jsx";
import {useNavigate} from "react-router-dom";

const ProtectedRoute = ({children}) => {
    const {isUserAuthenticated} = useAuth();
    const navigate = useNavigate();

    useEffect(() => {
        if (!isUserAuthenticated()) {
            navigate("/login");
        }
    }, [isUserAuthenticated]);

    return isUserAuthenticated() ? children : null;
};

export default ProtectedRoute;
