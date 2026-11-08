import { http, HttpResponse } from 'msw';
import type { components } from '../api/types.gen';
import { z } from 'zod';

type Book = components['schemas']['Book'];
type Swipe = components['schemas']['Swipe'];
type SwipeStatus = components['schemas']['SwipeStatus'];

const stringToken = 'chave_secreta_BookSwipe';
const emailSchema = z.string().email();
const mockBooks: Book[] = [
  {
    bookId: 1,
    title: 'PlaceHolder1',
    authors: ['Just another me'],
  },
  {
    bookId: 2,
    title: 'PlaceHolder2',
    authors: ['Just another me'],
  },
  {
    bookId: 3,
    title: 'PlaceHolder3',
    authors: ['Just another me'],
  },
];

interface MockshelfItem {
  swipedId: number;
  status: SwipeStatus;
  rating?: number;
  book: Book;
}
const mockShelf: MockshelfItem[] = [
  {
    swipedId: 1,
    status: 'READ',
    rating: 5,
    book: mockBooks[1],
  },
  {
    swipedId: 2,
    status: 'TO_READ',
    book: mockBooks[1],
  },
];
const mockGenre = [
  {
    genreId: 1,
    name: 'Fantasy',
  },
  {
    genreId: 2,
    name: 'Romance',
  },
  {
    genreId: 3,
    name: 'Fiction',
  },
  {
    genreId: 4,
    name: 'Science Fiction',
  },
  {
    genreId: 5,
    name: 'Thriller',
  },
  {
    genreId: 6,
    name: 'Mystery',
  },
  {
    genreId: 7,
    name: 'Horror',
  },
];
export interface MockUser {
  name: string;
  email: string;
  password: string;
}

export const mockUsers: MockUser[] = [
  {
    name: 'Marta Silva',
    email: 'marta@example.com',
    password: 'a-long-password',
  },
  {
    name: 'João Santos',
    email: 'joao.santos@example.com',
    password: 'Password123!',
  },
  {
    name: 'Ana Oliveira',
    email: 'ana.oliveira@example.com',
    password: 'securePassword456',
  },
  {
    name: 'Carlos Ferreira',
    email: 'carlos.ferreira@example.com',
    password: 'mySecretPass789',
  },
];
let nextSwipeId = 1;
const mockSwipes: Swipe[] = [];
function isAuthenticated(request: Request) {
  const authHeader = request.headers.get('authorization');
  return authHeader && authHeader.startsWith('Bearer ');
}

