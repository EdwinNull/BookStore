/**
 * 图书相关 API
 */
import { get, post, put, del } from '@/utils/request';
import type {
  Book,
  PageBean,
  BookSearchParams,
  BookListParams,
  SupplierBooks,
} from '@/types';

/**
 * 获取图书列表（分页）
 */
export function getBookList(params?: BookListParams): Promise<PageBean<Book>> {
  return get('/api/books', { params });
}

/**
 * 获取图书详情
 */
export function getBookDetail(bookId: number): Promise<Book> {
  return get(`/api/books/${bookId}`);
}

/**
 * 搜索图书
 */
export function searchBooks(params: BookSearchParams): Promise<Book[]> {
  return get('/api/books/findBook', { params });
}

/**
 * 添加图书
 */
export function addBook(book: Partial<Book>): Promise<void> {
  return post('/api/books/add', book);
}

/**
 * 更新图书
 */
export function updateBook(book: Partial<Book>): Promise<void> {
  return put('/api/books/update', book);
}

/**
 * 删除图书
 */
export function deleteBook(bookId: number): Promise<void> {
  return del(`/api/books/delete/${bookId}`);
}

/**
 * 添加供应商图书关联
 */
export function addSupplierBooks(supplierBooks: SupplierBooks[]): Promise<void> {
  return post('/api/books/addSupplierBooks', supplierBooks);
}
