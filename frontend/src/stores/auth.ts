/**
 * 用户认证状态管理
 * 使用 zustand 管理用户登录状态、token 等
 */
import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import type { User } from '@/types';

interface AuthState {
  token: string | null;
  user: User | null;
  isAdmin: boolean;

  // Actions
  setAuth: (token: string, user: User) => void;
  setUser: (user: User) => void;
  logout: () => void;
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      token: null,
      user: null,
      isAdmin: false,

      setAuth: (token, user) => {
        localStorage.setItem('token', token);
        set({
          token,
          user,
          isAdmin: user.role === 'admin',
        });
      },

      setUser: (user) => {
        set({
          user,
          isAdmin: user.role === 'admin',
        });
      },

      logout: () => {
        localStorage.removeItem('token');
        set({
          token: null,
          user: null,
          isAdmin: false,
        });
      },
    }),
    {
      name: 'auth-storage',
      partialize: (state) => ({
        token: state.token,
        user: state.user,
        isAdmin: state.isAdmin,
      }),
    }
  )
);
