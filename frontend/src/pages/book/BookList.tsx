/**
 * 图书列表页面
 */
import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Card, CardBody, Input, Button, Pagination, Loading, Empty } from '@/components/common';
import { getBookList, searchBooks } from '@/api/book';
import type { Book, PageBean, BookSearchParams } from '@/types';

export function BookListPage() {
  const [books, setBooks] = useState<PageBean<Book> | null>(null);
  const [loading, setLoading] = useState(true);
  const [pageNum, setPageNum] = useState(1);
  const pageSize = 12;

  // 搜索表单
  const [searchParams, setSearchParams] = useState<BookSearchParams>({
    title: '',
    author1: '',
    publisher: '',
  });
  const [isSearchMode, setIsSearchMode] = useState(false);
  const [searchResults, setSearchResults] = useState<Book[]>([]);

  useEffect(() => {
    if (!isSearchMode) {
      fetchBooks();
    }
  }, [pageNum, isSearchMode]);

  const fetchBooks = async () => {
    try {
      setLoading(true);
      const data = await getBookList({ pageNum, pageSize });
      setBooks(data);
    } catch (error) {
      console.error('获取图书列表失败:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = async () => {
    // 检查是否有搜索条件
    const hasSearchCondition = Object.values(searchParams).some((v) => v.trim());

    if (!hasSearchCondition) {
      setIsSearchMode(false);
      setPageNum(1);
      return;
    }

    try {
      setLoading(true);
      setIsSearchMode(true);
      const results = await searchBooks(searchParams);
      setSearchResults(results);
    } catch (error) {
      console.error('搜索失败:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleReset = () => {
    setSearchParams({
      title: '',
      author1: '',
      publisher: '',
    });
    setIsSearchMode(false);
    setPageNum(1);
  };

  // 获取作者显示文本
  const getAuthors = (book: Book) => {
    const authors = [book.author1, book.author2, book.author3, book.author4].filter(Boolean);
    return authors.join(', ') || '未知';
  };

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      {/* 搜索区域 */}
      <Card className="mb-6">
        <CardBody>
          <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
            <Input
              placeholder="书名"
              value={searchParams.title || ''}
              onChange={(e) => setSearchParams({ ...searchParams, title: e.target.value })}
            />
            <Input
              placeholder="作者"
              value={searchParams.author1 || ''}
              onChange={(e) => setSearchParams({ ...searchParams, author1: e.target.value })}
            />
            <Input
              placeholder="出版社"
              value={searchParams.publisher || ''}
              onChange={(e) => setSearchParams({ ...searchParams, publisher: e.target.value })}
            />
            <div className="flex gap-2">
              <Button onClick={handleSearch} className="flex-1">
                搜索
              </Button>
              <Button variant="secondary" onClick={handleReset}>
                重置
              </Button>
            </div>
          </div>
        </CardBody>
      </Card>

      {/* 图书列表 */}
      {loading ? (
        <Loading text="加载中..." />
      ) : isSearchMode ? (
        // 搜索结果
        <>
          <p className="text-gray-500 mb-4">
            搜索到 {searchResults.length} 本图书
          </p>
          {searchResults.length > 0 ? (
            <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-6">
              {searchResults.map((book) => (
                <Link key={book.bookId} to={`/books/${book.bookId}`}>
                  <Card hover className="h-full flex flex-col">
                    <div className="aspect-[3/4] bg-gray-100 relative overflow-hidden">
                      {book.coverImage ? (
                        <img
                          src={book.coverImage}
                          alt={book.title}
                          className="w-full h-full object-cover"
                        />
                      ) : (
                        <div className="w-full h-full flex items-center justify-center text-gray-400">
                          <svg
                            className="w-16 h-16"
                            fill="none"
                            viewBox="0 0 24 24"
                            stroke="currentColor"
                          >
                            <path
                              strokeLinecap="round"
                              strokeLinejoin="round"
                              strokeWidth={1}
                              d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253"
                            />
                          </svg>
                        </div>
                      )}
                      {book.stockQuantity <= 0 && (
                        <div className="absolute top-2 right-2 bg-red-500 text-white text-xs px-2 py-1 rounded">
                          缺货
                        </div>
                      )}
                    </div>
                    <CardBody className="flex-1 flex flex-col">
                      <h3 className="font-medium text-gray-900 line-clamp-2 mb-1">
                        {book.title}
                      </h3>
                      <p className="text-sm text-gray-500 mb-2 line-clamp-1">
                        {getAuthors(book)}
                      </p>
                      <div className="mt-auto">
                        <span className="text-lg font-bold text-red-500">
                          ¥{book.price.toFixed(2)}
                        </span>
                      </div>
                    </CardBody>
                  </Card>
                </Link>
              ))}
            </div>
          ) : (
            <Empty text="未找到相关图书" />
          )}
        </>
      ) : (
        // 分页列表
        <>
          {books?.items?.length ? (
            <>
              <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-6 mb-6">
                {books.items.map((book) => (
                  <Link key={book.bookId} to={`/books/${book.bookId}`}>
                    <Card hover className="h-full flex flex-col">
                      <div className="aspect-[3/4] bg-gray-100 relative overflow-hidden">
                        {book.coverImage ? (
                          <img
                            src={book.coverImage}
                            alt={book.title}
                            className="w-full h-full object-cover"
                          />
                        ) : (
                          <div className="w-full h-full flex items-center justify-center text-gray-400">
                            <svg
                              className="w-16 h-16"
                              fill="none"
                              viewBox="0 0 24 24"
                              stroke="currentColor"
                            >
                              <path
                                strokeLinecap="round"
                                strokeLinejoin="round"
                                strokeWidth={1}
                                d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253"
                              />
                            </svg>
                          </div>
                        )}
                        {book.stockQuantity <= 0 && (
                          <div className="absolute top-2 right-2 bg-red-500 text-white text-xs px-2 py-1 rounded">
                            缺货
                          </div>
                        )}
                      </div>
                      <CardBody className="flex-1 flex flex-col">
                        <h3 className="font-medium text-gray-900 line-clamp-2 mb-1">
                          {book.title}
                        </h3>
                        <p className="text-sm text-gray-500 mb-2 line-clamp-1">
                          {getAuthors(book)}
                        </p>
                        <div className="mt-auto">
                          <span className="text-lg font-bold text-red-500">
                            ¥{book.price.toFixed(2)}
                          </span>
                        </div>
                      </CardBody>
                    </Card>
                  </Link>
                ))}
              </div>

              <Pagination
                current={pageNum}
                total={books.total}
                pageSize={pageSize}
                onChange={setPageNum}
              />
            </>
          ) : (
            <Empty text="暂无图书" />
          )}
        </>
      )}
    </div>
  );
}
