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

export default function DetailBlogAdminPage() {
    const dataProfile = useContext(ProfileAdminContext);
    const permissions = dataProfile?.permissions;
    const { id } = useParams();
    const [blogInfo, setBlogInfo] = useState({
        title: "",
        content: "",
        position: 0,
        featured: false,
        status: "ACTIVE",
        blogCategory: {
            title: ""
        },
        thumbnail: [],
        author: ""
    });

    const [showFullContent, setShowFullContent] = useState(false);

    useEffect(() => {
        const fetchBlog = async () => {
            const response = await fetch(
                `https://freshskinweb.onrender.com/admin/blogs/${id}`
            );
            const data = await response.json();
            console.log(data.data);
            setBlogInfo(data.data);
        };

        fetchBlog();
    }, [id]);

    return (
        
        <Container maxWidth="md" sx={{ mt: 4 }}>
            {permissions?.includes("blogs_view") && (
                <Card elevation={3}>
                    <CardContent>
                        {blogInfo.thumbnail[0] && (
                            <Box
                                sx={{
                                    width: "50%",
                                    aspectRatio: 1 / 1,
                                    backgroundSize: "cover",
                                    backgroundPosition: "center",
                                    backgroundImage: `url(${blogInfo.thumbnail[0]})`,
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
                            {blogInfo.blogCategory.title}
                        </Typography>

                        {/* Status */}
                        <Box sx={{ textAlign: "center", mb: 2 }}>
                            <Chip
                                label={blogInfo.status === "ACTIVE" ? "Đang hoạt động" : "Không hoạt động"}
                                color={blogInfo.status === "ACTIVE" ? "success" : "error"}
                                sx={{ fontSize: "1rem", fontWeight: "bold" }}
                            />
                        </Box>

                        <Divider sx={{ my: 2 }} />

                        {/* Position and Featured */}
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
                                <Switch checked={blogInfo.featured} disabled />
                                <Typography
                                    variant="body1"
                                    sx={{
                                        color: blogInfo.featured ? "#4caf50" : "#f44336",
                                        fontWeight: "bold",
                                    }}
                                >
                                    {blogInfo.featured ? "Có" : "Không"}
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
                                Vị trí: {blogInfo.position}
                            </Button>
                        </Box>

                        <Box sx={{ mb: 2 }}>
                            <Box sx={{ display: "flex", alignItems: "center" }}>
                                <Typography variant="body1" sx={{ fontWeight: "bold", color: "#555", mr: 1 }}>
                                    Danh mục bài viết:
                                </Typography>
                                <Typography variant="body1" sx={{ color: "#555" }}>
                                    {blogInfo.blogCategory.title}
                                </Typography>
                            </Box>
                        </Box>

                        <Box sx={{ mb: 2 }}>
                            <Box sx={{ display: "flex", alignItems: "center" }}>
                                <Typography variant="body1" sx={{ fontWeight: "bold", color: "#555", mr: 1 }}>
                                    Tác giả:
                                </Typography>
                                <Typography variant="body1" sx={{ color: "#555" }}>
                                    {blogInfo.author}
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
                                    __html: showFullContent
                                        ? blogInfo.content
                                        : blogInfo.content.slice(0, 800) + (blogInfo.content.length > 800 ? "..." : ""),
                                }}
                                style={{
                                    fontSize: "1rem",
                                    lineHeight: 1.8,
                                    color: "#555",
                                    textAlign: "justify",
                                }}
                            />
                            {blogInfo.content.length > 800 && (
                                <Box sx={{ mt: 2, textAlign: "center" }}>
                                    <Button
                                        variant="text"
                                        onClick={() => setShowFullContent(!showFullContent)}
                                        sx={{
                                            fontWeight: "bold",
                                            textTransform: "none",
                                            mx: 1,
                                        }}
                                    >
                                        {showFullContent ? "Rút gọn" : "Xem thêm"}
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