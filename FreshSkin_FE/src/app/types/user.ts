import { RoleResponseDTO } from './role';

export interface OrderEntity {
  id: number;
  // Add other order properties as needed
}

export interface ProductComparisonResponseDTO {
  id: number;
  products: any[];
}

export interface UserDiscountUsageResponse {
  id: number;
  // Add other properties as needed
}

export interface UserResponseDTO {
  userID: number;
  role: RoleResponseDTO;
  username?: string;
  firstName?: string;
  lastName?: string;
  orders?: OrderEntity[];
  email?: string;
  phone?: string;
  avatar: string[];
  token: string;
  address?: string;
  status?: string;
  typeUser?: string;
  skinType?: string;
  provider?: string;
  providerId?: string;
  deleted?: boolean;
  createdAt?: string; // Using string for Date in frontend
  updatedAt?: string;
  productComparisonId?: ProductComparisonResponseDTO;
  userDiscountUsageResponses?: UserDiscountUsageResponse[];
}