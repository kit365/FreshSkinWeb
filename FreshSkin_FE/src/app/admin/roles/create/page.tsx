"use client"

import { Box, Button, Paper, TextField, Typography } from "@mui/material";
import dynamic from 'next/dynamic';
import { useContext, useState } from "react";
import { ProfileAdminContext } from "../../layout";
import Alert from '@mui/material/Alert';
const TinyEditor = dynamic(() => import('../../../../../TinyEditor'), {
    ssr: false
});

interface Data {
    title: string,
    description: string
}

export default function CreateRoleAdmin() {
    const dataProfile = useContext(ProfileAdminContext);
    const permissions = dataProfile?.permissions;
    const [alertMessage, setAlertMessage] = useState<string>("");
    const [alertSeverity, setAlertSeverity] = useState<"success" | "error" | "info" | "warning">("info");
    const [content, setContent] = useState('');

    const handleSubmit = async (event: any) => {
        event.preventDefault();

        const data: Data = {
            title: event.target.title.value,
            description: content
        }

        const response = await fetch(`https://freshskinweb.onrender.com/admin/roles/create`, {
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
        }
    }
   
    return (
        <>
{
        alertMessage && (
            <Alert severity={alertSeverity} sx={{ mb: 2 }}>
                {alertMessage}
            </Alert>
        )
    }
            <Box sx={{ padding: 3, backgroundColor: '#ffffff' }}>
                <Typography variant="h5" gutterBottom>
                    Trang tạo mới nhóm quyền
                </Typography>

                {permissions?.includes("roles_create") && (
                    <Paper elevation={3} sx={{ padding: 3, marginBottom: 2 }}>
                        <form onSubmit={handleSubmit}>
                            <TextField
                                label="Tiêu đề nhóm quyền"
                                name='title'
                                variant="outlined"
                                fullWidth
                                sx={{ marginBottom: 3 }}
                                required
                            />
                            <h4>Mô tả nhóm quyền</h4>
                            <TinyEditor value={content} onEditorChange={(content: string) => setContent(content)} />
                            <Button type='submit' variant="contained" color="primary" sx={{ width: '100%' }}>
                                Tạo mới
                            </Button>
                        </form>
                    </Paper>
                )}
            </Box >
        </>
    )
}