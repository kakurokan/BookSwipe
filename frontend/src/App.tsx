import Approutes from './Approutes';
import type { components } from './api/types.gen';
import { useQuery } from '@tanstack/react-query';
import { apiFetch } from './api/client';
import './App.css';

type Book = components['schemas']['Book'];

function App() {
  const { data, isPending, error } = useQuery({
    queryKey: ['deck'],
    queryFn: () => apiFetch<Book[]>('/deck'),
  });
  console.log({ data, isPending, error });
  return <Approutes />;
}

export default App;
