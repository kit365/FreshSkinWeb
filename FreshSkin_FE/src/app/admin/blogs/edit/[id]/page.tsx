"use client"
import dynamic from 'next/dynamic';

const TinyEditor = dynamic(() => import("../../../../../../TinyEditor"), {
    ssr: false
});

import React, { useContext, useEffect, useState } from 'react';
import { Box, Typography, TextField, FormControl, Button, Paper, RadioGroup, FormControlLabel, Radio, MenuItem, InputLabel, Select } from '@mui/material';
import UploadImage from '@/app/components/Upload/UploadImage';
import { useParams } from 'next/navigation';
import { ProfileAdminContext } from '@/app/admin/layout';
import Alert from '@mui/material/Alert';

export default function EditBlogAdminPage() {
    const dataProfile = useContext(ProfileAdminContext);
    const permissions = dataProfile?.permissions;
    const { id } = useParams();
    const [content, setContent] = useState('');
    const [categoryCurrent, setCategoryCurrent] = useState('');
    const [listCategory, setListCategory] = useState([]);
    const [alertMessage, setAlertMessage] = useState<string>("");
    const [alertSeverity, setAlertSeverity] = useState<"success" | "error" | "info" | "warning">("info");
    const [data, setData] = useState({
        title: "",
        status: "ACTIVE",
        featured: false,
        thumbnail: []
    });
    const [loading, setLoading] = useState(false);


    useEffect(() => {
        const fetchCategories = async () => {
            const response = await fetch('https://freshskinweb.onrender.com/admin/blogs/category/show');
            const data = await response.json();
            setListCategory(data.data);
        };

        const fetchDataBlog = async () => {
            const response = await fetch(`https://freshskinweb.onrender.com/admin/blogs/${id}`);
            const data = await response.json();
            setData(data.data);
            setContent(data.data.content);
            setCategoryCurrent(data.data.blogCategory.id);
        };

        fetchCategories();
        fetchDataBlog();
    }, []);

    const handleChangeFeatured = (event: any) => {
        setData({ ...data, featured: event.target.value === "true" });
    };

    const handleChangeCategory = (event: any) => {
        setCategoryCurrent(event.target.value);
    };

    const [images, setImages] = useState<(File)[]>([]);

    const handleImageChange = (newImages: (File)[]) => {
        setImages(newImages);
    };

    const handleRemoveDefaultImage = (index: number) => {
        setData(prevData => ({
            ...prevData,
            thumbnail: prevData.thumbnail.filter((_, i) => i !== index)
        }));
    };

    const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        setLoading(true);

        const formData = new FormData(event.currentTarget);

        if (!formData.get("title")) {
            setAlertMessage("Tiêu đề bài viết không được để trống.");
            setAlertSeverity("error");
            setTimeout(() => setAlertMessage(""), 5000);
            setLoading(false);
            return;
        }

        if (!categoryCurrent) {
            setAlertMessage("Danh mục bài viết không được để trống.");
            setAlertSeverity("error");
            setTimeout(() => setAlertMessage(""), 5000);
            setLoading(false);
            return;
        }

        if ((images.length + data.thumbnail.length) < 1) {
            setAlertMessage("Phải chọn tối thiểu 1 ảnh.");
            setAlertSeverity("error");
            setTimeout(() => setAlertMessage(""), 5000);
            setLoading(false);
            return;
        }

        if (!content) {
            setAlertMessage("Nội dung bài viết không được để trống.");
            setAlertSeverity("error");
            setTimeout(() => setAlertMessage(""), 5000);
            setLoading(false);
            return;
        }

        const request = {
            title: formData.get("title"),
            content: content,
            user: dataProfile?.userID,
            thumbnail: data.thumbnail,
            featured: formData.get("featured") === "true",
            categoryID: categoryCurrent
        };

        formData.append("request", JSON.stringify(request));

        images.forEach((image) => {
            if (image instanceof File) {
                formData.append("newImg", image);
            }
        });

        const response = await fetch(`https://freshskinweb.onrender.com/admin/blogs/edit/${id}`, {
            method: "PATCH",
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
        }
    };

    return (
        <>
            {alertMessage && (
                <Alert severity={alertSeverity} sx={{ position: "fixed", width: "600px", height: "60px", right: "5%", top: "5%", fontSize: "16px", zIndex: "999999" }}>
                    {alertMessage}
                </Alert>
            )}
            <Box sx={{ padding: 3, backgroundColor: '#F5F5F5' }}>
                <Typography variant="h5" gutterBottom>
                    Trang tạo mới bài viết
                </Typography>

                {permissions?.includes("blogs_edit") && (
                    <Paper elevation={3} sx={{ padding: 3, marginBottom: 2 }}>
                        <form onSubmit={handleSubmit}>
                            <TextField
                                label="Tiêu đề bài viết"
                                name='title'
                                variant="outlined"
                                fullWidth
                                sx={{ marginBottom: 3 }}
                                value={data.title}
                                onChange={(e) => setData({ ...data, title: e.target.value })}
                            />
                            <FormControl fullWidth variant="outlined" sx={{ marginBottom: 3 }}>
                                <InputLabel shrink={true}>-- Chọn danh mục --</InputLabel>
                                <Select
                                    value={categoryCurrent}
                                    onChange={handleChangeCategory}
                                    label=" Chọn danh mục --"
                                    displayEmpty
                                >
                                    <MenuItem value=''>
                                        -- Chọn danh mục --
                                    </MenuItem>
                                    {listCategory.map((item: any, index: number) => (
                                        <MenuItem key={index} value={item.id}>{item.title}</MenuItem>
                                    ))}
                                </Select>
                            </FormControl>
                            <FormControl fullWidth sx={{ marginBottom: 3 }}>
                                <RadioGroup
                                    value={data.featured.toString()}
                                    name="featured"
                                    onChange={handleChangeFeatured}
                                    row
                                >
                                    <FormControlLabel value="true" control={<Radio />} label="Nổi bật" />
                                    <FormControlLabel value="false" control={<Radio />} label="Không nổi bật" />
                                </RadioGroup>
                            </FormControl>
                            <UploadImage
                                label="Chọn ảnh"
                                id="images"
                                name="images"
                                onImageChange={handleImageChange}
                                defaultImages={data.thumbnail}
                                onRemoveDefaultImage={handleRemoveDefaultImage}
                            />
                            <h4>Nội dung bài viết</h4>
                            <TinyEditor value={content} onEditorChange={(content: string) => setContent(content)} />
                            <Button type='submit' variant="contained" color="primary" sx={{ width: '100%' }} disabled={loading}>
                                {loading ? "Đang cập nhật bài viết..." : "Chỉnh sửa bài viết"}
                            </Button>
                        </form>
                    </Paper>
                )}
            </Box>
        </>
    );
}
