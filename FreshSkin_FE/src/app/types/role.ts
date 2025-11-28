export interface RoleResponseDTO {
  roleId: number;
  title: string;
  description: string;
  permission: string[];
  deleted: boolean;
  status: string;
  createdAt: string; // Using string for Date in frontend
  updatedAt: string;
}