"use client";

import React, { useContext, useEffect, useState } from "react";
import { useParams } from "next/navigation";
import {
    Box,
    Container,
    Typography,
    Card,
    CardContent,
    Chip,
    Divider,
    Switch,
    Button,
} from "@mui/material";
import { ProfileAdminContext } from "@/app/admin/layout";

export default function DetailBlogCategoryAdminPage() {
    const dataProfile = useContext(ProfileAdminContext);
    const permissions = dataProfile?.permissions;
    const { id } = useParams();
    const [categoryInfo, setCategoryInfo] = useState({
        title: "",
        description: "",
        position: 0,
        featured: false,
        status: "ACTIVE",
        image: [],
        createdAt: ""
    });
    const [showFullDescription, setShowFullDescription] = useState(false);


    useEffect(() => {
        const fetchBrand = async () => {
            const response = await fetch(
                `https://freshskinweb.onrender.com/admin/blogs/category/${id}`
            );
            const data = await response.json();
            setCategoryInfo(data.data);
        };

        fetchBrand();
    }, [id]);

    return (
        <Container maxWidth="md" sx={{ mt: 4 }}>
            {permissions?.includes("blogs-category_view") && (
                <Card elevation={3}>
                    <CardContent>
                        {categoryInfo.image[0] && (
                            <Box
                                sx={{
                                    width: "50%",
                                    aspectRatio: 1 / 1,
                                    backgroundSize: "cover",
                                    backgroundPosition: "center",
                                    backgroundImage: `url(${categoryInfo.image[0]})`,
                                    borderRadius: 2,
                                    mb: 2,
                                    objectFit: "cover",
                                    display: "flex",
                                    justifyContent: "center",
                                    alignItems: "center",
                                    margin: "0 auto",
                                }}
                            />
                        )}
                        <Typography
                            variant="h4"
                            component="h1"
                            gutterBottom
                            sx={{ fontWeight: "bold", textAlign: "center" }}
                        >
                            {categoryInfo.title}
                        </Typography>
                        <Box sx={{ textAlign: "center", mb: 2 }}>
                            <Chip
                                label={categoryInfo.status === "ACTIVE" ? "Đang hoạt động" : "Không hoạt động"}
                                color={categoryInfo.status === "ACTIVE" ? "success" : "error"}
                                sx={{ fontSize: "1rem", fontWeight: "bold" }}
                            />
                        </Box>

                        <Divider sx={{ my: 2 }} />
                        <Box
                            sx={{
                                mb: 4,
                                display: "flex",
                                alignItems: "center",
                                justifyContent: "space-between",
                            }}
                        >
                            <Box sx={{ display: "flex", alignItems: "center" }}>
                                <Typography
                                    variant="body1"
                                    sx={{ fontWeight: "bold", color: "#555", mr: 1 }}
                                >
                                    Nổi bật:
                                </Typography>
                                <Switch checked={categoryInfo.featured} disabled />
                                <Typography
                                    variant="body1"
                                    sx={{
                                        color: categoryInfo.featured ? "#4caf50" : "#f44336",
                                        fontWeight: "bold",
                                    }}
                                >
                                    {categoryInfo.featured ? "Có" : "Không"}
                                </Typography>
                            </Box>
                            <Button
                                variant="outlined"
                                sx={{
                                    bgcolor: "#e3f2fd",
                                    color: "#2196f3",
                                    borderRadius: "20px",
                                    fontWeight: "bold",
                                    textTransform: "none",
                                    px: 2,
                                    "&:hover": {
                                        bgcolor: "#bbdefb",
                                    },
                                }}
                            >
                                Vị trí: {categoryInfo.position}
                            </Button>
                        </Box>
                        <Box sx={{ mb: 2 }}>
                            <Box sx={{ display: "flex", alignItems: "center" }}>
                                <Typography variant="body1" sx={{ fontWeight: "bold", color: "#555", mr: 1 }}>
                                    Ngày tạo:
                                </Typography>
                                <Typography variant="body1" sx={{ color: "#555" }}>
                                    {categoryInfo.createdAt}
                                </Typography>
                            </Box>
                        </Box>
                        <Divider sx={{ my: 2 }} />
                        <Box
                            sx={{
                                mb: 4,
                                p: 3,
                                borderRadius: 2,
                                backgroundColor: "#f5f5f5",
                                boxShadow: "0px 4px 8px rgba(0, 0, 0, 0.1)",
                            }}
                        >
                            <Typography
                                variant="h5"
                                gutterBottom
                                sx={{
                                    fontWeight: "bold",
                                    textAlign: "center",
                                    color: "#333",
                                    mb: 2,
                                }}
                            >
                                Mô tả
                            </Typography>
                            <div
                                dangerouslySetInnerHTML={{
                                    __html: showFullDescription
                                        ? categoryInfo.description
                                        : categoryInfo.description.slice(0, 800) + (categoryInfo.description.length > 800 ? "..." : ""),
                                }}
                                style={{
                                    fontSize: "1rem",
                                    lineHeight: 1.8,
                                    color: "#555",
                                    textAlign: "justify",
                                }}
                            />
                            {categoryInfo.description.length > 800 && (
                                <Box sx={{ mt: 2, textAlign: "center" }}>
                                    <Button
                                        variant="text"
                                        onClick={() => setShowFullDescription(!showFullDescription)}
                                        sx={{
                                            fontWeight: "bold",
                                            textTransform: "none",
                                            mx: 1,
                                        }}
                                    >
                                        {showFullDescription ? "Rút gọn" : "Xem thêm"}
                                    </Button>
                                </Box>
                            )}
                        </Box>
                    </CardContent>
                </Card>
            )}
        </Container>
    );
}
