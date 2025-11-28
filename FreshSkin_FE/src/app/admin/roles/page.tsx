"use client"

import { Box, Typography, Button, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Tooltip } from "@mui/material";
import { BiDetail } from "react-icons/bi";
import { MdDeleteOutline, MdEditNote } from "react-icons/md";
import Link from "next/link";
import { useContext, useEffect, useState } from "react";
import { ProfileAdminContext } from "../layout";
import Alert from '@mui/material/Alert';
export default function RoleAdminPage() {
    const dataProfile = useContext(ProfileAdminContext);
    const permissions = dataProfile?.permissions;
    const [data, setData] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [alertMessage, setAlertMessage] = useState<string>("");
    const [alertSeverity, setAlertSeverity] = useState<"success" | "error" | "info" | "warning">("info");
    const linkApi = 'https://freshskinweb.onrender.com/admin/roles';

    useEffect(() => {
        const fetchData = async () => {
            const response = await fetch(linkApi);
            const data = await response.json();
            setData(data.data);
            setIsLoading(false);
        };

        fetchData();
    }, []);



    // Xóa một quyền
    const handleDeleteOneRole = async (id: number) => {
        const confirm: boolean = window.confirm("Bạn có chắc muốn xóa thể loại da này vĩnh viễn không?");
        if (confirm) {
            const path = `${linkApi}/delete/${id}`;

            const response = await fetch(path, {
                method: "DELETE",
                headers: {
                    "Content-Type": "application/json"
                }
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
    // Hết Xóa một quyền

    if (isLoading) {
        return <div>Loading...</div>;
    }


    return (
        <>
            {alertMessage && (
                <Alert severity={alertSeverity} sx={{ mb: 2 }}>
                    {alertMessage}
                </Alert>
            )}
            {permissions?.includes("roles_view") && permissions.includes("roles_edit") && (
                <Box p={3}>
                    <Typography variant="h5" gutterBottom>
                        Nhóm quyền
                    </Typography>
                    <Paper sx={{ backgroundColor: "white", p: 2 }}>
                        <Box display="flex" gap={20} flexWrap="wrap">
                            <Button
                                variant="outlined"
                                color="success"
                                sx={{ borderColor: '#374785', color: '#374785' }}
                            >
                                <Link href="/admin/roles/create">
                                    + Thêm mới
                                </Link>
                            </Button>
                        </Box>
                        <TableContainer sx={{ marginTop: "40px" }} component={Paper}>
                            <Table>
                                <TableHead>
                                    <TableRow>
                                        <TableCell>STT</TableCell>
                                        <TableCell>Nhóm quyền</TableCell>
                                        <TableCell>Mô tả ngắn</TableCell>
                                        {/* <TableCell>Tạo bởi</TableCell> */}
                                        {/* <TableCell>Cập nhật bởi</TableCell> */}
                                        <TableCell>Hành động</TableCell>
                                    </TableRow>
                                </TableHead>
                                <TableBody>
                                    {data.map((role: any, index: number) => (
                                        <TableRow key={index}>
                                            <TableCell>{index + 1}</TableCell>
                                            <TableCell>{role.title}</TableCell>
                                            <TableCell dangerouslySetInnerHTML={{ __html: role.description }}></TableCell>
                                            <TableCell>
                                                <div className="flex">
                                                    <Tooltip title="Chi tiết" placement="top">
                                                        <Link href={`/admin/roles/detail/${role.roleId}`}>
                                                            <BiDetail className="text-[25px] text-[#138496] mr-2" />
                                                        </Link>
                                                    </Tooltip>
                                                    <Tooltip title="Sửa" placement="top">
                                                        <Link href={`/admin/roles/edit/${role.roleId}`}>
                                                            <MdEditNote className="text-[25px] text-[#E0A800]" />
                                                        </Link>
                                                    </Tooltip>
                                                    <Tooltip title="Xóa" placement="top" className="cursor-pointer" onClick={() => handleDeleteOneRole(role.roleId)}>
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