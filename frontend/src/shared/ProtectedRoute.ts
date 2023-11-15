import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../components/context/AuthContext';

function ProtectedRoute({ children }: { children: any }) {
  const { isUserAuthenticated } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    if (!isUserAuthenticated()) {
      navigate('/login');
    }
  }, [isUserAuthenticated]);

  return isUserAuthenticated() ? children : null;
}

export default ProtectedRoute;
