/**
 * 首页
 */
import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Card, CardBody, Loading, Empty } from '@/components/common';
import { message } from '@/components/common/Message';
import { getBookList } from '@/api/book';
import { useCartStore } from '@/stores';
import type { Book, PageBean } from '@/types';

export function HomePage() {
  const [books, setBooks] = useState<PageBean<Book> | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchBooks();
  }, []);

  const fetchBooks = async () => {
    try {
      setLoading(true);
      const data = await getBookList({ pageNum: 1, pageSize: 8 });
      setBooks(data);
    } catch (error) {
      console.error('获取图书列表失败:', error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      {/* Banner */}
      <div className="bg-gradient-to-r from-blue-500 to-blue-600 rounded-lg p-8 mb-8 text-white">
        <h1 className="text-3xl font-bold mb-2">欢迎来到书店</h1>
        <p className="text-blue-100 mb-4">探索海量图书，开启阅读之旅</p>
        <Link
          to="/books"
          className="inline-block bg-white text-blue-500 px-6 py-2 rounded-md font-medium hover:bg-blue-50 transition-colors"
        >
          浏览全部图书
        </Link>
      </div>

      {/* 热门图书 */}
      <section>
        <div className="flex items-center justify-between mb-6">
          <h2 className="text-xl font-bold text-gray-900">热门图书</h2>
          <Link to="/books" className="text-blue-500 hover:underline">
            查看更多 →
          </Link>
        </div>

        {loading ? (
          <Loading text="加载中..." />
        ) : books?.items?.length ? (
          <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-6">
            {books.items.map((book) => (
              <BookCard key={book.bookId} book={book} />
            ))}
          </div>
        ) : (
          <Empty text="暂无图书" />
        )}
      </section>
    </div>
  );
}

/**
 * 图书卡片组件
 */
interface BookCardProps {
  book: Book;
}

function BookCard({ book }: BookCardProps) {
  const { addItem } = useCartStore();

  const handleAddToCart = (e: React.MouseEvent) => {
    e.preventDefault();
    e.stopPropagation();
    addItem(book, 1);
    message.success('已添加到购物车');
  };

  return (
    <Link to={`/books/${book.bookId}`}>
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
          {/* 库存标签 */}
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
            {book.author1}
          </p>
          <div className="mt-auto flex items-center justify-between">
            <span className="text-lg font-bold text-red-500">
              ¥{book.price.toFixed(2)}
            </span>
            {book.stockQuantity > 0 && (
              <button
                onClick={handleAddToCart}
                className="text-blue-500 hover:text-blue-600 text-sm"
              >
                加入购物车
              </button>
            )}
          </div>
        </CardBody>
      </Card>
    </Link>
  );
}

export { BookCard };
