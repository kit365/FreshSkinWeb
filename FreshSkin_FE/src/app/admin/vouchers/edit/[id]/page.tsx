"use client";

import React, { useContext, useEffect, useState } from "react";
import Alert from "@mui/material/Alert";
import {
    Box,
    Typography,
    TextField,
    FormControl,
    Button,
    Paper,
    RadioGroup,
    FormControlLabel,
    Radio,
} from "@mui/material";
import { useParams } from "next/navigation";
import { ProfileAdminContext } from "../../../layout";

export default function EditVoucherAdminPage() {
    const dataProfile = useContext(ProfileAdminContext);
    const permissions = dataProfile?.permissions;
    const { id } = useParams();

    const [loading, setLoading] = useState(false);
    const [alertMessage, setAlertMessage] = useState<string>("");
    const [alertSeverity, setAlertSeverity] = useState<"success" | "error" | "info" | "warning">("info");

    const [data, setData] = useState({
        name: "",
        type: "FIXED_AMOUNT",
        discountValue: 0,
        maxDiscount: 0,
        minOrderValue: 0,
        usageLimit: 0,
        startDate: "",
        endDate: ""
    });

    useEffect(() => {
        const fetchData = async () => {
            const response = await fetch(`https://freshskinweb.onrender.com/admin/vouchers/${id}`);
            const dataResponse = await response.json();
            if (dataResponse.code === 200) {
                setData(dataResponse.data);
            }
        };

        fetchData();
    }, [id]);

    console.log(data);

    const handleSubmit = async (event: any) => {
        event.preventDefault();
        setLoading(true);

        const {
            name, type, discountValue, maxDiscount, minOrderValue, usageLimit, startDate, endDate
        } = data;

        if (!name) {
            setAlertMessage("Tiêu đề mã giảm giá không được để trống.");
            setAlertSeverity("error");
            setTimeout(() => setAlertMessage(""), 5000);
            setLoading(false);
            return;
        }

        if (!discountValue) {
            setAlertMessage("Giá trị giảm không được để trống.");
            setAlertSeverity("error");
            setTimeout(() => setAlertMessage(""), 5000);
            setLoading(false);
            return;
        }

        if (type === "PERCENTAGE" && discountValue > 100) {
            setAlertMessage("Giá trị giảm không được quá 100%.");
            setAlertSeverity("error");
            setTimeout(() => setAlertMessage(""), 5000);
            setLoading(false);
            return;
        }

        if (!maxDiscount) {
            setAlertMessage("Giá trị tối đa được giảm không được để trống.");
            setAlertSeverity("error");
            setTimeout(() => setAlertMessage(""), 5000);
            setLoading(false);
            return;
        }

        if (!minOrderValue) {
            setAlertMessage("Giá trị tối thiểu đơn hàng áp dụng không được để trống.");
            setAlertSeverity("error");
            setTimeout(() => setAlertMessage(""), 5000);
            setLoading(false);
            return;
        }

        if (!usageLimit) {
            setAlertMessage("Số lượt sử dụng không được để trống.");
            setAlertSeverity("error");
            setTimeout(() => setAlertMessage(""), 5000);
            setLoading(false);
            return;
        }

        const start = new Date(startDate);
        const end = new Date(endDate);
        const now = new Date();

        if (isNaN(start.getTime()) || isNaN(end.getTime())) {
            setAlertMessage("Ngày bắt đầu và kết thúc phải hợp lệ.");
            setAlertSeverity("error");
            setTimeout(() => setAlertMessage(""), 5000);
            setLoading(false);
            return;
        }

        if (start < new Date(now.toDateString())) {
            setAlertMessage("Ngày bắt đầu phải từ hôm nay trở đi.");
            setAlertSeverity("error");
            setTimeout(() => setAlertMessage(""), 5000);
            setLoading(false);
            return;
        }

        if (end <= start) {
            setAlertMessage("Ngày kết thúc phải sau ngày bắt đầu.");
            setAlertSeverity("error");
            setTimeout(() => setAlertMessage(""), 5000);
            setLoading(false);
            return;
        }

        const response = await fetch(`https://freshskinweb.onrender.com/admin/vouchers/update/${id}`, {
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

        setLoading(false);
    };

    return (
        <>
            {alertMessage && (
                <Alert severity={alertSeverity} sx={{ position: "fixed", width: "600px", height: "60px", right: "5%", top: "5%", fontSize: "16px", zIndex: "999999" }}>
                    {alertMessage}
                </Alert>
            )}
            <Box sx={{ padding: 3, backgroundColor: "#ffffff" }}>
                <Typography variant="h5" gutterBottom>
                    Trang chỉnh sửa mã giảm giá
                </Typography>
                {permissions?.includes("vouchers_edit") && (
                    <Paper elevation={3} sx={{ padding: 3, marginBottom: 2 }}>
                        <form onSubmit={handleSubmit}>
                            <TextField
                                label="Mã giảm giá"
                                name="name"
                                variant="outlined"
                                fullWidth
                                sx={{ marginBottom: 3 }}
                                value={data.name}
                                onChange={(e) => setData({ ...data, name: e.target.value })}
                            />
                            <FormControl fullWidth sx={{ marginBottom: 3 }}>
                                <RadioGroup value={data.type} name="type" row
                                    onChange={(e) => setData({ ...data, type: e.target.value })}
                                >
                                    <FormControlLabel
                                        value="FIXED_AMOUNT"
                                        control={<Radio />}
                                        label="Giảm theo giá"
                                    />
                                    <FormControlLabel
                                        value="PERCENTAGE"
                                        control={<Radio />}
                                        label="Giảm theo phần trăm"
                                    />
                                </RadioGroup>
                            </FormControl>
                            <TextField
                                label="Giá giảm / Phần trăm giảm"
                                type="number"
                                name="discountValue"
                                variant="outlined"
                                fullWidth
                                sx={{ marginBottom: 3 }}
                                value={data.discountValue}
                                onChange={(e) => setData({ ...data, discountValue: parseInt(e.target.value) })}
                            />
                            <TextField
                                label="Giá giảm tối đa"
                                type="number"
                                name="maxDiscount"
                                variant="outlined"
                                fullWidth
                                sx={{ marginBottom: 3 }}
                                value={data.maxDiscount}
                                onChange={(e) => setData({ ...data, maxDiscount: parseInt(e.target.value) })}
                            />
                            <TextField
                                label="Giá trị đơn hàng tối thiểu"
                                type="number"
                                name="minOrderValue"
                                variant="outlined"
                                fullWidth
                                sx={{ marginBottom: 3 }}
                                value={data.minOrderValue}
                                onChange={(e) => setData({ ...data, minOrderValue: parseInt(e.target.value) })}
                            />
                            <TextField
                                label="Lượt sử dụng"
                                type="number"
                                name="usageLimit"
                                variant="outlined"
                                fullWidth
                                sx={{ marginBottom: 3 }}
                                value={data.usageLimit}
                                onChange={(e) => setData({ ...data, usageLimit: parseInt(e.target.value) })}
                            />
                            <TextField
                                label="Ngày bắt đầu"
                                type="date"
                                name="startDate"
                                variant="outlined"
                                fullWidth
                                value={data.startDate}
                                onChange={(e) => setData({ ...data, startDate: e.target.value })}
                                sx={{ marginBottom: 3 }}
                            />
                            <TextField
                                label="Ngày kết thúc"
                                type="date"
                                name="endDate"
                                variant="outlined"
                                fullWidth
                                value={data.endDate}
                                onChange={(e) => setData({ ...data, endDate: e.target.value })}
                                sx={{ marginBottom: 3 }}
                            />
                            <Button
                                type="submit"
                                variant="contained"
                                color="primary"
                                sx={{ width: "100%" }}
                                disabled={loading}
                            >
                                {loading ? "Đang cập nhật mã giảm giá..." : "Cập nhật mã giảm giá"}
                            </Button>
                        </form>
                    </Paper>
                )}
            </Box>
        </>
    );
}
