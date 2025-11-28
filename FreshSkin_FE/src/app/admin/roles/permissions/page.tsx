"use client";

import { Box, Button, Paper, Typography } from "@mui/material";
import { useEffect, useState, ChangeEvent, useContext } from "react";
import { ProfileAdminContext } from "../../layout";
// Định nghĩa kiểu cho PermissionItem
interface PermissionItem {
  dataName: string;
  dataContent: string;
}

// Định nghĩa kiểu cho Role
interface Role {
  roleId: string;
  title: string;
  permission: string[];
}

interface PermissionProps {
  permissions: PermissionItem[];
  roles: Role[];
  onCheckboxChange: (
    roleId: string,
    permissionName: string,
    checked: boolean
  ) => void;
}

function Permission({ permissions, roles, onCheckboxChange }: PermissionProps) {
  return (
    <>
      {permissions.map((item, index) => (
        <tr key={index} data-name={item.dataName}>
          <td style={{ padding: "12px" }}>{item.dataContent}</td>
          {roles.map((role) => {
            // Xác định xem role đã có permission này hay chưa
            const isChecked = role.permission.includes(item.dataName);
            return (
              <td key={role.roleId} style={{ textAlign: "center" }}>
                <input
                  type="checkbox"
                  data-id={role.roleId}
                  checked={isChecked}
                  onChange={(e: ChangeEvent<HTMLInputElement>) =>
                    onCheckboxChange(
                      role.roleId,
                      item.dataName,
                      e.target.checked
                    )
                  }
                />
              </td>
            );
          })}
        </tr>
      ))}
    </>
  );
}

