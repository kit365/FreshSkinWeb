import { AUTH_ENDPOINT } from '../constants/apiConstants';
import { AuthenticationRequest, LoginResponse } from '../types/auth';
import { ApiResponse } from '../types/api';
import { UserResponseDTO } from '../types/user';
import { apiClient } from './api';


export const authService = {
 
  login: async (credentials: AuthenticationRequest): Promise<ApiResponse<LoginResponse>> => {
    try {
      const response = await apiClient.post(`${AUTH_ENDPOINT}/login`, credentials);
      return response.data;
    } catch (error: any) {
      throw new Error(error.response?.data?.message || 'Đăng nhập thất bại');
    }
  },

  // Lấy thông tin profile hiện tại
  getMyProfile: async (): Promise<ApiResponse<UserResponseDTO>> => {
    try {
      const response = await apiClient.get(`${AUTH_ENDPOINT}/my-profile`);
      return response.data;
    } catch (error: any) {
      throw new Error(error.response?.data?.message || 'Không thể lấy thông tin người dùng');
    }
  },

  // Đăng ký
  register: async (userData: AuthenticationRequest & { email?: string }): Promise<ApiResponse<UserResponseDTO>> => {
    try {
      const response = await apiClient.post(`${AUTH_ENDPOINT}/register`, userData);
      return response.data;
    } catch (error: any) {
      throw new Error(error.response?.data?.message || 'Đăng ký thất bại');
    }
  },

  // Đăng xuất
  logout: async (): Promise<ApiResponse<null>> => {
    try {
      const response = await apiClient.post(`${AUTH_ENDPOINT}/logout`);
      return response.data;
    } catch (error: any) {
      throw new Error(error.response?.data?.message || 'Đăng xuất thất bại');
    }
  },

  // Refresh token
  refreshToken: async (): Promise<ApiResponse<LoginResponse>> => {
    try {
      const response = await apiClient.post(`${AUTH_ENDPOINT}/refresh`);
      return response.data;
    } catch (error: any) {
      throw new Error(error.response?.data?.message || 'Làm mới token thất bại');
    }
  }

};

export default authService;