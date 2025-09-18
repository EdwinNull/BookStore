import request from '@/utils/request'
import type { Book, PageBean, BookSearchParams, SupplierBooks, ApiResponse } from '@/types'

export const bookApi = {
  // 获取图书列表
  getBooks(params: BookSearchParams) {
    return request.get<ApiResponse<PageBean<Book>>>('/books', { params })
  },

  // 获取图书详情
  getBookDetail(bookId: number) {
    return request.get<ApiResponse<Book>>(`/books/${bookId}`)
  },

  // 添加图书
  addBook(book: Partial<Book>) {
    return request.post<ApiResponse<string>>('/books/add', book)
  },

  // 更新图书
  updateBook(book: Partial<Book>) {
    return request.put<ApiResponse<string>>('/books/update', book)
  },

  // 删除图书
  deleteBook(bookId: number) {
    return request.delete<ApiResponse<string>>(`/books/delete/${bookId}`)
  },

  // 搜索图书
  searchBooks(params: {
    title?: string
    keywords?: string
    author1?: string
    author2?: string
    author3?: string
    author4?: string
    publisher?: string
  }) {
    return request.get<ApiResponse<Book[]>>('/books/findBook', { params })
  },

  // 添加供应商图书关系
  addSupplierBooks(supplierBooks: SupplierBooks[]) {
    return request.post<ApiResponse<string>>('/books/addSupplierBooks', supplierBooks)
  }
}