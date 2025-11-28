"use client";

import React, { useContext, useState } from "react";
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
import { ProfileAdminContext } from "../../layout";

export default function CreateVoucherAdminPage() {
    const dataProfile = useContext(ProfileAdminContext);
    const [loading, setLoading] = useState(false);
    const [alertMessage, setAlertMessage] = useState<string>("");
    const [alertSeverity, setAlertSeverity] = useState<"success" | "error" | "info" | "warning">("info");
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    const tomorrowStr = tomorrow.toISOString().split("T")[0];

    const tenDaysLater = new Date();
    tenDaysLater.setDate(tomorrow.getDate() + 10);
    const tenDaysLaterStr = tenDaysLater.toISOString().split("T")[0];

    const [startDate, setStartDate] = useState(tomorrowStr);
    const [endDate, setEndDate] = useState(tenDaysLaterStr);
    const permissions = dataProfile?.permissions;

    const handleSubmit = async (event: any) => {
        event.preventDefault();
        setLoading(true);

        if (!event.target.name.value) {
            setAlertMessage("Tiêu đề mã giảm giá không được để trống.");
            setAlertSeverity("error");
            setTimeout(() => setAlertMessage(""), 5000);
            setLoading(false);
            return;
        }

        if (!event.target.discountValue.value) {
            setAlertMessage("Giá trị giảm không được để trống.");
            setAlertSeverity("error");
            setTimeout(() => setAlertMessage(""), 5000);
            setLoading(false);
            return;
        }

        if (event.target.type.value == "PERCENTAGE" && event.target.discountValue.value > 100) {
            setAlertMessage("Giá trị giảm không được quá 100%.");
            setAlertSeverity("error");
            setTimeout(() => setAlertMessage(""), 5000);
            setLoading(false);
            return;
        }

        if (!event.target.maxDiscount.value) {
            setAlertMessage("Giá trị tối đa được giảm không được để trống.");
            setAlertSeverity("error");
            setTimeout(() => setAlertMessage(""), 5000);
            setLoading(false);
            return;
        }

        if (!event.target.minOrderValue.value) {
            setAlertMessage("Giá trị tối thiểu đơn hàng áp dụng không được để trống.");
            setAlertSeverity("error");
            setTimeout(() => setAlertMessage(""), 5000);
            setLoading(false);
            return;
        }

        if (!event.target.usageLimit.value) {
            setAlertMessage("Số lượt sử dụng không được để trống.");
            setAlertSeverity("error");
            setTimeout(() => setAlertMessage(""), 5000);
            setLoading(false);
            return;
        }

        const start = new Date(event.target.startDate.value);
        const end = new Date(event.target.endDate.value);
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

        const data: any = {
            name: event.target.name.value,
            type: event.target.type.value,
            discountValue: parseInt(event.target.discountValue.value),
            maxDiscount: parseInt(event.target.maxDiscount.value),
            minOrderValue: parseInt(event.target.minOrderValue.value),
            usageLimit: parseInt(event.target.usageLimit.value),
            startDate: event.target.startDate.value,
            endDate: event.target.endDate.value,
        }

        console.log(data);

        const response = await fetch(`https://freshskinweb.onrender.com/admin/vouchers/create`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(data)
        });
        const dataResponse = await response.json();

        console.log(dataResponse);

        if (dataResponse.code === 200) {
            setAlertMessage(dataResponse.message);
            setAlertSeverity("success");
            setTimeout(() => location.reload(), 2000);
        } else {
            setAlertMessage(dataResponse.message);
            setAlertSeverity("error");
        }
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
                    Trang tạo mới mã giảm giá
                </Typography>

                {permissions?.includes("vouchers_create") && (
                    <Paper elevation={3} sx={{ padding: 3, marginBottom: 2 }}>
                        <form onSubmit={handleSubmit}>
                            <TextField
                                label="Mã giảm giá"
                                name="name"
                                variant="outlined"
                                fullWidth
                                sx={{ marginBottom: 3 }}
                            />
                            <FormControl fullWidth sx={{ marginBottom: 3 }}>
                                <RadioGroup defaultValue="FIXED_AMOUNT" name="type" row>
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
                            />
                            <TextField
                                label="Giá giảm tối đa"
                                type="number"
                                name="maxDiscount"
                                variant="outlined"
                                fullWidth
                                sx={{ marginBottom: 3 }}
                            />
                            <TextField
                                label="Giá trị đơn hàng tối thiểu"
                                type="number"
                                name="minOrderValue"
                                variant="outlined"
                                fullWidth
                                sx={{ marginBottom: 3 }}
                            />
                            <TextField
                                label="Lượt sử dụng"
                                type="number"
                                name="usageLimit"
                                variant="outlined"
                                fullWidth
                                sx={{ marginBottom: 3 }}
                            />
                            <TextField
                                label="Ngày bắt đầu"
                                type="date"
                                name="startDate"
                                variant="outlined"
                                fullWidth
                                value={startDate}
                                onChange={(e) => setStartDate(e.target.value)}
                                sx={{ marginBottom: 3 }}
                            />
                            <TextField
                                label="Ngày kết thúc"
                                type="date"
                                name="endDate"
                                variant="outlined"
                                fullWidth
                                value={endDate}
                                onChange={(e) => setEndDate(e.target.value)}
                                sx={{ marginBottom: 3 }}
                            />
                            <Button
                                type='submit'
                                variant="contained"
                                color="primary"
                                sx={{ width: '100%' }}
                                disabled={loading}
                            >
                                {loading ? "Đang tạo mã giảm giá..." : "Tạo mã giảm giá"}
                            </Button>
                        </form>
                    </Paper>
                )}
            </Box>
        </>
    );
}
