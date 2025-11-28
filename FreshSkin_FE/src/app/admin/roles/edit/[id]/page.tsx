"use client"

import { ProfileAdminContext } from "@/app/admin/layout";
import { Box, Button, Paper, TextField, Typography } from "@mui/material";
import dynamic from 'next/dynamic';
import { useParams } from "next/navigation";
import Alert from '@mui/material/Alert';
import { useContext, useEffect, useState } from "react";
const TinyEditor = dynamic(() => import('../../../../../../TinyEditor'), {
    ssr: false
});

interface Data {
    title: string,
    description: string
}

export default function EditRoleAdmin() {
    const dataProfile = useContext(ProfileAdminContext);
    const permissions = dataProfile?.permissions;
    const [description, setDescription] = useState('');
    const { id } = useParams();
    const [alertMessage, setAlertMessage] = useState<string>("");
    const [alertSeverity, setAlertSeverity] = useState<"success" | "error" | "info" | "warning">("info");
    const [roleInfo, setRoleInfo] = useState({
        title: "",
        position: 0,
        featured: false,
        status: "ACTIVE",
    });

    useEffect(() => {
        const fetchBrand = async () => {
            const response = await fetch(
                `https://freshskinweb.onrender.com/admin/roles/${id}`
            );
            const data = await response.json();
            setRoleInfo(data.data);
            setDescription(data.data.description);
        };

        fetchBrand();
    }, []);

 
    const handleSubmit = async (event: any) => {
        event.preventDefault();

        const data: Data = {
            title: event.target.title.value,
            description: description
        }

        const response = await fetch(`https://freshskinweb.onrender.com/admin/roles/edit/${id}`, {
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

    return (
        <>
        {
            alertMessage && (
                <Alert severity={alertSeverity} sx={{ mb: 2 }}>
                    {alertMessage}
                </Alert>
            )
        }
            <Box sx={{ padding: 3, backgroundColor: '#e3f2fd' }}>
                <Typography variant="h4" gutterBottom>
                    Trang chỉnh sửa nhóm quyền
                </Typography>

                {permissions?.includes("roles_edit") && (
                    <Paper elevation={3} sx={{ padding: 3, marginBottom: 2 }}>
                        <form onSubmit={handleSubmit}>
                            <TextField
                                label="Tiêu đề nhóm quyền"
                                name='title'
                                variant="outlined"
                                fullWidth
                                sx={{ marginBottom: 3 }}
                                required
                                value={roleInfo.title}
                                onChange={(e) =>
                                    setRoleInfo({ ...roleInfo, title: e.target.value })
                                }
                            />
                            <h4>Mô tả nhóm quyền</h4>
                            <TinyEditor value={description} onEditorChange={(description: string) => setDescription(description)} />
                            <Button type='submit' variant="contained" color="primary" sx={{ width: '100%' }}>
                                Cập nhật
                            </Button>
                        </form>
                    </Paper>
                )}
            </Box >
        </>
    )
}