"use client";

import { useParams } from "next/navigation";
import { useContext, useEffect, useState } from "react";
import {
    Box,
    Typography,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    Paper
} from "@mui/material";
import { ProfileAdminContext } from "@/app/admin/layout";

export default function DetailOrderAdmin() {

    const dataProfile = useContext(ProfileAdminContext);
    const permissions = dataProfile?.permissions;
    const { id } = useParams();
    const [data, setData] = useState<any>({
        firstName: "",
        lastName: "",
        email: "",
        phoneNumber: "",
        totalAmount: 0,
        totalPrice: 0,
        paymentMethod: "",
        orderItems: [],
        address: "",
        orderId: "",
        discountAmout: 0,
        priceShipping: 0
    });

    useEffect(() => {
        const fetchDetailOrder = async () => {
            const response = await fetch(`https://freshskinweb.onrender.com/admin/orders/${id}`);
            const data = await response.json();
            setData(data.data);
        };

        fetchDetailOrder();
    }, [id]);


    return (
        <>
            {permissions?.includes("orders_view") && (
                <Box sx={{ padding: 2 }}>
                    <Typography variant="h4" gutterBottom>
                        Chi tiết đơn hàng <span style={{ color: "#3f51b5", fontWeight: "bold" }}>#{data.orderId}</span>
                    </Typography>
                    <Box sx={{ padding: 2, backgroundColor: "#f5f5f5", borderRadius: 1, marginBottom: 3 }}>
                        <Typography variant="h6" sx={{ marginBottom: 2 }}>
                            Thông tin người dùng
                        </Typography>
                        <TableContainer component={Paper}>
                            <Table>
                                <TableHead>
                                    <TableRow>
                                        <TableCell>Họ và tên</TableCell>
                                        <TableCell>Địa chỉ</TableCell>
                                        <TableCell>SĐT</TableCell>
                                        <TableCell>Email</TableCell>
                                        <TableCell>Phương thức thanh toán</TableCell>
                                    </TableRow>
                                </TableHead>
                                <TableBody>
                                    <TableRow>
                                        <TableCell>{data.firstName} {data.lastName}</TableCell>
                                        <TableCell>{data.address}</TableCell>
                                        <TableCell>{data.phoneNumber}</TableCell>
                                        <TableCell>{data.email}</TableCell>
                                        <TableCell>{data.paymentMethod == "CASH" ? "Thanh toán khi nhận hàng" : "Chuyển khoản"}</TableCell>
                                    </TableRow>
                                </TableBody>
                            </Table>
                        </TableContainer>
                    </Box>

                    <Box sx={{ padding: 2, backgroundColor: "#f5f5f5", borderRadius: 1 }}>
                        <Typography variant="h6" sx={{ marginBottom: 2 }}>
                            Danh sách sản phẩm trong đơn
                        </Typography>
                        <TableContainer component={Paper}>
                            <Table>
                                <TableHead>
                                    <TableRow>
                                        <TableCell>STT</TableCell>
                                        <TableCell>Ảnh</TableCell>
                                        <TableCell>Tên</TableCell>
                                        <TableCell>Giá</TableCell>
                                        <TableCell>Số lượng</TableCell>
                                        <TableCell>Tổng tiền</TableCell>
                                    </TableRow>
                                </TableHead>
                                <TableBody>
                                    {data.orderItems.map((item: any, index: number) => (
                                        <TableRow key={index}>
                                            <TableCell>{index + 1}</TableCell>
                                            <TableCell>
                                                <img src={item.productVariant.product.thumbnail[0]} alt={item.name} style={{ width: 100, height: 'auto' }} />
                                            </TableCell>
                                            <TableCell>{item.productVariant.product.title}</TableCell>
                                            <TableCell>{(item.productVariant.price * (1 - item.productVariant.product.discountPercent / 100)).toLocaleString("en-US")}<sup>đ</sup></TableCell>
                                            <TableCell>{item.quantity}</TableCell>
                                            <TableCell>{(item.subtotal).toLocaleString("en-US")}<sup>đ</sup></TableCell>
                                        </TableRow>
                                    ))}
                                </TableBody>
                            </Table>
                        </TableContainer>
                        {data.discountAmount > 0 ? (
                            <Typography variant="h6" sx={{ marginTop: 3, textAlign: "right" }}>
                                Khuyến mại: -{data.discountAmount.toLocaleString("en-US")}<sup>đ</sup>
                            </Typography>
                        ) : (
                            <Typography variant="h6" sx={{ marginTop: 3, textAlign: "right" }}>
                                Khuyến mại: -0<sup>đ</sup>
                            </Typography>
                        )}
                        <Typography variant="h6" sx={{ marginTop: 3, textAlign: "right" }}>
                            Phí vận chuyển: {data.priceShipping.toLocaleString("en-US")}<sup>đ</sup>
                        </Typography>
                        <Typography variant="h5" sx={{ marginTop: 3, textAlign: "right" }}>
                            Tổng đơn hàng: {data.totalPrice.toLocaleString("en-US")}<sup>đ</sup>
                        </Typography>
                    </Box>

                </Box>
            )}
        </>
    );
}