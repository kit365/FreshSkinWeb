"use client"

import { Box, Button, Paper, TextField, Typography } from "@mui/material";
import Alert from '@mui/material/Alert';
import { useState } from "react";
interface SkinType {
    type: string,
    description: string
}

export default function CreateTypeSkinAdminPage() {
    const [loading, setLoading] = useState(false);
    const [alertMessage, setAlertMessage] = useState<string>("");
    const [alertSeverity, setAlertSeverity] = useState<"success" | "error" | "info" | "warning">("info");

    const handleSubmit = async (event: any) => {
        event.preventDefault();
        setLoading(true);

        if (!event.target.type.value) {
            setAlertMessage("Thể loại da không được để trống.");
            setAlertSeverity("error");
            setTimeout(() => setAlertMessage(""), 5000);
            setLoading(false);
            return;
        }

        if (!event.target.description.value) {
            setAlertMessage("Mô tả thể loại da không được để trống.");
            setAlertSeverity("error");
            setTimeout(() => setAlertMessage(""), 5000);
            setLoading(false);
            return;
        }

        const data: SkinType = {
            type: event.target.type.value,
            description: event.target.description.value
        }

        const response = await fetch('https://freshskinweb.onrender.com/admin/skintypes/create', {
            method: "POST",
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
            setLoading(false);
            setTimeout(() => {
                setAlertMessage("");
            }, 2000);
        }
    }

    return (
        <>
            {alertMessage && (
                <Alert severity={alertSeverity} sx={{ position: "fixed", width: "600px", height: "60px", right: "5%", top: "5%", fontSize: "16px", zIndex: "999999" }}>
                    {alertMessage}
                </Alert>
            )}

            <Box sx={{ padding: 3, backgroundColor: '#ffffff' }}>
                <Typography variant="h5" gutterBottom>
                    Trang tạo mới thể loại da
                </Typography>

                <Paper elevation={3} sx={{ padding: 3, marginBottom: 2 }}>
                    <form onSubmit={handleSubmit}>
                        <TextField
                            label="Thể loại da"
                            name='type'
                            variant="outlined"
                            fullWidth
                            sx={{ marginBottom: 3 }}
                        />
                        <TextField
                            label="Mô tả thể loại da"
                            name='description'
                            variant="outlined"
                            fullWidth
                            sx={{ marginBottom: 3 }}
                        />
                        <Button
                            type="submit"
                            variant="contained"
                            color="primary"
                            sx={{ width: "100%" }}
                            disabled={loading}
                        >
                            {loading ? "Đang tạo mới thể loại loại da..." : "Tạo mới thể loại loại da"}
                        </Button>
                    </form>
                </Paper>
            </Box>
        </>
    )
}