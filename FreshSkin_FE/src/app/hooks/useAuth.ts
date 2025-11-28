import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import { useState, useCallback, useMemo } from 'react';
import { authService } from '../service/authService';
import { AuthenticationRequest } from '../types/auth';
import { UserResponseDTO } from '../types/user';

// Zustand store chỉ cho auth tokens
interface AuthTokenState {
  accessToken: string | null;
  refreshToken: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  error: string | null;
  
  login: (credentials: AuthenticationRequest) => Promise<void>;
  logout: () => void;
  clearError: () => void;
  register: (userData: AuthenticationRequest & { email?: string }) => Promise<void>;
}

export const useAuthStore = create<AuthTokenState>()(
  persist(
    (set) => ({
      accessToken: null,
      refreshToken: null,
      isAuthenticated: false,
      isLoading: false,
      error: null,

      login: async (credentials: AuthenticationRequest) => {
        try {
          set({ isLoading: true, error: null });
          
          const response = await authService.login(credentials);
          
          if (response.code === 200 && response.data) {
            const { accessToken, refreshToken } = response.data;
            
            set({
              accessToken,
              refreshToken,
              isAuthenticated: true,
              isLoading: false,
              error: null
            });
          } else {
            throw new Error(response.message || 'Đăng nhập thất bại');
          }
        } catch (error: any) {
          set({
            isLoading: false,
            error: error.message || 'Đăng nhập thất bại',
            isAuthenticated: false,
            accessToken: null,
            refreshToken: null
          });
          throw error;
        }
      },

      logout: () => {
        set({
          accessToken: null,
          refreshToken: null,
          isAuthenticated: false,
          error: null
        });
      },

      clearError: () => {
        set({ error: null });
      },

      register: async (userData: AuthenticationRequest & { email?: string }) => {
        try {
          set({ isLoading: true, error: null });
          
          const response = await authService.register(userData);
          
          if (response.code === 200) {
            set({ isLoading: false, error: null });
          } else {
            throw new Error(response.message || 'Đăng ký thất bại');
          }
        } catch (error: any) {
          set({
            isLoading: false,
            error: error.message || 'Đăng ký thất bại'
          });
          throw error;
        }
      }
    }),
    {
      name: 'auth-storage'
    }
  )
);

// Hook chính - tất cả trong một
export const useAuth = () => {
  // Selective subscriptions để tránh re-render không cần thiết
  const { login, logout: authLogout, clearError, register } = useAuthStore();
  const isAuthenticated = useAuthStore((state) => state.isAuthenticated);
  const isLoading = useAuthStore((state) => state.isLoading);
  const error = useAuthStore((state) => state.error);
  const accessToken = useAuthStore((state) => state.accessToken);
  const refreshToken = useAuthStore((state) => state.refreshToken);
  
  // User data dùng useState
  const [user, setUser] = useState<UserResponseDTO | null>(null);
  const [userLoading, setUserLoading] = useState(false);
  const [userError, setUserError] = useState<string | null>(null);

  const fetchProfile = useCallback(async () => {
    try {
      setUserLoading(true);
      setUserError(null);
      const response = await authService.getMyProfile();
      if (response.code === 200 && response.data) {
        setUser(response.data);
      }
    } catch (error: any) {
      setUserError(error.message || 'Không thể lấy thông tin người dùng');
    } finally {
      setUserLoading(false);
    }
  }, []);

  const clearUser = useCallback(() => {
    setUser(null);
    setUserError(null);
  }, []);

  const logoutWithClearUser = useCallback(() => {
    authLogout();
    clearUser();
  }, [authLogout, clearUser]);

  return useMemo(() => ({
    // Auth state từ Zustand
    isAuthenticated,
    isLoading,
    error,
    accessToken,
    refreshToken,
    login,
    logout: logoutWithClearUser,
    clearError,
    register,
    
    // User state từ useState
    user,
    userLoading,
    userError,
    fetchProfile,
    setUser,
    clearUser
  }), [
    isAuthenticated,
    isLoading,
    error,
    accessToken,
    refreshToken,
    login,
    logoutWithClearUser,
    clearError,
    register,
    user,
    userLoading,
    userError,
    fetchProfile,
    clearUser
  ]);
};