"use client"

import { useContext, useEffect, useState } from "react";
import { Box, Typography, TextField, FormControl, Button, Paper, RadioGroup, FormControlLabel, Radio, InputLabel, Select, MenuItem } from '@mui/material';
import UploadImage from "@/app/components/Upload/UploadImage";
import { ProfileAdminContext } from "@/app/admin/layout";
import Alert from '@mui/material/Alert';
import { useParams } from "next/navigation";
export default function EditAccountAdmin() {
    const { id } = useParams();
    const dataProfile = useContext(ProfileAdminContext);
    const permissions = dataProfile?.permissions;
    const [listRoles, setListRoles] = useState([]);
    const [data, setData] = useState({
        firstName: "",
        lastName: "",
        email: "",
        phone: "",
        username: "",
        address: "",
        avatar: []
    })
    const [roleCurrent, setRoleCurrent] = useState(0);
    const [loading, setLoading] = useState(false);
    const [alertMessage, setAlertMessage] = useState<string>("");
    const [alertSeverity, setAlertSeverity] = useState<"success" | "error" | "info" | "warning">("info");

    useEffect(() => {
        const fetchRoles = async () => {
            const response = await fetch('https://freshskinweb.onrender.com/admin/roles');
            const data = await response.json();
            setListRoles(data.data);
        };

        const fetchData = async () => {
            const response = await fetch(`https://freshskinweb.onrender.com/admin/account/${id}`);
            const data = await response.json();
            setRoleCurrent(data.data.role.roleId)
            setData(data.data);
        }

        fetchRoles();
        fetchData();
    }, []);

    // Upload ảnh
    const [images, setImages] = useState<(File)[]>([]);

    const handleImageChange = (newImages: (File)[]) => {
        setImages(newImages);
    };

    const handleRemoveDefaultImage = (index: number) => {
        setData(prevData => ({
            ...prevData,
            avatar: prevData.avatar.filter((_, i) => i !== index)
        }));
    };


    const handleChangeRole = (event: any) => {
        setRoleCurrent(event.target.value);
    };

    const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        setLoading(true);

        if((event.currentTarget.phone.value[0]) !== '0'){
            setAlertMessage("Số điện thoại không hợp lệ.");
            setAlertSeverity("error");
            setTimeout(() => setAlertMessage(""), 5000);
            setLoading(false);
            return;
        }

        if((event.currentTarget.phone.value).length !== 10){
            setAlertMessage("Số điện thoại không hợp lệ.");
            setAlertSeverity("error");
            setTimeout(() => setAlertMessage(""), 5000);
            setLoading(false);
            return;
        }

        const formData = new FormData(event.currentTarget);
        if (!roleCurrent) {
            setAlertMessage("Phân quyền tài khoản không được để trống.");
            setAlertSeverity("error");
            setTimeout(() => setAlertMessage(""), 5000);
            setLoading(false);
            return;
        }

        if (!formData.get("firstname")) {
            setAlertMessage("Họ người dùng không được để trống.");
            setAlertSeverity("error");
            setTimeout(() => setAlertMessage(""), 5000);
            setLoading(false);
            return;
        }

        if (!formData.get("lastname")) {
            setAlertMessage("Tên người dùng không được để trống.");
            setAlertSeverity("error");
            setTimeout(() => setAlertMessage(""), 5000);
            setLoading(false);
            return;
        }

        if (!formData.get("email")) {
            setAlertMessage("Email tài khoản không được để trống.");
            setAlertSeverity("error");
            setTimeout(() => setAlertMessage(""), 5000);
            setLoading(false);
            return;
        }

        if (!formData.get("phone")) {
            setAlertMessage("Số điện thoại tài khoản không được để trống.");
            setAlertSeverity("error");
            setTimeout(() => setAlertMessage(""), 5000);
            setLoading(false);
            return;
        }

        if (!formData.get("phone")) {
            setAlertMessage("Số điện thoại tài khoản không được để trống.");
            setAlertSeverity("error");
            setTimeout(() => setAlertMessage(""), 5000);
            setLoading(false);
            return;
        }

        if (!formData.get("address")) {
            setAlertMessage("Địa chỉ tài khoản không được để trống.");
            setAlertSeverity("error");
            setTimeout(() => setAlertMessage(""), 5000);
            setLoading(false);
            return;
        }

        if ((images.length + data.avatar.length) != 1) {
            setAlertMessage("Phải chọn 1 ảnh đại diện.");
            setAlertSeverity("error");
            setTimeout(() => setAlertMessage(""), 5000);
            setLoading(false);
            return;
        }

        const request = {
            role: roleCurrent,
            firstName: formData.get("firstname"),
            lastName: formData.get("lastname"),
            email: formData.get("email"),
            phone: formData.get("phone"),
            avatar: data.avatar,
            address: formData.get("address")
        };

        formData.append("request", JSON.stringify(request));

        images.forEach((image) => {
            if (image instanceof File) {
                formData.append("newImg", image);
            }
        });

        const response = await fetch(`https://freshskinweb.onrender.com/admin/account/edit/${id}`, {
            method: "PATCH",
            body: formData
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
            {permissions?.includes("accounts_edit") && (
                <Box sx={{ padding: 3, backgroundColor: "#e3f2fd" }}>
                    <Typography variant="h5" gutterBottom>
                        Trang chỉnh sửa tài khoản quản trị
                    </Typography>

                    <Paper elevation={3} sx={{ padding: 3, marginBottom: 2 }}>
                        <form onSubmit={handleSubmit}>
                            <FormControl fullWidth variant="outlined" sx={{ marginBottom: 3 }}>
                                <InputLabel shrink={true}>-- Chọn nhóm quyền --</InputLabel>
                                <Select
                                    value={roleCurrent}
                                    onChange={handleChangeRole}
                                    label=" Chọn nhóm quyền --"
                                    displayEmpty
                                >
                                    {listRoles && listRoles.length > 0 && listRoles.map((item: any, index: number) => (
                                        <MenuItem key={index} value={item.roleId}>{item.title}</MenuItem>
                                    ))}

                                </Select>
                            </FormControl>
                            <TextField
                                label="Họ"
                                name="firstname"
                                variant="outlined"
                                fullWidth
                                sx={{ marginBottom: 3 }}
                                value={data.firstName}
                                onChange={(e) =>
                                    setData({ ...data, firstName: e.target.value })
                                }
                            />
                            <TextField
                                label="Tên"
                                name="lastname"
                                variant="outlined"
                                fullWidth
                                sx={{ marginBottom: 3 }}
                                value={data.lastName}
                                onChange={(e) =>
                                    setData({ ...data, lastName: e.target.value })
                                }
                            />
                            <TextField
                                label="Email"
                                name="email"
                                variant="outlined"
                                fullWidth
                                sx={{ marginBottom: 3 }}
                                value={data.email}
                                onChange={(e) =>
                                    setData({ ...data, email: e.target.value })
                                }
                            />
                            <TextField
                                label="Số điện thoại"
                                name="phone"
                                variant="outlined"
                                fullWidth
                                sx={{ marginBottom: 3 }}
                                value={data.phone}
                                onChange={(e) =>
                                    setData({ ...data, phone: e.target.value })
                                }
                            />
                            <TextField
                                label="Địa chỉ"
                                name="address"
                                variant="outlined"
                                fullWidth
                                sx={{ marginBottom: 3 }}
                                value={data.address}
                                onChange={(e) =>
                                    setData({ ...data, address: e.target.value })
                                }
                            />
                            <UploadImage
                                label="Chỉnh sửa hình ảnh"
                                id="upload-images"
                                name="images"
                                defaultImages={data.avatar}
                                onImageChange={handleImageChange}
                                onRemoveDefaultImage={handleRemoveDefaultImage}
                            />
                            <Button
                                type='submit'
                                variant="contained"
                                color="primary"
                                sx={{ width: '100%' }}
                                disabled={loading}
                            >
                                {loading ? "Cập nhật tài khoản..." : "Chỉnh sửa tài khoản"}
                            </Button>
                        </form>
                    </Paper>
                </Box>
            )}
        </>
    )
}