export default function PermissionPage() {
  const dataProfile = useContext(ProfileAdminContext);
  const permissions = dataProfile?.permissions;
  const [listRoles, setListRoles] = useState<Role[]>([]);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [updatedRoles, setUpdatedRoles] = useState<Record<string, string[]>>(
    {}
  );

  useEffect(() => {
    const fetchRoles = async () => {
      try {
        const response = await fetch(
          "https://freshskinweb.onrender.com/admin/roles"
        );
        const data = await response.json();
        const roles: Role[] = data.data;
        setListRoles(roles);

        // Khởi tạo state updatedRoles từ dữ liệu nhận được
        const initialState: Record<string, string[]> = {};
        roles.forEach((role) => {
          initialState[role.roleId] = role.permission || [];
        });
        setUpdatedRoles(initialState);
        setIsLoading(false);
      } catch (error) {
        console.error("Error fetching roles:", error);
        setIsLoading(false);
      }
    };

    fetchRoles();
  }, []);

  // Hàm xử lý khi checkbox thay đổi
  const handleCheckboxChange = (
    roleId: string,
    permissionName: string,
    checked: boolean
  ) => {
    setUpdatedRoles((prev) => {
      const rolePermissions = prev[roleId] || [];
      let newPermissions: string[];
      if (checked) {
        // Nếu chưa có thì thêm vào
        newPermissions = rolePermissions.includes(permissionName)
          ? rolePermissions
          : [...rolePermissions, permissionName];
      } else {
        // Nếu bỏ check thì loại bỏ permission đó
        newPermissions = rolePermissions.filter(
          (name) => name !== permissionName
        );
      }
      return { ...prev, [roleId]: newPermissions };
    });
  };

  if (isLoading) {
    return <div>Loading...</div>;
  }

  // Hàm gửi dữ liệu cập nhật permission lên server
  const handleClick = async () => {
    // Chuẩn bị dữ liệu dạng mảng: [{ roleId, permission: [...] }, ...]
    const dataFinal = Object.entries(updatedRoles).map(
      ([roleId, permission]) => ({
        roleId,
        permission,
      })
    );

    try {
      const response = await fetch(
        "https://freshskinweb.onrender.com/admin/roles/add-permission",
        {
          method: "PATCH",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(dataFinal),
        }
      );
      const dataResponse = await response.json();
      if (dataResponse.code === 200) {
        // Reload trang hoặc thông báo thành công
        location.reload();
      }
    } catch (error) {
      console.error("Error updating permissions:", error);
    }
  };

  return (
    <Box p={3}>
      <Typography variant="h5" gutterBottom sx={{ mb: 4 }}>
        Quản lý phân quyền
      </Typography>

      {permissions?.includes("roles_permissions") && (
        <Paper sx={{ backgroundColor: "white", p: 2 }}>
          <Box display="flex" justifyContent="flex-end" gap={2} mb={3}>
            <Button
              variant="contained"
              color="primary"
              sx={{ minWidth: "150px", boxShadow: 2 }}
              onClick={handleClick}
            >
              Cập nhật quyền
            </Button>
          </Box>

          <table
            style={{
              width: "100%",
              tableLayout: "fixed",
              borderSpacing: "0 10px",
              borderCollapse: "separate",
            }}
          >
            <thead>
              <tr>
                <th
                  style={{
                    fontWeight: "bold",
                    textAlign: "left",
                    padding: "12px",
                    backgroundColor: "#f5f5f5",
                  
              
                  }}
                >
                  Tính năng
                </th>
                {listRoles.map((role) => (
                  <th
                    key={role.roleId}
                    role-id={role.roleId}
                    style={{
                      fontWeight: "bold",
                      textAlign: "center",
                      padding: "12px",
                      backgroundColor: "#f5f5f5",
                    }}
                  >
                    {role.title}
                  </th>
                ))}
              </tr>
            </thead>
            <tbody>
              {/* Tổng quan */}
              <tr>
                <td
                  style={{
                    fontWeight: "bold",
                    padding: "12px",
                    backgroundColor: "#e3f2fd",
                  }}
                  colSpan={listRoles.length + 1}
                >
                  Tổng quan
                </td>
              </tr>
              <Permission
                roles={listRoles.map((role) => ({
                  ...role,
                  permission: updatedRoles[role.roleId] || [],
                }))}
                permissions={[
                  { dataName: "dashboard_view", dataContent: "Xem" },
                ]}
                onCheckboxChange={handleCheckboxChange}
              />

              {/* Danh mục sản phẩm */}
              <tr>
                <td
                  style={{
                    fontWeight: "bold",
                    padding: "12px",
                    backgroundColor: "#e1f5fe",
                  }}
                  colSpan={listRoles.length + 1}
                >
                  Danh mục sản phẩm
                </td>
              </tr>
              <Permission
                roles={listRoles.map((role) => ({
                  ...role,
                  permission: updatedRoles[role.roleId] || [],
                }))}
                permissions={[
                  { dataName: "products-category_view", dataContent: "Xem" },
                  {
                    dataName: "products-category_create",
                    dataContent: "Thêm mới",
                  },
                  {
                    dataName: "products-category_edit",
                    dataContent: "Chỉnh sửa",
                  },
                  { dataName: "products-category_delete", dataContent: "Xóa" },
                ]}
                onCheckboxChange={handleCheckboxChange}
              />

              {/* Sản phẩm */}
              <tr>
                <td
                  style={{
                    fontWeight: "bold",
                    padding: "12px",
                    backgroundColor: "#e1f5fe",
                  }}
                  colSpan={listRoles.length + 1}
                >
                  Sản phẩm
                </td>
              </tr>
              <Permission
                roles={listRoles.map((role) => ({
                  ...role,
                  permission: updatedRoles[role.roleId] || [],
                }))}
                permissions={[
                  { dataName: "products_view", dataContent: "Xem" },
                  { dataName: "products_create", dataContent: "Thêm mới" },
                  { dataName: "products_edit", dataContent: "Chỉnh sửa" },
                  { dataName: "products_delete", dataContent: "Xóa" },
                ]}
                onCheckboxChange={handleCheckboxChange}
              />

              {/* Thương hiệu */}
              <tr>
                <td
                  style={{
                    fontWeight: "bold",
                    padding: "12px",
                    backgroundColor: "#e1f5fe",
                  }}
                  colSpan={listRoles.length + 1}
                >
                  Thương hiệu
                </td>
              </tr>
              <Permission
                roles={listRoles.map((role) => ({
                  ...role,
                  permission: updatedRoles[role.roleId] || [],
                }))}
                permissions={[
                  { dataName: "brands_view", dataContent: "Xem" },
                  { dataName: "brands_create", dataContent: "Thêm mới" },
                  { dataName: "brands_edit", dataContent: "Chỉnh sửa" },
                  { dataName: "brands_delete", dataContent: "Xóa" },
                ]}
                onCheckboxChange={handleCheckboxChange}
              />

              {/* Thể loại da */}
              <tr>
                <td
                  style={{
                    fontWeight: "bold",
                    padding: "12px",
                    backgroundColor: "#e1f5fe",
                  }}
                  colSpan={listRoles.length + 1}
                >
                  Thể loại da
                </td>
              </tr>
              <Permission
                roles={listRoles.map((role) => ({
                  ...role,
                  permission: updatedRoles[role.roleId] || [],
                }))}
                permissions={[
                  { dataName: "skin_view", dataContent: "Xem" },
                  { dataName: "skin_create", dataContent: "Thêm mới" },
                  { dataName: "skin_edit", dataContent: "Chỉnh sửa" },
                  { dataName: "skin_delete", dataContent: "Xóa" },
                ]}
                onCheckboxChange={handleCheckboxChange}
              />

              {/* Mã khuyến mãi */}
              <tr>
                <td
                  style={{
                    fontWeight: "bold",
                    padding: "12px",
                    backgroundColor: "#e1f5fe",
                  }}
                  colSpan={listRoles.length + 1}
                >
                  Mã khuyến mãi
                </td>
              </tr>
              <Permission
                roles={listRoles.map((role) => ({
                  ...role,
                  permission: updatedRoles[role.roleId] || [],
                }))}
                permissions={[
                  { dataName: "vouchers_view", dataContent: "Xem" },
                  { dataName: "vouchers_create", dataContent: "Thêm mới" },
                  { dataName: "vouchers_edit", dataContent: "Chỉnh sửa" },
                  { dataName: "vouchers_delete", dataContent: "Xóa" },
                ]}
                onCheckboxChange={handleCheckboxChange}
              />

              {/* Danh mục bài viết */}
              <tr>
                <td
                  style={{
                    fontWeight: "bold",
                    padding: "12px",
                    backgroundColor: "#e1f5fe",
                  }}
                  colSpan={listRoles.length + 1}
                >
                  Danh mục bài viết
                </td>
              </tr>
              <Permission
                roles={listRoles.map((role) => ({
                  ...role,
                  permission: updatedRoles[role.roleId] || [],
                }))}
                permissions={[
                  { dataName: "blogs-category_view", dataContent: "Xem" },
                  {
                    dataName: "blogs-category_create",
                    dataContent: "Thêm mới",
                  },
                  { dataName: "blogs-category_edit", dataContent: "Chỉnh sửa" },
                  { dataName: "blogs-category_delete", dataContent: "Xóa" },
                ]}
                onCheckboxChange={handleCheckboxChange}
              />

              {/* Bài viết */}
              <tr>
                <td
                  style={{
                    fontWeight: "bold",
                    padding: "12px",
                    backgroundColor: "#e1f5fe",
                  }}
                  colSpan={listRoles.length + 1}
                >
                  Bài viết
                </td>
              </tr>
              <Permission
                roles={listRoles.map((role) => ({
                  ...role,
                  permission: updatedRoles[role.roleId] || [],
                }))}
                permissions={[
                  { dataName: "blogs_view", dataContent: "Xem" },
                  { dataName: "blogs_create", dataContent: "Thêm mới" },
                  { dataName: "blogs_edit", dataContent: "Chỉnh sửa" },
                  { dataName: "blogs_delete", dataContent: "Xóa" },
                ]}
                onCheckboxChange={handleCheckboxChange}
              />

              {/* Đơn hàng */}
              <tr>
                <td
                  style={{
                    fontWeight: "bold",
                    padding: "12px",
                    backgroundColor: "#e1f5fe",
                  }}
                  colSpan={listRoles.length + 1}
                >
                  Đơn hàng
                </td>
              </tr>
              <Permission
                roles={listRoles.map((role) => ({
                  ...role,
                  permission: updatedRoles[role.roleId] || [],
                }))}
                permissions={[
                  { dataName: "orders_view", dataContent: "Xem" },
                  { dataName: "orders_confirm", dataContent: "Duyệt đơn" },
                  { dataName: "orders_delete", dataContent: "Xóa" },
                ]}
                onCheckboxChange={handleCheckboxChange}
              />

              {/* Nhóm quyền */}
              <tr>
                <td
                  style={{
                    fontWeight: "bold",
                    padding: "12px",
                    backgroundColor: "#e1f5fe",
                  }}
                  colSpan={listRoles.length + 1}
                >
                  Nhóm quyền
                </td>
              </tr>
              <Permission
                roles={listRoles.map((role) => ({
                  ...role,
                  permission: updatedRoles[role.roleId] || [],
                }))}
                permissions={[
                  { dataName: "roles_view", dataContent: "Xem" },
                  { dataName: "roles_create", dataContent: "Thêm mới" },
                  { dataName: "roles_edit", dataContent: "Chỉnh sửa" },
                  { dataName: "roles_delete", dataContent: "Xóa" },
                  { dataName: "roles_permissions", dataContent: "Phân quyền" },
                ]}
                onCheckboxChange={handleCheckboxChange}
              />

              {/* Tài khoản */}
              <tr>
                <td
                  style={{
                    fontWeight: "bold",
                    padding: "12px",
                    backgroundColor: "#e1f5fe",
                  }}
                  colSpan={listRoles.length + 1}
                >
                  Tài khoản
                </td>
              </tr>
              <Permission
                roles={listRoles.map((role) => ({
                  ...role,
                  permission: updatedRoles[role.roleId] || [],
                }))}
                permissions={[
                  { dataName: "accounts_view", dataContent: "Xem" },
                  { dataName: "accounts_create", dataContent: "Thêm mới" },
                  { dataName: "accounts_edit", dataContent: "Chỉnh sửa" },
                  { dataName: "accounts_delete", dataContent: "Xóa" },
                ]}
                onCheckboxChange={handleCheckboxChange}
              />

              {/* Quiz */}
              <tr>
                <td
                  style={{
                    fontWeight: "bold",
                    padding: "12px",
                    backgroundColor: "#e1f5fe",
                  }}
                  colSpan={listRoles.length + 1}
                >
                  Bộ câu hỏi
                </td>
              </tr>
              <Permission
                roles={listRoles.map((role) => ({
                  ...role,
                  permission: updatedRoles[role.roleId] || [],
                }))}
                permissions={[
                  { dataName: "quiz_view", dataContent: "Xem" },
                  { dataName: "quiz_create", dataContent: "Thêm mới" },
                  { dataName: "quiz_edit", dataContent: "Chỉnh sửa" },
                  { dataName: "quiz_delete", dataContent: "Xóa" },
                ]}
                onCheckboxChange={handleCheckboxChange}
              />

              {/* Lộ trình chăm sóc da */}
              <tr>
                <td
                  style={{
                    fontWeight: "bold",
                    padding: "12px",
                    backgroundColor: "#e1f5fe",
                  }}
                  colSpan={listRoles.length + 1}
                >
                  Lộ trình chăm sóc da
                </td>
              </tr>
              <Permission
                roles={listRoles.map((role) => ({
                  ...role,
                  permission: updatedRoles[role.roleId] || [],
                }))}
                permissions={[
                  { dataName: "rountine_view", dataContent: "Xem" },
                  { dataName: "rountine_create", dataContent: "Tạo mới" },
                  { dataName: "rountine_edit", dataContent: "Chỉnh sửa" },
                ]}
                onCheckboxChange={handleCheckboxChange}
              />

              {/* Cài đặt */}
              <tr>
                <td
                  style={{
                    fontWeight: "bold",
                    padding: "12px",
                    backgroundColor: "#e1f5fe",
                  }}
                  colSpan={listRoles.length + 1}
                >
                  Cài đặt
                </td>
              </tr>
              <Permission
                roles={listRoles.map((role) => ({
                  ...role,
                  permission: updatedRoles[role.roleId] || [],
                }))}
                permissions={[
                  { dataName: "settings_view", dataContent: "Xem" },
                  { dataName: "settings_edit", dataContent: "Chỉnh sửa" },
                ]}
                onCheckboxChange={handleCheckboxChange}
              />
            </tbody>
          </table>
        </Paper>
      )}
    </Box>
  );
}
