"use client"

import { useParams } from "next/navigation";
import { useContext, useEffect, useState } from "react";
import {
    Box,
    Container,
    Typography,
    Card,
    CardContent,
    Chip,
    Divider,
    Grid,
    Paper
} from "@mui/material";
import { ProfileAdminContext } from "@/app/admin/layout";

// Định nghĩa kiểu dữ liệu cho Role
interface RoleData {
    title: string;
    description: string;
    status: string;
    permission: string[];
}

export default function DetailRoleAdmin() {
    const dataProfile = useContext(ProfileAdminContext);
    const permissions = dataProfile?.permissions;

    const { id } = useParams();
    const [data, setData] = useState<RoleData>({
        title: "",
        description: "",
        status: "ACTIVE",
        permission: []
    });

    // Fetch role data
    useEffect(() => {
        const fetchRole = async () => {
            const response = await fetch(`https://freshskinweb.onrender.com/admin/roles/${id}`);
            const result = await response.json();
            setData(result.data);
        };

        fetchRole();
    }, [id]);

    const permissionColors: Record<string, string> = {
        "view": "#4caf50",
        "create": "#ffa500",
        "edit": "#1976d2",
        "delete": "#f44336",
        "default": "#757575"
    };

    const permissionLabels: Record<string, string> = {
        "dashboard_view": "Xem trang tổng quan",
        "products-category_view": "Xem danh mục sản phẩm",
        "products-category_create": "Tạo danh mục sản phẩm",
        "products-category_edit": "Chỉnh sửa danh mục sản phẩm",
        "products-category_delete": "Xóa danh mục sản phẩm",
        "products_view": "Xem sản phẩm",
        "products_create": "Tạo sản phẩm",
        "products_edit": "Chỉnh sửa sản phẩm",
        "products_delete": "Xóa sản phẩm",
        "brands_view": "Xem thương hiệu",
        "brands_create": "Tạo thương hiệu",
        "brands_edit": "Chỉnh sửa thương hiệu",
        "brands_delete": "Xóa thương hiệu",
        "skin_view": "Xem loại da",
        "skin_create": "Tạo loại da",
        "skin_edit": "Chỉnh sửa loại da",
        "skin_delete": "Xóa loại da",
        "blogs-category_view": "Xem danh mục bài viết",
        "blogs-category_create": "Tạo danh mục bài viết",
        "blogs-category_edit": "Chỉnh sửa danh mục bài viết",
        "blogs-category_delete": "Xóa danh mục bài viết",
        "blogs_view": "Xem bài viết",
        "blogs_create": "Tạo bài viết",
        "blogs_edit": "Chỉnh sửa bài viết",
        "blogs_delete": "Xóa bài viết",
        "orders_view": "Xem đơn hàng",
        "orders_confirm": "Xác nhận đơn hàng",
        "orders_delete": "Xóa đơn hàng",
        "roles_view": "Xem vai trò",
        "roles_create": "Tạo vai trò",
        "roles_edit": "Chỉnh sửa vai trò",
        "roles_delete": "Xóa vai trò",
        "roles_permissions": "Quản lý quyền vai trò",
        "accounts_view": "Xem tài khoản",
        "accounts_create": "Tạo tài khoản",
        "accounts_edit": "Chỉnh sửa tài khoản",
        "accounts_delete": "Xóa tài khoản",
        "settings_edit": "Chỉnh sửa cài đặt",
        "settings_view": "Xem cài đặt"
    };

    const renderPermission = (permission: string) => {
        const action = permission.split("_")[1];
        const color = permissionColors[action] || permissionColors.default;
        const label = permissionLabels[permission] || permission;

        return (
            <Grid item xs={12} sm={6} md={4} key={permission}>
                <Paper sx={{ backgroundColor: color, padding: 1, textAlign: "center", borderRadius: 1 }}>
                    <Typography variant="body2" sx={{ color: "#fff" }}>
                        {label.toUpperCase()}
                    </Typography>
                </Paper>
            </Grid>
        );
    };

    return (
        <Container maxWidth="md" sx={{ mt: 4 }}>
            {permissions?.includes("roles_view") && (
                <Card elevation={3}>
                    <CardContent>
                        {/* Tên vai trò */}
                        <Typography variant="h4" component="h1" gutterBottom sx={{ fontWeight: "bold", textAlign: "center" }}>
                            {data.title}
                        </Typography>

                        {/* Trạng thái */}
                        <Box sx={{ textAlign: "center", mb: 2 }}>
                            <Chip
                                label={data.status === "ACTIVE" ? "Đang hoạt động" : "Không hoạt động"}
                                color={data.status === "ACTIVE" ? "success" : "error"}
                                sx={{ fontSize: "1rem", fontWeight: "bold" }}
                            />
                        </Box>

                        <Divider sx={{ my: 2 }} />

                        {/* Mô tả vai trò */}
                        <Box sx={{ mb: 2 }}>
                            <Box sx={{ display: "flex", alignItems: "center" }}>
                                <Typography variant="body1" sx={{ fontWeight: "bold", color: "#555", mr: 1 }}>
                                    Mô tả:
                                </Typography>
                                <div dangerouslySetInnerHTML={{ __html: data.description }}></div>
                            </Box>
                        </Box>

                        <Divider sx={{ my: 2 }} />

                        {/* Quyền của vai trò */}
                        <Typography variant="h6" sx={{ fontWeight: "bold", color: "#555", mb: 2 }}>
                            Quyền của vai trò
                        </Typography>

                        <Grid container spacing={2}>
                            {data.permission.includes("dashboard_view") && renderPermission("dashboard_view")}
                            {data.permission.includes("products-category_view") && renderPermission("products-category_view")}
                            {data.permission.includes("products-category_create") && renderPermission("products-category_create")}
                            {data.permission.includes("products-category_edit") && renderPermission("products-category_edit")}
                            {data.permission.includes("products-category_delete") && renderPermission("products-category_delete")}
                            {data.permission.includes("products_view") && renderPermission("products_view")}
                            {data.permission.includes("products_create") && renderPermission("products_create")}
                            {data.permission.includes("products_edit") && renderPermission("products_edit")}
                            {data.permission.includes("products_delete") && renderPermission("products_delete")}
                            {data.permission.includes("brands_view") && renderPermission("brands_view")}
                            {data.permission.includes("brands_create") && renderPermission("brands_create")}
                            {data.permission.includes("brands_edit") && renderPermission("brands_edit")}
                            {data.permission.includes("brands_delete") && renderPermission("brands_delete")}
                            {data.permission.includes("skin_view") && renderPermission("skin_view")}
                            {data.permission.includes("skin_create") && renderPermission("skin_create")}
                            {data.permission.includes("skin_edit") && renderPermission("skin_edit")}
                            {data.permission.includes("skin_delete") && renderPermission("skin_delete")}
                            {data.permission.includes("blogs-category_view") && renderPermission("blogs-category_view")}
                            {data.permission.includes("blogs-category_create") && renderPermission("blogs-category_create")}
                            {data.permission.includes("blogs-category_edit") && renderPermission("blogs-category_edit")}
                            {data.permission.includes("blogs-category_delete") && renderPermission("blogs-category_delete")}
                            {data.permission.includes("blogs_view") && renderPermission("blogs_view")}
                            {data.permission.includes("blogs_create") && renderPermission("blogs_create")}
                            {data.permission.includes("blogs_edit") && renderPermission("blogs_edit")}
                            {data.permission.includes("blogs_delete") && renderPermission("blogs_delete")}
                            {data.permission.includes("orders_view") && renderPermission("orders_view")}
                            {data.permission.includes("orders_confirm") && renderPermission("orders_confirm")}
                            {data.permission.includes("orders_delete") && renderPermission("orders_delete")}
                            {data.permission.includes("roles_view") && renderPermission("roles_view")}
                            {data.permission.includes("roles_create") && renderPermission("roles_create")}
                            {data.permission.includes("roles_edit") && renderPermission("roles_edit")}
                            {data.permission.includes("roles_delete") && renderPermission("roles_delete")}
                            {data.permission.includes("roles_permissions") && renderPermission("roles_permissions")}
                            {data.permission.includes("accounts_view") && renderPermission("accounts_view")}
                            {data.permission.includes("accounts_create") && renderPermission("accounts_create")}
                            {data.permission.includes("accounts_edit") && renderPermission("accounts_edit")}
                            {data.permission.includes("accounts_delete") && renderPermission("accounts_delete")}
                            {data.permission.includes("settings_edit") && renderPermission("settings_edit")}
                            {data.permission.includes("settings_view") && renderPermission("settings_view")}
                        </Grid>
                    </CardContent>
                </Card>
            )}
        </Container>
    );
}
