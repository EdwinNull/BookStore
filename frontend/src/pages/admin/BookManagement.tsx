/**
 * 管理员 - 图书管理页面
 */
import { useState, useEffect } from 'react';
import { Card, CardBody, Button, Input, Pagination, Loading, Empty } from '@/components/common';
import { message } from '@/components/common/Message';
import { getBookList, addBook, updateBook, deleteBook } from '@/api/book';
import type { Book, PageBean } from '@/types';

export function BookManagement() {
  const [books, setBooks] = useState<PageBean<Book> | null>(null);
  const [loading, setLoading] = useState(true);
  const [pageNum, setPageNum] = useState(1);
  const pageSize = 10;

  // 编辑弹窗
  const [showModal, setShowModal] = useState(false);
  const [editingBook, setEditingBook] = useState<Partial<Book>>({});
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    fetchBooks();
  }, [pageNum]);

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

  const handleAdd = () => {
    setEditingBook({});
    setShowModal(true);
  };

  const handleEdit = (book: Book) => {
    setEditingBook(book);
    setShowModal(true);
  };

  const handleDelete = async (bookId: number) => {
    if (!confirm('确定要删除这本图书吗？')) return;

    try {
      await deleteBook(bookId);
      message.success('删除成功');
      fetchBooks();
    } catch (error) {
      message.error(error instanceof Error ? error.message : '删除失败');
    }
  };

  const handleSave = async () => {
    if (!editingBook.title || !editingBook.author1 || !editingBook.price) {
      message.warning('请填写必填字段');
      return;
    }

    setSaving(true);
    try {
      if (editingBook.bookId) {
        await updateBook(editingBook as Book);
        message.success('更新成功');
      } else {
        await addBook(editingBook);
        message.success('添加成功');
      }
      setShowModal(false);
      fetchBooks();
    } catch (error) {
      message.error(error instanceof Error ? error.message : '保存失败');
    } finally {
      setSaving(false);
    }
  };

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold text-gray-900">图书管理</h1>
        <Button onClick={handleAdd}>添加图书</Button>
      </div>

      <Card>
        <CardBody className="p-0">
          {loading ? (
            <Loading />
          ) : books?.items?.length ? (
            <>
              <table className="w-full">
                <thead className="bg-gray-50">
                  <tr>
                    <th className="px-4 py-3 text-left text-sm font-medium text-gray-500">ID</th>
                    <th className="px-4 py-3 text-left text-sm font-medium text-gray-500">书名</th>
                    <th className="px-4 py-3 text-left text-sm font-medium text-gray-500">作者</th>
                    <th className="px-4 py-3 text-left text-sm font-medium text-gray-500">出版社</th>
                    <th className="px-4 py-3 text-left text-sm font-medium text-gray-500">价格</th>
                    <th className="px-4 py-3 text-left text-sm font-medium text-gray-500">库存</th>
                    <th className="px-4 py-3 text-left text-sm font-medium text-gray-500">操作</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-200">
                  {books.items.map((book) => (
                    <tr key={book.bookId} className="hover:bg-gray-50">
                      <td className="px-4 py-3 text-sm">{book.bookId}</td>
                      <td className="px-4 py-3 text-sm font-medium">{book.title}</td>
                      <td className="px-4 py-3 text-sm text-gray-500">{book.author1}</td>
                      <td className="px-4 py-3 text-sm text-gray-500">{book.publisher}</td>
                      <td className="px-4 py-3 text-sm text-red-500">¥{book.price.toFixed(2)}</td>
                      <td className="px-4 py-3 text-sm">
                        <span className={book.stockQuantity <= 0 ? 'text-red-500' : ''}>
                          {book.stockQuantity}
                        </span>
                      </td>
                      <td className="px-4 py-3 text-sm">
                        <Button variant="ghost" size="sm" onClick={() => handleEdit(book)}>
                          编辑
                        </Button>
                        <Button variant="danger" size="sm" onClick={() => handleDelete(book.bookId)}>
                          删除
                        </Button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>

              <div className="p-4 border-t">
                <Pagination
                  current={pageNum}
                  total={books.total}
                  pageSize={pageSize}
                  onChange={setPageNum}
                />
              </div>
            </>
          ) : (
            <Empty text="暂无图书" />
          )}
        </CardBody>
      </Card>

      {/* 编辑弹窗 */}
      {showModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <Card className="w-full max-w-2xl max-h-[80vh] overflow-auto">
            <CardBody>
              <div className="flex items-center justify-between mb-4">
                <h2 className="text-lg font-bold">
                  {editingBook.bookId ? '编辑图书' : '添加图书'}
                </h2>
                <button
                  onClick={() => setShowModal(false)}
                  className="text-gray-400 hover:text-gray-600"
                >
                  ✕
                </button>
              </div>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <Input
                  label="书名 *"
                  value={editingBook.title || ''}
                  onChange={(e) => setEditingBook({ ...editingBook, title: e.target.value })}
                />
                <Input
                  label="作者1 *"
                  value={editingBook.author1 || ''}
                  onChange={(e) => setEditingBook({ ...editingBook, author1: e.target.value })}
                />
                <Input
                  label="作者2"
                  value={editingBook.author2 || ''}
                  onChange={(e) => setEditingBook({ ...editingBook, author2: e.target.value })}
                />
                <Input
                  label="出版社"
                  value={editingBook.publisher || ''}
                  onChange={(e) => setEditingBook({ ...editingBook, publisher: e.target.value })}
                />
                <Input
                  label="价格 *"
                  type="number"
                  value={editingBook.price || ''}
                  onChange={(e) => setEditingBook({ ...editingBook, price: parseFloat(e.target.value) })}
                />
                <Input
                  label="库存"
                  type="number"
                  value={editingBook.stockQuantity || 0}
                  onChange={(e) => setEditingBook({ ...editingBook, stockQuantity: parseInt(e.target.value) })}
                />
                <Input
                  label="关键词"
                  value={editingBook.keywords || ''}
                  onChange={(e) => setEditingBook({ ...editingBook, keywords: e.target.value })}
                />
                <Input
                  label="封面图片URL"
                  value={editingBook.coverImage || ''}
                  onChange={(e) => setEditingBook({ ...editingBook, coverImage: e.target.value })}
                />
              </div>

              <div className="mt-6 flex justify-end gap-2">
                <Button variant="secondary" onClick={() => setShowModal(false)}>
                  取消
                </Button>
                <Button onClick={handleSave} loading={saving}>
                  保存
                </Button>
              </div>
            </CardBody>
          </Card>
        </div>
      )}
    </div>
  );
}
