"use client";

import React, { useState } from 'react';
import { TextField, Button, Box, Typography, Alert } from '@mui/material';
import { useRouter } from 'next/navigation';
import Cookies from 'js-cookie';


export default function LoginAdminPage() {
    const [alertMessage, setAlertMessage] = useState<string>("");
    const [alertSeverity, setAlertSeverity] = useState<
        "success" | "error" | "info" | "warning"
    >("info");
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');

    const router = useRouter();

    const handleSubmit = async (event: any) => {
        event.preventDefault();

        const response = await fetch('https://freshskinweb.onrender.com/auth/login-admin', {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            credentials: 'include',
            body: JSON.stringify({
                username: username,
                password: password
            })
        });
        const dataResponse = await response.json();

        if (dataResponse.code === 200) {
            const token = dataResponse.data.token;
            Cookies.set('token', token);
            setAlertMessage(dataResponse.message);
            setAlertSeverity("success");
            location.href = `/admin/dashboard`;
        } else {
            setAlertMessage(dataResponse.message);
            setAlertSeverity("error");
        }

    };
    return (
        <Box
            sx={{
                display: 'flex',
                flexDirection: 'column',
                justifyContent: 'center',
                alignItems: 'center',
                minHeight: '100vh',
                backgroundColor: '#f0f4f8',
                padding: 4,
                borderRadius: 2,
                boxShadow: 3,
            }}
        >
            {alertMessage && (
                <Alert severity={alertSeverity} sx={{ position: "fixed", width: "600px", height: "60px", right: "5%", top: "5%", fontSize: "16px", zIndex: "999999" }}>
                    {alertMessage}
                </Alert>
            )}
            <Typography variant="h4" gutterBottom sx={{ fontWeight: 'bold', color: '#333' }}>
                Đăng Nhập
            </Typography>
            <form onSubmit={handleSubmit} style={{ width: '100%', maxWidth: '400px' }}>
                <TextField
                    label="Tên người dùng"
                    variant="outlined"
                    fullWidth
                    margin="normal"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                    sx={{
                        '& .MuiOutlinedInput-root': {
                            '& fieldset': {
                                borderColor: '#1976d2',
                            },
                            '&:hover fieldset': {
                                borderColor: '#1565c0',
                            },
                            '&.Mui-focused fieldset': {
                                borderColor: '#0d47a1',
                            },
                        },
                        marginBottom: 1
                    }}
                />
                <TextField
                    label="Mật khẩu"
                    variant="outlined"
                    type="password"
                    fullWidth
                    margin="normal"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    sx={{
                        '& .MuiOutlinedInput-root': {
                            '& fieldset': {
                                borderColor: '#1976d2',
                            },
                            '&:hover fieldset': {
                                borderColor: '#1565c0',
                            },
                            '&.Mui-focused fieldset': {
                                borderColor: '#0d47a1',
                            },
                        },
                        marginBottom: 2
                    }}
                />
                <Button
                    type="submit"
                    variant="contained"
                    color="primary"
                    fullWidth
                    sx={{
                        marginTop: 1,
                        backgroundColor: '#1976d2',
                        '&:hover': {
                            backgroundColor: '#1565c0',
                        },
                    }}
                >
                    Đăng nhập
                </Button>
            </form>
        </Box>
    );
}