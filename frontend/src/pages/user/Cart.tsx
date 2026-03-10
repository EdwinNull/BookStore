/**
 * 购物车页面
 */
import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Card, CardBody, CardFooter, Button, Empty } from '@/components/common';
import { message } from '@/components/common/Message';
import { useCartStore, useAuthStore } from '@/stores';
import { createOrder } from '@/api/order';

export function CartPage() {
  const navigate = useNavigate();
  const { user } = useAuthStore();
  const { items, updateQuantity, removeItem, clearCart, getTotalPrice } = useCartStore();
  const [loading, setLoading] = useState(false);

  const totalPrice = getTotalPrice();

  // 修改数量
  const handleQuantityChange = (bookId: number, quantity: number) => {
    updateQuantity(bookId, quantity);
  };

  // 删除商品
  const handleRemove = (bookId: number) => {
    removeItem(bookId);
    message.success('已移除');
  };

  // 结算
  const handleCheckout = async () => {
    if (!user) {
      message.warning('请先登录');
      navigate('/login');
      return;
    }

    if (items.length === 0) {
      message.warning('购物车为空');
      return;
    }

    setLoading(true);
    try {
      await createOrder({
        userId: user.userId,
        items: items.map((item) => ({
          bookId: item.bookId,
          quantity: item.quantity,
          price: item.book.price,
        })),
      });
      message.success('下单成功');
      clearCart();
      navigate('/user/orders');
    } catch (error) {
      message.error(error instanceof Error ? error.message : '下单失败');
    } finally {
      setLoading(false);
    }
  };

  if (items.length === 0) {
    return (
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <h1 className="text-2xl font-bold text-gray-900 mb-6">购物车</h1>
        <Empty
          text="购物车是空的"
          action={
            <Link to="/books">
              <Button>去逛逛</Button>
            </Link>
          }
        />
      </div>
    );
  }

  return (
    <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold text-gray-900">购物车</h1>
        <button
          onClick={() => clearCart()}
          className="text-red-500 hover:text-red-600 text-sm"
        >
          清空购物车
        </button>
      </div>

      {/* 购物车列表 */}
      <Card className="mb-6">
        <CardBody className="p-0">
          <div className="divide-y divide-gray-200">
            {items.map((item) => (
              <div key={item.bookId} className="flex items-center gap-4 p-4">
                {/* 图书封面 */}
                <Link to={`/books/${item.bookId}`} className="flex-shrink-0">
                  <div className="w-20 h-28 bg-gray-100 rounded overflow-hidden">
                    {item.book.coverImage ? (
                      <img
                        src={item.book.coverImage}
                        alt={item.book.title}
                        className="w-full h-full object-cover"
                      />
                    ) : (
                      <div className="w-full h-full flex items-center justify-center text-gray-400">
                        <svg
                          className="w-8 h-8"
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
                </Link>

                {/* 图书信息 */}
                <div className="flex-1 min-w-0">
                  <Link
                    to={`/books/${item.bookId}`}
                    className="font-medium text-gray-900 hover:text-blue-500 line-clamp-2"
                  >
                    {item.book.title}
                  </Link>
                  <p className="text-sm text-gray-500 mt-1">{item.book.author1}</p>
                  <p className="text-red-500 font-medium mt-1">
                    ¥{item.book.price.toFixed(2)}
                  </p>
                </div>

                {/* 数量控制 */}
                <div className="flex items-center border border-gray-300 rounded">
                  <button
                    onClick={() => handleQuantityChange(item.bookId, item.quantity - 1)}
                    className="px-3 py-1 hover:bg-gray-100"
                  >
                    -
                  </button>
                  <input
                    type="number"
                    value={item.quantity}
                    onChange={(e) => handleQuantityChange(item.bookId, parseInt(e.target.value) || 1)}
                    className="w-12 text-center border-x border-gray-300 py-1"
                  />
                  <button
                    onClick={() => handleQuantityChange(item.bookId, item.quantity + 1)}
                    className="px-3 py-1 hover:bg-gray-100"
                  >
                    +
                  </button>
                </div>

                {/* 小计 */}
                <div className="text-right min-w-[80px]">
                  <p className="font-medium text-gray-900">
                    ¥{(item.book.price * item.quantity).toFixed(2)}
                  </p>
                </div>

                {/* 删除按钮 */}
                <button
                  onClick={() => handleRemove(item.bookId)}
                  className="text-gray-400 hover:text-red-500"
                >
                  <svg
                    className="w-5 h-5"
                    fill="none"
                    viewBox="0 0 24 24"
                    stroke="currentColor"
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth={2}
                      d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"
                    />
                  </svg>
                </button>
              </div>
            ))}
          </div>
        </CardBody>

        <CardFooter className="flex items-center justify-between">
          <div>
            <span className="text-gray-600">共 {items.length} 种图书，</span>
            <span className="text-gray-600">
              {items.reduce((sum, item) => sum + item.quantity, 0)} 件
            </span>
          </div>
          <div className="flex items-center gap-4">
            <div>
              <span className="text-gray-600">合计：</span>
              <span className="text-2xl font-bold text-red-500">
                ¥{totalPrice.toFixed(2)}
              </span>
            </div>
            <Button onClick={handleCheckout} loading={loading}>
              结算
            </Button>
          </div>
        </CardFooter>
      </Card>

      {/* 继续购物 */}
      <Link to="/books" className="text-blue-500 hover:underline">
        ← 继续购物
      </Link>
    </div>
  );
}
