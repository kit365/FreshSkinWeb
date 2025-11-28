"use client"

import { Box, Typography, Button, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Tooltip } from "@mui/material";
import { MdDeleteOutline, MdEditNote } from "react-icons/md";
import Alert from '@mui/material/Alert';
import { useEffect, useState } from "react";
import Link from "next/link";

export default function ProductsAdminPage() {
    const [data, setData] = useState([]);
    const [alertMessage, setAlertMessage] = useState<string>("");
    const [alertSeverity, setAlertSeverity] = useState<"success" | "error" | "info" | "warning">("info");
    const linkApiShow = 'https://freshskinweb.onrender.com/admin/skintypes/show';
    const linkApi = 'https://freshskinweb.onrender.com/admin/skintypes';

    // Hiển thị lựa chọn mặc định
    useEffect(() => {
        const fetchSkintypes = async () => {
            const response = await fetch(linkApiShow);
            const data = await response.json();
            setData(data.data);
        };

        fetchSkintypes();
    }, []);

    // Xóa một sản phẩm
    const handleDeleteOneSkinType = async (id: number) => {
        const confirm: boolean = window.confirm("Bạn có chắc muốn xóa thể loại da này vĩnh viễn không?");
        if (confirm) {
            const path = `${linkApi}/delete/${id}`;

            const response = await fetch(path, {
                method: "DELETE",
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
    // Hết Xóa một sản phẩm

    return (
        <>
            {
                alertMessage && (
                    <Alert severity={alertSeverity} sx={{ mb: 2 }}>
                        {alertMessage}
                    </Alert>
                )
            }

            <Box p={3}>
                {/* Header */}
                <Typography variant="h5" gutterBottom>
                    Trang thể loại da
                </Typography>

                {/* Table */}
                <Paper sx={{ backgroundColor: "white", p: 2 }}>
                    <Box display="flex" gap={20} flexWrap="wrap">
                        <Button
                            variant="outlined"
                            color="success"
                            sx={{ borderColor: '#374785', color: '#374785', marginLeft: "88%" }}
                        >
                            <Link href="/admin/skin/create">
                                + Thêm mới
                            </Link>
                        </Button>
                    </Box>
                    <TableContainer sx={{ marginTop: "40px" }} component={Paper}>
                        <Table>
                            <TableHead>
                                <TableRow>
                                    <TableCell sx={{ color: "#374785", fontWeight: "bold" }} >STT</TableCell>
                                    <TableCell sx={{ color: "#374785", fontWeight: "bold" }} >Loại da</TableCell>
                                    <TableCell sx={{ color: "#374785", fontWeight: "bold" }} >Mô tả</TableCell>
                                    <TableCell sx={{ color: "#374785", fontWeight: "bold" }} >Hành động</TableCell>
                                </TableRow>
                            </TableHead>
                            <TableBody>
                                {data.map((type: any, index: number) => (
                                    <TableRow key={type.id}>
                                        <TableCell>{index + 1}</TableCell>
                                        <TableCell>{type.type}</TableCell>
                                        <TableCell>{type.description}</TableCell>
                                        {/* <TableCell>
                                    {product.createdBy}
                                    <br />
                                    {product.createdAt}
                                </TableCell>
                                <TableCell>
                                    {product.updatedBy}
                                    <br />
                                    {product.updatedAt}
                                </TableCell> */}
                                        <TableCell>
                                            <div className="flex">
                                                <Tooltip title="Sửa" placement="top">
                                                    <Link href={`/admin/skin/edit/${type.id}`}>
                                                        <MdEditNote className="text-[25px] text-[#E0A800]" />
                                                    </Link>
                                                </Tooltip>
                                                <Tooltip title="Xóa" placement="top" className="cursor-pointer" onClick={() => handleDeleteOneSkinType(type.id)}>
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
        </>
    );
}