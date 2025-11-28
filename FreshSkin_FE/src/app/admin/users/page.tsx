"use client"

import { Box, Typography, TextField, Select, MenuItem, InputLabel, FormControl, Button, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Chip, Tooltip, Checkbox, Stack, Pagination } from "@mui/material";
import { BiDetail } from "react-icons/bi";
import { MdDeleteOutline } from "react-icons/md";
import Link from "next/link";
import { useContext, useEffect, useState } from "react";
import { ProfileAdminContext } from "../layout";
import DeleteIcon from "@mui/icons-material/Delete";
import Alert from '@mui/material/Alert';
export default function UserAdminPage() {
    const dataProfile = useContext(ProfileAdminContext);
    const permissions = dataProfile?.permissions;
    const [page, setPage] = useState(1);

    const [data, setData] = useState({
        totalItems: 0,
        totalPages: 2,
        pageSize: 10,
        currentPage: 1,
        users: []
    });

    const linkApi = 'https://freshskinweb.onrender.com/admin/users';

    const [inputChecked, setInputChecked] = useState<number[]>([]);
    // Hiển thị lựa chọn mặc định
    const [filterStatus, setFilterStatus] = useState("");
    const [keyword, setKeyword] = useState("");
    const [changeMulti, setChangeMulti] = useState("ACTIVE");

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

        // Phân trang
        const pageCurrent = urlCurrent.searchParams.get("page");
        setPage(pageCurrent ? parseInt(pageCurrent) : 1);

        if (pageCurrent) {
            api.searchParams.set("page", pageCurrent);
        } else {
            api.searchParams.delete("page");
        }
        // Hết Phân trang

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
            setData(data.data);
        };

        fetchAccounts();
    }, []);

    // Lọc theo trạng thái
    const handleChangeFilterStatus = async (event: any) => {
        const value = event.target.value;
        const url = new URL(location.href);

        if (value) {
            url.searchParams.set("status", value);
        }
        else {
            url.searchParams.delete("status");
        }

        location.href = url.href;
    }
    // Hết Lọc theo trạng thái

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

    // Thay đổi trạng thái nhiều tài khoản
    const handleChangeMulti = async (event: any) => {
        event.preventDefault();

        const statusChange = changeMulti;
        const path = `${linkApi}/change-multi`;

        const data: any = {
            id: inputChecked,
            status: statusChange
        }

        const response = await fetch(path, {
            method: "PATCH",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(data)
        });

        const dataResponse = await response.json();

        console.log(dataResponse)

        if (dataResponse.code === 200) {
            setAlertMessage(dataResponse.message);
            setAlertSeverity("success");
            setTimeout(() => location.reload(), 2000);
        } else {
            setAlertMessage(dataResponse.message);
            setAlertSeverity("error");
        }
    }
    const [alertMessage, setAlertMessage] = useState<string>("");
    const [alertSeverity, setAlertSeverity] = useState<"success" | "error" | "info" | "warning">("info");
    const handleInputChecked = (event: any, id: number) => {
        if (event.target.checked) {
            setInputChecked(prev => [...prev, id]);
        } else {
            setInputChecked(prev => prev.filter(id => id !== id));
        }
    }
    // Hết Thay đổi trạng thái nhiều tài khoản

    // Thay đổi trạng thái 1 tài khoản
    const handleChangeStatusOneAccount = async (status: string, dataPath: string) => {
        const statusChange = status;
        const path = `${linkApi}${dataPath}`;

        const data = {
            status: statusChange
        }

        const response = await fetch(path, {
            method: "PATCH",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(data)
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

    // Xóa một tài khoản
    const handleDeleteOneAccount = async (id: number) => {
        const confirm: boolean = window.confirm("Bạn có chắc muốn xoá tài khoản này không?");
    if (confirm) {
        const path = `${linkApi}/deleteT/${id}`;

        console.log(path);

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

    // Phân trang
    const handlePagination = (event: any, page: number) => {
        const url = new URL(location.href);

        if (page) {
            url.searchParams.set("page", page.toString());
        } else {
            url.searchParams.delete("page");
        }

        location.href = url.href;
    };
    // Hết phân trang

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
                        Trang danh sách tài khoản người dùng
                    </Typography>

                    {/* Bộ lọc và Tìm kiếm */}
                    <Paper elevation={1} sx={{ p: 2, mb: 2, bgcolor: "white" }} >
                        <Typography variant="subtitle1" fontWeight="bold" marginBottom={2} gutterBottom>
                            Bộ lọc và Tìm kiếm
                        </Typography>
                        <Box display="flex" flexWrap="wrap">
                            <FormControl sx={{ width: '30%', marginRight: '20px' }} >
                                <InputLabel id="filter-label" shrink={true}>Bộ lọc</InputLabel>
                                <Select labelId="filter-label" label="Bộ lọc" value={filterStatus} displayEmpty onChange={handleChangeFilterStatus} >
                                    <MenuItem value="">Tất cả</MenuItem>
                                    <MenuItem value="ACTIVE">Hoạt động</MenuItem>
                                    <MenuItem value="INACTIVE">Dừng hoạt động</MenuItem>
                                </Select>
                            </FormControl>
                            <form onSubmit={handleSumbitSearch} style={{ flex: 1, gap: "8px" }}>
                                <Box display="flex">
                                    <TextField
                                        label="Nhập từ khóa..."
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
                            <form onSubmit={handleChangeMulti} style={{ flex: 1, gap: "8px" }}>
                                <Box display="flex" >
                                    <Select fullWidth name="status" value={changeMulti} displayEmpty onChange={(e) => setChangeMulti(e.target.value)} >
                                        <MenuItem value="ACTIVE">Hoạt động</MenuItem>
                                        <MenuItem value="INACTIVE">Dừng hoạt động</MenuItem>
                                        <MenuItem value="SOFT_DELETED">Xóa</MenuItem>
                                    </Select>
                                    <Button variant="contained" color="success" type="submit" sx={{ width: "120px", backgroundColor: '#374785', color: '#ffffff' }}>
                                        Áp dụng
                                    </Button>
                                </Box>
                            </form>
                            <Button
                                variant="contained"
                                startIcon={<DeleteIcon />}
                                sx={{ backgroundColor: '#757575', '&:hover': { backgroundColor: '#616161' } }}
                            >
                                <Link href="/admin/users/trash">
                                    Thùng rác
                                </Link>
                            </Button>
                        </Box>
                        <TableContainer sx={{ marginTop: "40px" }} component={Paper}>
                            <Table>
                                <TableHead>
                                    <TableRow>
                                        <TableCell ></TableCell>
                                        <TableCell>STT</TableCell>
                                        <TableCell>Avatar</TableCell>
                                        <TableCell>Họ</TableCell>
                                        <TableCell>Tên</TableCell>
                                        <TableCell>Tên tài khoản</TableCell>
                                        <TableCell>Số điện thoại</TableCell>
                                        <TableCell>Trạng thái</TableCell>
                                        <TableCell>Hành động</TableCell>
                                    </TableRow>
                                </TableHead>
                                <TableBody>
                                    {data.users.map((account: any, index: number) => (
                                        <TableRow key={index}>
                                            <TableCell padding="checkbox" onClick={(event) => handleInputChecked(event, account.userID)}>
                                                <Checkbox />
                                            </TableCell>
                                            <TableCell>{index + 1}</TableCell>
                                            <TableCell>
                                                <img
                                                    src={account.avatar}
                                                    style={{ width: 100, height: 100, objectFit: "cover" }}
                                                />
                                            </TableCell>
                                            <TableCell>{account.firstName}</TableCell>
                                            <TableCell>{account.lastName}</TableCell>
                                            <TableCell>{account.username}</TableCell>
                                            <TableCell>{account.phone}</TableCell>
                                            <TableCell>
                                                {account.status === "ACTIVE" && (
                                                    <Chip
                                                        label="Hoạt động"
                                                        color="success"
                                                        size="small"
                                                        variant="outlined"
                                                        onClick={() => handleChangeStatusOneAccount("INACTIVE", `/edit/${account.userID}`)}
                                                    />
                                                )}
                                                {account.status === "INACTIVE" && (
                                                    <Chip
                                                        label="Dừng hoạt động"
                                                        color="error"
                                                        size="small"
                                                        variant="outlined"
                                                        onClick={() => handleChangeStatusOneAccount("ACTIVE", `/edit/${account.userID}`)}
                                                    />
                                                )}
                                            </TableCell>
                                            <TableCell>
                                                <div className="flex">
                                                    <Tooltip title="Chi tiết" placement="top">
                                                        <Link href={`/admin/users/detail/${account.userID}`}>
                                                            <BiDetail className="text-[25px] text-[#138496] mr-2" />
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
                    {/* Pagination */}
                    <Stack spacing={2} marginTop={2}>
                        <Pagination
                            count={data.totalPages}
                            color="primary"
                            page={page}
                            variant="outlined"
                            shape="rounded"
                            siblingCount={1}
                            sx={{
                                "& .MuiPaginationItem-root": {
                                    backgroundColor: "white",
                                    color: "blue",
                                    "&:hover": {
                                        backgroundColor: "#e0e0e0",
                                    },
                                },
                                "& .Mui-selected": {
                                    backgroundColor: "blue",
                                    color: "white",
                                },
                            }}
                            onChange={handlePagination}
                        />
                    </Stack>
                </Box>
            )}
        </>
    )
}