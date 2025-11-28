"use client"

import { Box, Typography, TextField, Select, MenuItem, InputLabel, FormControl, Button, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Chip, Tooltip } from "@mui/material";
import { BiDetail } from "react-icons/bi";
import { MdDeleteOutline, MdEditNote, MdOutlineChangeCircle } from "react-icons/md";
import Link from "next/link";
import { useContext, useEffect, useState } from "react";
import { ProfileAdminContext } from "../layout";
import Alert from '@mui/material/Alert';

export default function AccountAdminPage() {
    const dataProfile = useContext(ProfileAdminContext);
    const permissions = dataProfile?.permissions;

    const [data, setData] = useState([]);

    const linkApi = 'https://freshskinweb.onrender.com/admin/account';

    // Hiển thị lựa chọn mặc định
    const [filterStatus, setFilterStatus] = useState("");
    const [keyword, setKeyword] = useState("");
    const [alertMessage, setAlertMessage] = useState<string>("");
    const [alertSeverity, setAlertSeverity] = useState<"success" | "error" | "info" | "warning">("info");

    useEffect(() => {
        const urlCurrent = new URL(location.href);
        const api = new URL(linkApi);

        // Lọc theo trạng thái
        const statusCurrent = urlCurrent.searchParams.get('status');
        setFilterStatus(statusCurrent ?? "");

        if (statusCurrent) {
            api.searchParams.set('status', statusCurrent);
        }
        else {
            api.searchParams.delete('status');
        }
        // Hết Lọc theo trạng thái

        // Tìm kiếm tài khoản
        const keywordCurrent = urlCurrent.searchParams.get('keyword');
        setKeyword(keywordCurrent ?? "");

        if (keywordCurrent) {
            api.searchParams.set('keyword', keywordCurrent);
        }
        else {
            api.searchParams.delete('keyword');
        }
        // Hết Tìm kiếm tài khoản

        const fetchAccounts = async () => {
            const response = await fetch(api.href);
            const data = await response.json();
            setData(data.data.accounts);
        };

        fetchAccounts();
    }, []);


    // Tìm kiếm tài khoản
    const handleSumbitSearch = async (event: any) => {
        event.preventDefault();

        const value = event.target.keyword.value;
        const url = new URL(location.href);

        if (value) {
            url.searchParams.set("keyword", value);
        }
        else {
            url.searchParams.delete("keyword");
        }

        location.href = url.href;
    }

    // Hết Tìm kiếm sản phẩm



    // Xóa vĩnh viễn một tài khoản
    const handleDeleteOneAccount = async (id: number) => {
        const isConfirm = confirm("Bạn có chắc muốn xóa tài khoản?");
        if (isConfirm) {
            const path = `${linkApi}/deleteT/${id}`;

            const response = await fetch(path, {
                method: "PATCH",
                headers: {
                    "Content-Type": "application/json"
                },
            });

            const dataResponse = await response.json();

            if (dataResponse.code === 200) {
                setAlertMessage(dataResponse.message);
                setAlertSeverity("success");
                setTimeout(() => location.reload(), 2000);
            } else {
                setAlertMessage(dataResponse.message);
                setAlertSeverity("error");
            }
        }
    }
    // Hết Xóa một tài khoản

    return (
        <>
            {
                alertMessage && (
                    <Alert severity={alertSeverity} sx={{ mb: 2 }}>
                        {alertMessage}
                    </Alert>
                )
            }
            {permissions?.includes("accounts_view") && permissions?.includes("accounts_edit") && (
                <Box p={3}>
                    {/* Header */}
                    <Typography variant="h5" gutterBottom>
                        Trang danh sách tài khoản quản trị
                    </Typography>

                    {/* Bộ lọc và Tìm kiếm */}
                    <Paper elevation={1} sx={{ p: 2, mb: 2, bgcolor: "white" }} >
                        <Typography variant="subtitle1" fontWeight="bold" marginBottom={2} gutterBottom>
                            Tìm kiếm
                        </Typography>
                        <Box display="flex" flexWrap="wrap">
                            <form onSubmit={handleSumbitSearch} style={{ flex: 1, gap: "8px" }}>
                                <Box display="flex">
                                    <TextField
                                        label="Nhập họ / tên..."
                                        variant="outlined"
                                        fullWidth
                                        name="keyword"
                                        defaultValue={keyword}
                                        InputLabelProps={{
                                            shrink: true,
                                        }}
                                    />
                                    <Button variant="contained" color="success" type="submit" sx={{ backgroundColor: "#374785" }}>
                                        Tìm
                                    </Button>
                                </Box>
                            </form>
                        </Box>
                    </Paper>

                    {/* Table */}
                    <Paper sx={{ backgroundColor: "white", p: 2 }}>
                        <Typography variant="h6" gutterBottom sx={{ marginLeft: "20px" }}>
                            Danh sách
                        </Typography>
                        <Box display="flex" gap={20} flexWrap="wrap">
                            <Button
                                variant="outlined"
                                color="success"
                                sx={{ borderColor: '#374785', color: '#374785' }}
                            >
                                <Link href="/admin/accounts/create">
                                    + Thêm mới
                                </Link>
                            </Button>
                        </Box>
                        <TableContainer sx={{ marginTop: "40px" }} component={Paper}>
                            <Table>
                                <TableHead>
                                    <TableRow>
                                        <TableCell>STT</TableCell>
                                        <TableCell>Avatar</TableCell>
                                        <TableCell>Họ</TableCell>
                                        <TableCell>Tên</TableCell>
                                        <TableCell>Nhóm quyền</TableCell>
                                        <TableCell>Email</TableCell>
                                        <TableCell>Hành động</TableCell>
                                    </TableRow>
                                </TableHead>
                                <TableBody>
                                    {data.map((account: any, index: number) => (
                                        <TableRow key={index}>
                                            <TableCell>{index + 1}</TableCell>
                                            <TableCell>
                                                <img
                                                    src={account.avatar}
                                                    style={{ width: 100, height: 100, objectFit: "cover" }}
                                                />
                                            </TableCell>
                                            <TableCell>{account.firstName}</TableCell>
                                            <TableCell>{account.lastName}</TableCell>
                                            <TableCell>{account.role?.title || ""}</TableCell>
                                            <TableCell>{account.email}</TableCell>
                                            <TableCell>
                                                <div className="flex">
                                                    <Tooltip title="Chi tiết" placement="top">
                                                        <Link href={`/admin/accounts/detail/${account.userID}`}>
                                                            <BiDetail className="text-[25px] text-[#138496] mr-2" />
                                                        </Link>
                                                    </Tooltip>
                                                    <Tooltip title="Sửa" placement="top">
                                                        <Link href={`/admin/accounts/edit/${account.userID}`}>
                                                            <MdEditNote className="text-[25px] text-[#E0A800]" />
                                                        </Link>
                                                    </Tooltip>
                                                    <Tooltip title="Đổi mật khẩu" placement="top">
                                                        <Link href={`/admin/accounts/change-password/${account.userID}`}>
                                                            <MdOutlineChangeCircle className="text-[25px] text-[#E0A800]" />
                                                        </Link>
                                                    </Tooltip>
                                                    <Tooltip title="Xóa" placement="top" className="cursor-pointer" onClick={() => handleDeleteOneAccount(account.userID)}>
                                                        <MdDeleteOutline className="text-[25px] text-[#C62828] ml-1" />
                                                    </Tooltip>
                                                </div>
                                            </TableCell>
                                        </TableRow>
                                    ))}
                                </TableBody>
                            </Table>
                        </TableContainer>
                    </Paper>
                </Box>
            )}
        </>
    )
}