"use client";

import { Box, Button, Paper, TextField, Typography } from "@mui/material";
import { useContext, useEffect, useState } from "react";
import dynamic from "next/dynamic";
import { ProfileAdminContext } from "../../layout";
import Alert from '@mui/material/Alert';
const TinyEditor = dynamic(() => import('../../../../../TinyEditor'), {
    ssr: false
});

export default function SettingGeneralAdminPage() {
    const dataProfile = useContext(ProfileAdminContext);
    const [alertMessage, setAlertMessage] = useState<string>("");
    const [alertSeverity, setAlertSeverity] = useState<"success" | "error" | "info" | "warning">("info");
    const permissions = dataProfile?.permissions;
    const [data, setData] = useState({
        websiteName: '',
        logo: '',
        phone: '',
        email: '',
        address: '',
        copyright: '',
        facebook: '',
        twitter: '',
        youtube: '',
        instagram: '',
        policy1: '',
        policy2: '',
        policy3: '',
        policy4: '',
        policy5: '',
        policy6: '',
        support1: '',
        support2: '',
        support3: '',
        support4: '',
        support5: '',
        support6: ''
    });
    
    useEffect(() => {
        const fetchSettings = async () => {
            const response = await fetch('https://freshskinweb.onrender.com/setting/show');
            const data = await response.json();
            setData(data.data[0]);
        };

        fetchSettings();
    }, []);

    const handleChange = (event: any) => {
        const { name, value } = event.target;
        setData({ ...data, [name]: value });
    };

    const handleSubmit = async (event: any) => {
        event.preventDefault();

        const response = await fetch('https://freshskinweb.onrender.com/setting/edit/1', {
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
    };

    return (
        <>
        {
        alertMessage && (
            <Alert severity={alertSeverity} sx={{ mb: 2 }}>
                {alertMessage}
            </Alert>
        )
    }
            <Box p={3} sx={{ padding: 3, backgroundColor: '#ffffff' }}>
                <Typography variant="h5" gutterBottom>
                    Trang cài đặt chung
                </Typography>
                {permissions?.includes("settings_edit") && permissions.includes("settings_view") && (
                    <Paper elevation={3} sx={{ padding: 3, marginBottom: 2 }}>
                        <form onSubmit={handleSubmit}>
                            <TextField
                                fullWidth
                                margin="normal"
                                label="Tên website"
                                name="websiteName"
                                value={data.websiteName}
                                onChange={handleChange}
                            />
                            <TextField
                                fullWidth
                                margin="normal"
                                label="Logo"
                                name="logo"
                                value={data.logo}
                                onChange={handleChange}
                            />
                            <TextField
                                fullWidth
                                margin="normal"
                                label="Số điện thoại"
                                name="phone"
                                value={data.phone}
                                onChange={handleChange}
                            />
                            <TextField
                                fullWidth
                                margin="normal"
                                label="Email"
                                name="email"
                                type="email"
                                value={data.email}
                                onChange={handleChange}
                            />
                            <TextField
                                fullWidth
                                margin="normal"
                                label="Địa chỉ"
                                name="address"
                                value={data.address}
                                onChange={handleChange}
                            />
                            <TextField
                                fullWidth
                                margin="normal"
                                label="Copyright"
                                name="copyright"
                                value={data.copyright}
                                onChange={handleChange}
                            />
                            <TextField
                                fullWidth
                                margin="normal"
                                label="Facebook"
                                name="facebook"
                                value={data.facebook}
                                onChange={handleChange}
                            />
                            <TextField
                                fullWidth
                                margin="normal"
                                label="Twitter"
                                name="twitter"
                                value={data.twitter}
                                onChange={handleChange}
                            />
                            <TextField
                                fullWidth
                                margin="normal"
                                label="Youtube"
                                name="youtube"
                                value={data.youtube}
                                onChange={handleChange}
                            />
                            <TextField
                                fullWidth
                                margin="normal"
                                label="Instagram"
                                name="instagram"
                                value={data.instagram}
                                onChange={handleChange}
                            />
                            <h4 className="my-4 ml-2 text-[18px]">Chính sách và quy định chung</h4>
                            <TinyEditor value={data.policy1} onEditorChange={(content: string) => setData({ ...data, policy1: content })} />
                            <h4 className="my-4 ml-2 text-[18px]">Chính sách thanh toán</h4>
                            <TinyEditor value={data.policy2} onEditorChange={(content: string) => setData({ ...data, policy2: content })} />
                            <h4 className="my-4 ml-2 text-[18px]">Chính sách giao nhận</h4>
                            <TinyEditor value={data.policy3} onEditorChange={(content: string) => setData({ ...data, policy3: content })} />
                            <h4 className="my-4 ml-2 text-[18px]">Chính sách đổi trả sản phẩm</h4>
                            <TinyEditor value={data.policy4} onEditorChange={(content: string) => setData({ ...data, policy4: content })} />
                            <h4 className="my-4 ml-2 text-[18px]">Chính sách bảo mật thông tin cá nhân</h4>
                            <TinyEditor value={data.policy5} onEditorChange={(content: string) => setData({ ...data, policy5: content })} />
                            <h4 className="my-4 ml-2 text-[18px]">Điều khoản sử dụng</h4>
                            <TinyEditor value={data.policy6} onEditorChange={(content: string) => setData({ ...data, policy6: content })} />

                            <h4 className="my-4 ml-2 text-[18px]">Quyền lợi Fresh-er</h4>
                            <TinyEditor value={data.support1} onEditorChange={(content: string) => setData({ ...data, support1: content })} />
                            <h4 className="my-4 ml-2 text-[18px]">Thông tin thành viên</h4>
                            <TinyEditor value={data.support2} onEditorChange={(content: string) => setData({ ...data, support2: content })} />
                            <h4 className="my-4 ml-2 text-[18px]">Tích điểm đổi quà</h4>
                            <TinyEditor value={data.support3} onEditorChange={(content: string) => setData({ ...data, support3: content })} />
                            <h4 className="my-4 ml-2 text-[18px]">Hỗ trợ kĩ thuật</h4>
                            <TinyEditor value={data.support4} onEditorChange={(content: string) => setData({ ...data, support4: content })} />
                            <h4 className="my-4 ml-2 text-[18px]">Câu hỏi thường gặp</h4>
                            <TinyEditor value={data.support5} onEditorChange={(content: string) => setData({ ...data, support5: content })} />

                            <Button type="submit" variant="contained" color="primary" sx={{ marginTop: 2 }}>
                                Cập nhật
                            </Button>
                        </form>
                    </Paper>
                )}
            </Box>
        </>
    )
}