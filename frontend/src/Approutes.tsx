import { Routes, Route } from 'react-router-dom';
import Register from './features/auth/Register';
import Login from './features/auth/Login';
import NonExistent from './features/nonexistent/NonExistent';
import EscolherG from './features/onboarding/Onboarding';
import Profile from './features/profile/Profile';
import Shelf from './features/shelf/Shelf';
import Swipe from './features/swipe/Swipe';

export default function Approutes() {
  return (
    <Routes>
      <Route path="/" element={<Swipe />} />
      <Route path="/register" element={<Register />} />
      <Route path="/login" element={<Login />} />
      <Route path="/onboarding" element={<EscolherG />} />
      <Route path="/profile" element={<Profile />} />
      <Route path="/shelf" element={<Shelf />} />
      <Route path="/swipe" element={<Swipe />} />
      <Route path="*" element={<NonExistent />} />
    </Routes>
  );
}
