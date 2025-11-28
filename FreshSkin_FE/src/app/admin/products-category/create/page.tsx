"use client"

import dynamic from 'next/dynamic';
const TinyEditor = dynamic(() => import('../../../../../TinyEditor'), {
    ssr: false
});
import React, { useContext, useEffect, useState } from 'react';
import { Box, Typography, TextField, FormControl, Button, Paper, RadioGroup, FormControlLabel, Radio, InputLabel, Select, MenuItem } from '@mui/material';
import UploadImage from '@/app/components/Upload/UploadImage';
import { ProfileAdminContext } from '../../layout';
import Alert from '@mui/material/Alert';
export default function CreateProductCategoryAdminPage() {
    const dataProfile = useContext(ProfileAdminContext);
    const permissions = dataProfile?.permissions;
    const [alertMessage, setAlertMessage] = useState<string>("");
    const [alertSeverity, setAlertSeverity] = useState<"success" | "error" | "info" | "warning">("info");
    const [description, setDescription] = useState('');
    const [categoryCurrent, setCategoryCurrent] = useState(0);
    const [listCategory, setListCategory] = useState([]);
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        const fetchCategories = async () => {
            const response = await fetch('https://freshskinweb.onrender.com/admin/products/category/shows');
            const data = await response.json();
            setListCategory(data.data);
        };

        fetchCategories();
    }, []);

    const [images, setImages] = useState<(File)[]>([]);
    const handleImageChange = (newImages: (File)[]) => {
        setImages(newImages);
    };

    const handleChangeCategory = (event: any) => {
        setCategoryCurrent(event.target.value);
    };

    const handleSubmit = async (event: any) => {
        event.preventDefault();
        setLoading(true);

        if (!event.target.title.value) {
            setAlertMessage("Tên danh mục không được để trống.");
            setAlertSeverity("error");
            setTimeout(() => setAlertMessage(""), 5000);
            setLoading(false);
            return;
        }

        if (event.target.title.value.length < 5) {
            setAlertMessage("Tên danh mục phải trên 5 ký tự.");
            setAlertSeverity("error");
            setTimeout(() => setAlertMessage(""), 5000);
            setLoading(false);
            return;
        }

        if (images.length < 1) {
            setAlertMessage("Phải chọn tối thiểu 1 ảnh.");
            setAlertSeverity("error");
            setTimeout(() => setAlertMessage(""), 5000);
            setLoading(false);
            return;
        }

        if (!description) {
            setAlertMessage("Mô tả danh mục không được để trống.");
            setAlertSeverity("error");
            setTimeout(() => setAlertMessage(""), 5000);
            setLoading(false);
            return;
        }

        const formData = new FormData();

        const request = {
            title: event.target.title.value,
            description: description,
            position: event.target.position.value,
            parentID: categoryCurrent,
            status: event.target.status.value,
            featured: event.target.featured.value === "true",
        };

        formData.append("request", JSON.stringify(request));

        images.forEach((image) => formData.append("thumbnail", image));

        const response = await fetch('https://freshskinweb.onrender.com/admin/products/category/create', {
            method: "POST",
            body: formData
        });

        const dataResponse = await response.json();

        if (dataResponse.code === 200) {
            setAlertMessage(dataResponse.message);
            setAlertSeverity("success");
            setTimeout(() => location.reload(), 1000);
        } else {
            setAlertMessage(dataResponse.message);
            setAlertSeverity("error");
            setTimeout(() => setAlertMessage(""), 5000);
        }
    }


    return (
        <>
            {alertMessage && (
                <Alert severity={alertSeverity} sx={{ position: "fixed", width: "600px", height: "60px", right: "5%", top: "5%", fontSize: "16px", zIndex: "999999" }}>
                    {alertMessage}
                </Alert>
            )}
            {permissions?.includes("products-category_create") && (
                <Box sx={{ padding: 3, backgroundColor: '#ffffff' }}>
                    <Typography variant="h4" gutterBottom>
                        Trang tạo mới danh mục sản phẩm
                    </Typography>

                    <Paper elevation={3} sx={{ padding: 3, marginBottom: 2 }}>
                        <form onSubmit={handleSubmit}>
                            <TextField
                                label="Tên danh mục"
                                name='title'
                                variant="outlined"
                                fullWidth
                                sx={{ marginBottom: 3 }}
                            />
                            <FormControl fullWidth variant="outlined" sx={{ marginBottom: 3 }}>
                                <InputLabel shrink={true}>Danh mục cha</InputLabel>
                                <Select
                                    value={categoryCurrent}
                                    onChange={handleChangeCategory}
                                    label=" Chọn danh mục --"
                                    displayEmpty
                                >
                                    <MenuItem value={0}>
                                        -- Chọn danh mục --
                                    </MenuItem>
                                    {listCategory.map((item: any, index: number) => (
                                        <MenuItem key={index} value={item.id}>{item.title}</MenuItem>
                                    ))}

                                </Select>
                            </FormControl>
                            <FormControl fullWidth sx={{ marginBottom: 3 }}>
                                <RadioGroup
                                    defaultValue={false}
                                    name="featured"
                                    row
                                >
                                    <FormControlLabel value={true} control={<Radio />} label="Nổi bật" />
                                    <FormControlLabel value={false} control={<Radio />} label="Không nổi bật" />
                                </RadioGroup>
                            </FormControl>
                            <UploadImage
                                label="Chọn ảnh"
                                id="images"
                                name="images"
                                onImageChange={handleImageChange}
                            />
                            <h4>Mô tả</h4>
                            <TinyEditor value={description} onEditorChange={(content: string) => setDescription(content)} />
                            <TextField
                                label="Vị trí (tự động tăng)"
                                name='position'
                                variant="outlined"
                                fullWidth
                                type="number"
                                sx={{ marginBottom: 2, marginTop: 2 }}
                            />
                            <FormControl fullWidth sx={{ marginBottom: 3 }}>
                                <RadioGroup
                                    defaultValue="ACTIVE"
                                    name="status"
                                    row
                                >
                                    <FormControlLabel value="ACTIVE" control={<Radio />} label="Hoạt động" />
                                    <FormControlLabel value="INACTIVE" control={<Radio />} label="Dừng hoạt động" />
                                </RadioGroup>
                            </FormControl>
                            <Button type='submit' variant="contained" color="primary" sx={{ width: '100%' }} disabled={loading}>
                                {loading ? "Đang tạo danh mục..." : "Tạo danh mục"}
                            </Button>
                        </form>
                    </Paper>
                </Box >
            )}
        </>
    );
}