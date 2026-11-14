import { render, screen } from '@testing-library/react';

function MockHeader() {
  return <header>BookSwipe</header>;
}

test('Header should be on page', async () => {
  render(<MockHeader />);
  expect(screen.getByText('BookSwipe')).toBeInTheDocument();
});
