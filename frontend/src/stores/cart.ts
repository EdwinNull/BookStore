/**
 * 购物车状态管理
 */
import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import type { Book, CartItem } from '@/types';

interface CartState {
  items: CartItem[];

  // Actions
  addItem: (book: Book, quantity?: number) => void;
  removeItem: (bookId: number) => void;
  updateQuantity: (bookId: number, quantity: number) => void;
  clearCart: () => void;

  // Computed
  getTotalPrice: () => number;
  getTotalCount: () => number;
}

export const useCartStore = create<CartState>()(
  persist(
    (set, get) => ({
      items: [],

      addItem: (book, quantity = 1) => {
        set((state) => {
          const existingItem = state.items.find((item) => item.bookId === book.bookId);

          if (existingItem) {
            // 已存在，更新数量
            return {
              items: state.items.map((item) =>
                item.bookId === book.bookId
                  ? { ...item, quantity: item.quantity + quantity }
                  : item
              ),
            };
          }

          // 不存在，添加新项
          return {
            items: [...state.items, { bookId: book.bookId, book, quantity }],
          };
        });
      },

      removeItem: (bookId) => {
        set((state) => ({
          items: state.items.filter((item) => item.bookId !== bookId),
        }));
      },

      updateQuantity: (bookId, quantity) => {
        if (quantity <= 0) {
          get().removeItem(bookId);
          return;
        }

        set((state) => ({
          items: state.items.map((item) =>
            item.bookId === bookId ? { ...item, quantity } : item
          ),
        }));
      },

      clearCart: () => {
        set({ items: [] });
      },

      getTotalPrice: () => {
        return get().items.reduce(
          (total, item) => total + item.book.price * item.quantity,
          0
        );
      },

      getTotalCount: () => {
        return get().items.reduce((count, item) => count + item.quantity, 0);
      },
    }),
    {
      name: 'cart-storage',
    }
  )
);
