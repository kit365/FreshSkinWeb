"use client"

import { Box, Typography, Button, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Tooltip } from "@mui/material";
import { BiDetail } from "react-icons/bi";
import { MdDeleteOutline, MdEditNote } from "react-icons/md";
import Link from "next/link";
import { useContext, useEffect, useState } from "react";
import { ProfileAdminContext } from "../layout";
import Alert from '@mui/material/Alert';
export default function VouchersAdminPage() {
    const dataProfile = useContext(ProfileAdminContext);
    const permissions = dataProfile?.permissions;
    const [data, setData] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [alertMessage, setAlertMessage] = useState<string>("");
    const [alertSeverity, setAlertSeverity] = useState<"success" | "error" | "info" | "warning">("info");
    const linkApi = 'https://freshskinweb.onrender.com/admin/vouchers';

    useEffect(() => {
        const fetchData = async () => {
            const response = await fetch(linkApi);
            const data = await response.json();
            setData(data.data);
            setIsLoading(false);
        };

        fetchData();
    }, []);

    console.log(permissions);



    // Xóa một voucher
    const handleDeleteOneVoucher = async (id: number) => {
        const confirm: boolean = window.confirm("Bạn có chắc muốn xóa mã giảm giá này vĩnh viễn không?");
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
    // Hết Xóa voucher

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
            {permissions?.includes("vouchers_view") && permissions.includes("vouchers_delete") && (
                <Box p={3}>
                    <Typography variant="h5" gutterBottom>
                        Danh sách mã giảm giá
                    </Typography>
                    <Paper sx={{ backgroundColor: "white", p: 2 }}>
                        <Box display="flex" gap={20} flexWrap="wrap">
                            <Button
                                variant="outlined"
                                color="success"
                                sx={{ borderColor: '#374785', color: '#374785' }}
                            >
                                <Link href="/admin/vouchers/create">
                                    + Thêm mới
                                </Link>
                            </Button>
                        </Box>
                        <TableContainer sx={{ marginTop: "40px" }} component={Paper}>
                            <Table>
                                <TableHead>
                                    <TableRow>
                                        <TableCell>STT</TableCell>
                                        <TableCell>Mã giảm giá</TableCell>
                                        <TableCell>Loại giảm giá</TableCell>
                                        <TableCell style={{textAlign: "center"}}>Giá trị giảm</TableCell>
                                        <TableCell style={{textAlign: "center"}}>Số lượt còn lại</TableCell>
                                        <TableCell>Hành động</TableCell>
                                    </TableRow>
                                </TableHead>
                                <TableBody>
                                    {data.map((voucher: any, index: number) => (
                                        <TableRow key={index}>
                                            <TableCell>{index + 1}</TableCell>
                                            <TableCell>{voucher.name}</TableCell>
                                            {voucher.type == "PERCENTAGE" ? (
                                                <TableCell sx={{ color: "#39A6D1" }}>Giảm giá theo phần trăm</TableCell>
                                            ) : (
                                                <TableCell sx={{ color: "#3EA18F" }}>Giảm giá theo giá cố định</TableCell>
                                            )}
                                            {voucher.type == "PERCENTAGE" ? (
                                                <TableCell style={{textAlign: "center"}}>{voucher.discountValue}%</TableCell>
                                            ) : (
                                                <TableCell style={{textAlign: "center"}}>{voucher.discountValue.toLocaleString("en-US")} VND</TableCell>
                                            )}
                                            <TableCell style={{textAlign: "center"}}>{voucher.usageLimit}</TableCell>
                                            <TableCell>
                                                <div className="flex">
                                                    <Tooltip title="Chi tiết" placement="top">
                                                        <Link href={`/admin/vouchers/detail/${voucher.voucherId}`}>
                                                            <BiDetail className="text-[25px] text-[#138496] mr-2" />
                                                        </Link>
                                                    </Tooltip>
                                                    <Tooltip title="Sửa" placement="top">
                                                        <Link href={`/admin/vouchers/edit/${voucher.voucherId}`}>
                                                            <MdEditNote className="text-[25px] text-[#E0A800]" />
                                                        </Link>
                                                    </Tooltip>
                                                    <Tooltip title="Xóa" placement="top" className="cursor-pointer" onClick={() => handleDeleteOneVoucher(voucher.voucherId)}>
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