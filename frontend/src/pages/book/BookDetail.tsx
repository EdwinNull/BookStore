/**
 * 图书详情页面
 */
import { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { Card, CardBody, Button, Loading } from '@/components/common';
import { message } from '@/components/common/Message';
import { getBookDetail } from '@/api/book';
import { useCartStore } from '@/stores';
import type { Book } from '@/types';

export function BookDetailPage() {
  const { id } = useParams<{ id: string }>();
  const [book, setBook] = useState<Book | null>(null);
  const [loading, setLoading] = useState(true);
  const [quantity, setQuantity] = useState(1);
  const { addItem } = useCartStore();

  useEffect(() => {
    if (id) {
      fetchBook(parseInt(id));
    }
  }, [id]);

  const fetchBook = async (bookId: number) => {
    try {
      setLoading(true);
      const data = await getBookDetail(bookId);
      setBook(data);
    } catch (error) {
      message.error('获取图书详情失败');
    } finally {
      setLoading(false);
    }
  };

  const handleAddToCart = () => {
    if (!book) return;
    addItem(book, quantity);
    message.success('已添加到购物车');
  };

  // 获取作者显示文本
  const getAuthors = () => {
    if (!book) return '';
    const authors = [book.author1, book.author2, book.author3, book.author4].filter(Boolean);
    return authors.join(', ') || '未知';
  };

  if (loading) {
    return (
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <Loading text="加载中..." />
      </div>
    );
  }

  if (!book) {
    return (
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="text-center py-12">
          <p className="text-gray-500 mb-4">图书不存在</p>
          <Link to="/books" className="text-blue-500 hover:underline">
            返回图书列表
          </Link>
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      {/* 面包屑 */}
      <nav className="text-sm text-gray-500 mb-6">
        <Link to="/" className="hover:text-blue-500">首页</Link>
        <span className="mx-2">/</span>
        <Link to="/books" className="hover:text-blue-500">图书列表</Link>
        <span className="mx-2">/</span>
        <span className="text-gray-900">{book.title}</span>
      </nav>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
        {/* 图书封面 */}
        <div className="aspect-[3/4] bg-gray-100 rounded-lg overflow-hidden">
          {book.coverImage ? (
            <img
              src={book.coverImage}
              alt={book.title}
              className="w-full h-full object-cover"
            />
          ) : (
            <div className="w-full h-full flex items-center justify-center text-gray-400">
              <svg
                className="w-32 h-32"
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
        </div>

        {/* 图书信息 */}
        <div>
          <h1 className="text-2xl font-bold text-gray-900 mb-4">{book.title}</h1>

          <div className="space-y-3 text-gray-600 mb-6">
            <p><span className="font-medium">作者：</span>{getAuthors()}</p>
            <p><span className="font-medium">出版社：</span>{book.publisher || '未知'}</p>
            {book.keywords && (
              <p><span className="font-medium">关键词：</span>{book.keywords}</p>
            )}
            <p>
              <span className="font-medium">库存：</span>
              {book.stockQuantity > 0 ? (
                <span className="text-green-600">{book.stockQuantity} 本</span>
              ) : (
                <span className="text-red-500">缺货</span>
              )}
            </p>
          </div>

          {/* 价格 */}
          <div className="flex items-baseline gap-2 mb-6">
            <span className="text-3xl font-bold text-red-500">
              ¥{book.price.toFixed(2)}
            </span>
          </div>

          {/* 数量选择 */}
          {book.stockQuantity > 0 && (
            <div className="flex items-center gap-4 mb-6">
              <span className="text-gray-600">数量：</span>
              <div className="flex items-center border border-gray-300 rounded">
                <button
                  onClick={() => setQuantity(Math.max(1, quantity - 1))}
                  className="px-3 py-1 hover:bg-gray-100"
                >
                  -
                </button>
                <input
                  type="number"
                  value={quantity}
                  onChange={(e) => setQuantity(Math.max(1, Math.min(book.stockQuantity, parseInt(e.target.value) || 1)))}
                  className="w-16 text-center border-x border-gray-300 py-1"
                />
                <button
                  onClick={() => setQuantity(Math.min(book.stockQuantity, quantity + 1))}
                  className="px-3 py-1 hover:bg-gray-100"
                >
                  +
                </button>
              </div>
              <span className="text-gray-500 text-sm">
                （库存 {book.stockQuantity} 本）
              </span>
            </div>
          )}

          {/* 操作按钮 */}
          <div className="flex gap-4">
            <Button
              onClick={handleAddToCart}
              disabled={book.stockQuantity <= 0}
              size="lg"
            >
              加入购物车
            </Button>
            <Link to="/cart">
              <Button variant="secondary" size="lg">
                去结算
              </Button>
            </Link>
          </div>

          {/* 图书目录 */}
          {book.tableOfContents && (
            <Card className="mt-8">
              <CardBody>
                <h3 className="font-bold text-gray-900 mb-4">目录</h3>
                <div className="text-gray-600 whitespace-pre-line">
                  {book.tableOfContents}
                </div>
              </CardBody>
            </Card>
          )}
        </div>
      </div>
    </div>
  );
}