export const handlers = [
  http.get('/api/v1/deck', async ({ request }) => {
    if (!isAuthenticated(request)) {
      return HttpResponse.json(
        {
          code: 'UnAuthenticated',
          message: 'authentication token invalid or missing',
        },
        { status: 401 },
      );
    }
    const url = new URL(request.url);
    const size = Number(url.searchParams.get('size')) || 3;
    return HttpResponse.json(mockBooks.slice(0, size), { status: 200 });
  }),

  http.get('/api/v1/deck/popular', async ({ request }) => {
    if (!isAuthenticated(request)) {
      return HttpResponse.json(
        {
          code: 'UnAuthenticated',
          message: 'authentication token invalid or missing',
        },
        { status: 401 },
      );
    }
    const url = new URL(request.url);
    const size = Number(url.searchParams.get('size')) || 1;
    return HttpResponse.json(mockBooks.slice(0, size), { status: 200 });
  }),

  http.post('/api/v1/swipes', async ({ request }) => {
    if (!isAuthenticated(request)) {
      return HttpResponse.json(
        {
          code: 'UnAuthenticated',
          message: 'authentication token invalid or missing',
        },
        { status: 401 },
      );
    }
    const body = (await request.json()) as {
      bookId?: number;
      rating?: number;
      status?: SwipeStatus;
    };

    if (!body.bookId || !body.status) {
      return HttpResponse.json(
        { code: 'BadRequest', message: 'bookId or status missing' },
        { status: 400 },
      );
    }

    if (body.rating !== undefined && body.status !== 'READ') {
      return HttpResponse.json(
        {
          code: 'BadRequest',
          message: 'not possible to rate a book if you didint read it',
        },
        { status: 400 },
      );
    }

    if (body.rating !== undefined && (body.rating < 0.5 || body.rating > 5)) {
      return HttpResponse.json(
        { code: 'BadRequest', message: 'not a valid rating' },
        { status: 400 },
      );
    }

    const bookExist = mockBooks.find((v) => v.bookId === body.bookId);
    if (!bookExist) {
      return HttpResponse.json(
        { code: 'NotFound', message: 'Not a valid book' },
        { status: 404 },
      );
    }

    const now = new Date().toISOString();
    const mockSwipe: Swipe = {
      swipeId: nextSwipeId++,
      bookId: body.bookId,
      status: body.status,
      rating: body.status === 'READ' ? (body.rating ?? null) : null,
      swipedAt: now,
      updatedAt: now,
    };

    mockSwipes.push(mockSwipe);
    return HttpResponse.json(mockSwipe, { status: 201 });
  }),
  http.delete('/api/v1/swipes/last', async ({ request }) => {
    if (!isAuthenticated(request)) {
      return HttpResponse.json(
        {
          code: 'UnAuthenticated',
          message: 'authentication token invalid or missing',
        },
        { status: 401 },
      );
    }

    if (mockSwipes.length === 0) {
      return HttpResponse.json(
        { code: 'Error', message: 'The user has no swipes to undo' },
        { status: 404 },
      );
    }
    const mockSwipe = mockSwipes.pop();
    const bookId = mockSwipe?.bookId;
    const bookWanted = mockBooks.find((v) => v.bookId === bookId);
    if (!bookWanted) {
      return HttpResponse.json(
        { code: 'BadRequest', message: 'Last swipe invalid' },
        { status: 400 },
      );
    }
    return HttpResponse.json(bookWanted, { status: 200 });
  }),
  http.patch<{ id: string }>(
    '/api/v1/swipes/:id',
    async ({ request, params }) => {
      if (!isAuthenticated(request)) {
        return HttpResponse.json(
          {
            code: 'UnAuthenticated',
            message: 'authentication token invalid or missing',
          },
          { status: 401 },
        );
      }
      const { id } = params;
      if (!id) {
        return HttpResponse.json(
          { code: 'BadRequest', message: 'Missing swipeId' },
          { status: 400 },
        );
      }
      const idNumber = parseInt(id);
      const existSwipe = mockSwipes.find((p) => {
        return p.swipeId === idNumber;
      });
      if (existSwipe === undefined) {
        return HttpResponse.json(
          { code: 'BadRequest', message: 'Invalid swipedId' },
          { status: 404 },
        );
      }
      const body = (await request.json()) as {
        status: SwipeStatus;
        rating?: number;
      };
      if (body.status === 'TO_READ') {
        return HttpResponse.json(
          { code: 'BadRequest', message: 'Invalid status' },
          { status: 400 },
        );
      }
      if (body.status === 'READ') {
        if (body.rating === undefined) {
          existSwipe.status = body.status;
          return HttpResponse.json(existSwipe, { status: 200 });
        }
        if (body.rating < 0.5 || body.rating > 5) {
          return HttpResponse.json(
            { code: 'BadRequest', message: 'Invalid rating' },
            { status: 400 },
          );
        }
        existSwipe.status = body.status;
        existSwipe.rating = body.rating;
        return HttpResponse.json(existSwipe, { status: 200 });
      }

      if (body.status === 'REJECTED') {
        existSwipe.status = body.status;
        return HttpResponse.json(existSwipe, { status: 200 });
      }
      return HttpResponse.json(
        { code: 'BadRequest', message: 'Invalid status' },
        { status: 400 },
      );
    },
  ),
  http.get('/api/v1/shelf', async ({ request }) => {
    if (!isAuthenticated(request)) {
      return HttpResponse.json(
        {
          code: 'UnAuthenticated',
          message: 'authentication token invalid or missing',
        },
        { status: 401 },
      );
    }
    return HttpResponse.json(mockShelf, { status: 200 });
  }),
  http.get('/api/v1/genres', async ({ request }) => {
    if (!isAuthenticated(request)) {
      return HttpResponse.json(
        {
          code: 'UnAuthenticated',
          message: 'authentication token invalid or missing',
        },
        { status: 401 },
      );
    }
    return HttpResponse.json(mockGenre, { status: 200 });
  }),
  http.put('/api/v1/me/genres', async ({ request }) => {
    if (!isAuthenticated(request)) {
      return HttpResponse.json(
        {
          code: 'UnAuthenticated',
          message: 'authentication token invalid or missing',
        },
        { status: 401 },
      );
    }
    const body = (await request.json()) as {
      genreIds?: number[];
    };
    const genreIds = body?.genreIds;

    if (!Array.isArray(genreIds) || genreIds.length < 3) {
      return HttpResponse.json(
        { code: 'BadRequest', message: 'At least 3 genres are required' },
        { status: 400 },
      );
    }

    if (new Set(genreIds).size !== genreIds.length) {
      return HttpResponse.json(
        { code: 'BadRequest', message: 'Genres must not repeat' },
        { status: 400 },
      );
    }

    const allExist = genreIds.every((id) =>
      mockGenre.some((g) => g.genreId === id),
    );
    if (!allExist) {
      return HttpResponse.json(
        { code: 'BadRequest', message: 'Unknown genre id' },
        { status: 400 },
      );
    }

    return new HttpResponse(null, { status: 204 });
  }),
  http.post('/api/v1/auth/register', async ({ request }) => {
    const body = (await request.json()) as {
      name: string;
      email: string;
      password: string;
    };

    if (!body.name || !body.email || !body.password) {
      return HttpResponse.json(
        { code: 'BadRequest', message: 'Missing arguments' },
        { status: 400 },
      );
    }
    if (mockUsers.find((p) => p.email === body.email) !== undefined) {
      return HttpResponse.json(
        {
          code: 'Conflict',
          message: 'An account with the corresponding email already exist',
        },
        { status: 409 },
      );
    }
    if (!emailSchema.safeParse(body.email).success) {
      return HttpResponse.json(
        { code: 'BadRequest', message: 'Invalid email format' },
        { status: 400 },
      );
    }
    if (body.name.length < 1 || body.name.length > 100) {
      return HttpResponse.json(
        { code: 'BadRequest', message: 'Invalid name size' },
        { status: 400 },
      );
    }
    if (body.password.length < 8 || body.password.length > 72) {
      return HttpResponse.json(
        { code: 'BadRequest', message: 'Invalide password size' },
        { status: 400 },
      );
    }
    const newUser: MockUser = {
      name: body.name,
      email: body.email,
      password: body.password,
    };
    const token = stringToken;
    mockUsers.push(newUser);
    return HttpResponse.json({ accessToken: token }, { status: 201 });
  }),
  http.post('/api/v1/auth/login', async ({ request }) => {
    const body = (await request.json()) as {
      email: string;
      password: string;
    };
    if (!body.email || !body.password) {
      return HttpResponse.json(
        { code: 'BadRequest', message: 'You type a email and password' },
        { status: 400 },
      );
    }
    const emailExists = mockUsers.find((p) => p.email === body.email);
    if (emailExists === undefined || emailExists.password !== body.password) {
      return HttpResponse.json(
        { code: 'Error', message: 'Wrong email or password' },
        { status: 401 },
      );
    }

    const token = stringToken;
    return HttpResponse.json({ accessToken: token }, { status: 200 });
  }),
];
