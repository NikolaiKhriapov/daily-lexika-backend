import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../components/context/AuthContext';
import { Pages } from '../utils/constants';

function ProtectedRoute({ children }: { children: any }) {
  const { isUserAuthenticated } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    if (!isUserAuthenticated()) {
      navigate(Pages.LOGIN);
    }
  }, [isUserAuthenticated]);

  return isUserAuthenticated() ? children : null;
}

export default ProtectedRoute;
