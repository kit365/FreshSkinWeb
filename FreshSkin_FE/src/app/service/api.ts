import axios from 'axios';
import { API_BASE_URL } from '../constants/apiConstants';
import { useAuthStore } from '../hooks/useAuth';


export const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json'
  },
  withCredentials: true,
  validateStatus: (status) => status < 500 
});


apiClient.interceptors.request.use(
  (config) => {
    // Đọc token từ Zustand store thay vì localStorage
    const token = useAuthStore.getState().accessToken;

    // Attach authorization token if available

    if (token) {
      config.headers = config.headers ?? {};
      config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
  },
  (error) => {
    console.error("❌ Lỗi trong request interceptor:", error);
    return Promise.reject(error);
  }
);

// Response interceptor: Auto-refresh token on 401
let isRefreshing = false;
let failedQueue: any[] = [];

const processQueue = (error: any, token: string | null = null) => {
  failedQueue.forEach((prom) => {
    if (error) {
      prom.reject(error);
    } else {
      prom.resolve(token);
    }
  });
  
  failedQueue = [];
};

apiClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    // If error is 401 and we haven't tried to refresh yet
    if (error.response?.status === 401 && !originalRequest._retry) {
      if (isRefreshing) {
        // Wait for the ongoing refresh to complete
        return new Promise((resolve, reject) => {
          failedQueue.push({ resolve, reject });
        }).then((token) => {
          originalRequest.headers['Authorization'] = 'Bearer ' + token;
          return apiClient(originalRequest);
        }).catch((err) => {
          return Promise.reject(err);
        });
      }

      originalRequest._retry = true;
      isRefreshing = true;

      const authState = useAuthStore.getState();
      const refreshToken = authState.refreshToken;
      
      if (!refreshToken) {
        // No refresh token available, logout
        authState.logout();
        window.location.href = '/user/login';
        return Promise.reject(error);
      }

      try {
        // Call refresh token endpoint
        const response = await axios.post(`${API_BASE_URL}/auth/refresh`, {
          token: refreshToken
        });

        if (response.data?.success && response.data.data) {
          const { token: newAccessToken, refreshToken: newRefreshToken } = response.data.data;
          
          // Cập nhật tokens vào Zustand store
          useAuthStore.setState({
            accessToken: newAccessToken,
            refreshToken: newRefreshToken,
            isAuthenticated: true
          });
          
          // Update authorization header
          apiClient.defaults.headers.common['Authorization'] = 'Bearer ' + newAccessToken;
          originalRequest.headers['Authorization'] = 'Bearer ' + newAccessToken;
          
          processQueue(null, newAccessToken);
          isRefreshing = false;
          
          // Retry original request with new token
          return apiClient(originalRequest);
        } else {
          throw new Error('Token refresh failed');
        }
      } catch (refreshError) {
        processQueue(refreshError, null);
        isRefreshing = false;
        
        // Refresh failed, logout
        useAuthStore.getState().logout();
        window.location.href = '/user/login';
        
        return Promise.reject(refreshError);
      }
    }

    return Promise.reject(error);
  }
);





