"use client";

import { useParams } from "next/navigation";
import { useContext, useEffect, useState } from "react";
import {
    Box,
    Container,
    Typography,
    Card,
    CardContent,
    Chip,
    Switch,
    Button,
    Grid,
} from "@mui/material";
import { ProfileAdminContext } from "@/app/admin/layout";

export default function DetailProductAdminPage() {
    const dataProfile = useContext(ProfileAdminContext);
    const permissions = dataProfile?.permissions;
    const { id } = useParams();

    const [data, setData] = useState({
        benefits: "",
        brand: {
            title: "",
        },
        category: [],
        description: "",
        discountPercent: 0,
        featured: false,
        ingredients: "",
        origin: "",
        position: 0,
        skinIssues: "",
        skinTypes: [],
        slug: "",
        status: "ACTIVE",
        thumbnail: [],
        title: "",
        usageInstructions: "",
        variants: [],
    });

    const [showFullDescription, setShowFullDescription] = useState(false);

    useEffect(() => {
        const fetchInfo = async () => {
            const response = await fetch(`https://freshskinweb.onrender.com/admin/products/${id}`);
            const data = await response.json();
            setData(data.data);
        };

        fetchInfo();
    }, [id]);

    return (
        <>
            {permissions?.includes("products_view") && (
                <Container maxWidth="md" sx={{ mt: 4 }}>
                    <Card elevation={3}>
                        <CardContent>
                            <Box sx={{ mb: 3 }}>
                                <Typography
                                    variant="h4"
                                    component="h1"
                                    gutterBottom
                                    sx={{ fontWeight: "bold", textAlign: "center" }}
                                >
                                    {data.title}
                                </Typography>
                            </Box>
                            <Box
                                sx={{
                                    mb: 4,
                                    display: "flex",
                                    alignItems: "center",
                                    justifyContent: "space-between",
                                }}
                            >
                                <Box sx={{ display: "flex", alignItems: "center" }}>
                                    <Typography variant="body1" sx={{ fontWeight: "bold", color: "#555", mr: 1 }}>
                                        Nổi bật:
                                    </Typography>
                                    <Switch checked={data.featured} disabled />
                                    <Typography
                                        variant="body1"
                                        sx={{
                                            color: data.featured ? "#4caf50" : "#f44336",
                                            fontWeight: "bold",
                                        }}
                                    >
                                        {data.featured ? "Có" : "Không"}
                                    </Typography>
                                </Box>
                                <Chip
                                    label={data.status === "ACTIVE" ? "Đang hoạt động" : "Không hoạt động"}
                                    color={data.status === "ACTIVE" ? "success" : "error"}
                                    sx={{ fontSize: "1rem", fontWeight: "bold", marginRight: "90px" }}
                                />
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
                                    Vị trí: {data.position}
                                </Button>
                            </Box>
                            <Box sx={{ mb: 2 }}>
                                <Box sx={{ display: "flex", alignItems: "center" }}>
                                    <Typography variant="body1" sx={{ fontWeight: "bold", color: "#555", mr: 1 }}>
                                        Thương hiệu:
                                    </Typography>
                                    <Typography variant="body1" sx={{ color: "#555" }}>
                                        {data.brand?.title}
                                    </Typography>
                                </Box>
                            </Box>
                            <Box sx={{ mb: 2 }}>
                                <Typography variant="body1" sx={{ fontWeight: "bold", color: "#555", mb: 1 }}>
                                    Danh mục:
                                </Typography>
                                {data.category && (
                                    <Box sx={{ display: "flex", flexDirection: "column", gap: 0.5 }}>
                                        {data.category.map((item: any, index: number) => (
                                            <Typography
                                                key={index}
                                                variant="body1"
                                                sx={{
                                                    color: "#555",
                                                    display: "flex",
                                                    alignItems: "center",
                                                }}
                                            >
                                                <span style={{ color: "#4caf50", fontWeight: "bold", marginRight: 8 }}>+</span>
                                                {item.title || ""}
                                            </Typography>
                                        ))}
                                    </Box>
                                )}
                            </Box>
                            <Box sx={{ mb: 2 }}>
                                <Typography variant="body1" sx={{ fontWeight: "bold", color: "#555", mb: 1 }}>
                                    Loại da phù hợp:
                                </Typography>
                                <Box sx={{ display: "flex", flexDirection: "column", gap: 0.5 }}>
                                    {data.skinTypes?.map((item: any, index: number) => (
                                        <Typography
                                            key={index}
                                            variant="body1"
                                            sx={{
                                                color: "#555",
                                                display: "flex",
                                                alignItems: "center",
                                            }}
                                        >
                                            <span style={{ color: "#4caf50", fontWeight: "bold", marginRight: 8 }}>+</span>
                                            {item.type || ""}
                                        </Typography>
                                    ))}
                                </Box>
                            </Box>
                            <Box sx={{ mb: 2 }}>
                                <Box sx={{ display: "flex", alignItems: "center" }}>
                                    <Typography variant="body1" sx={{ fontWeight: "bold", color: "#555", mr: 1 }}>
                                        Phần trăm giảm giá:
                                    </Typography>
                                    <Typography variant="body1" sx={{ color: "#555" }}>
                                        {data.discountPercent}%
                                    </Typography>
                                </Box>
                            </Box>
                            <Box sx={{ mb: 3 }}>
                                <Typography variant="body1" sx={{ fontWeight: 'bold', color: '#555', mb: 2 }}>
                                    Dung tích và giá:
                                </Typography>
                                <Grid container spacing={2}>
                                    {data.variants?.map((item: any, index: number) => (
                                        <Grid item xs={12} sm={6} md={4} key={index}>
                                            <Card
                                                sx={{
                                                    display: 'flex',
                                                    flexDirection: 'column',
                                                    alignItems: 'center',
                                                    padding: 2,
                                                    boxShadow: 3,
                                                    borderRadius: '8px',
                                                    backgroundColor: '#f9f9f9',
                                                    '&:hover': {
                                                        boxShadow: 6,
                                                        transform: 'scale(1.05)',
                                                        transition: 'transform 0.3s ease, box-shadow 0.3s ease',
                                                    },
                                                }}
                                            >
                                                <Typography variant="h6" sx={{ fontWeight: 'bold', color: '#4caf50' }}>
                                                    {item.volume}{item.unit}
                                                </Typography>
                                                <Typography variant="body1" sx={{ color: '#555'  }}>
                                                    Giá: {item.price.toLocaleString()} VND
                                                </Typography>
                                                <Typography variant="body1" sx={{ color: '#555', marginBottom: 2 }}>
                                                    Số lượng: {item.stock}
                                                </Typography>
                                            </Card>
                                        </Grid>
                                    ))}
                                </Grid>
                            </Box>
                            <Box sx={{ mb: 2 }}>
                                <Box sx={{ display: "flex", alignItems: "center" }}>
                                    <Typography variant="body1" sx={{ fontWeight: "bold", color: "#555", mr: 1 }}>
                                        Xuất xứ:
                                    </Typography>
                                    <Typography variant="body1" sx={{ color: "#555" }}>
                                        {data.origin}
                                    </Typography>
                                </Box>
                            </Box>
                            <Box sx={{ mb: 2 }}>
                                <Box sx={{ display: "flex", alignItems: "center" }}>
                                    <Typography variant="body1" sx={{ fontWeight: "bold", color: "#555", mr: 1 }}>
                                        Thành phần:
                                    </Typography>
                                    <div
                                        dangerouslySetInnerHTML={{
                                            __html: data.ingredients
                                        }}
                                    />
                                </Box>
                            </Box>
                            <Box sx={{ mb: 2 }}>
                                <Box sx={{ display: "flex", alignItems: "center" }}>
                                    <Typography variant="body1" sx={{ fontWeight: "bold", color: "#555", mr: 1 }}>
                                        Hướng dẫn sử dụng:
                                    </Typography>
                                    <div
                                        dangerouslySetInnerHTML={{
                                            __html: data.usageInstructions
                                        }}
                                    />
                                </Box>
                            </Box>
                            <Box sx={{ mb: 2 }}>
                                <Box sx={{ display: "flex", alignItems: "center" }}>
                                    <Typography variant="body1" sx={{ fontWeight: "bold", color: "#555", mr: 1 }}>
                                        Lợi ích:
                                    </Typography>
                                    <Typography variant="body1" sx={{ color: "#555" }}>
                                        {data.benefits}
                                    </Typography>
                                </Box>
                            </Box>
                            <Box sx={{ mb: 2 }}>
                                <Box sx={{ display: "flex", alignItems: "center" }}>
                                    <Typography variant="body1" sx={{ fontWeight: "bold", color: "#555", mr: 1 }}>
                                        Vấn đề da:
                                    </Typography>
                                    <Typography variant="body1" sx={{ color: "#555" }}>
                                        {data.skinIssues}
                                    </Typography>
                                </Box>
                            </Box>






                            {/* Mô tả */}
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
                                            ? data.description
                                            : data.description.slice(0, 800) + (data.description.length > 800 ? "..." : ""),
                                    }}
                                    style={{
                                        fontSize: "1rem",
                                        lineHeight: 1.8,
                                        color: "#555",
                                        textAlign: "justify",
                                    }}
                                />
                                {data.description.length > 800 && (
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





                            {/* Product Images */}
                            <Grid container spacing={2}>
                                {data.thumbnail?.map((image: any, index: number) => (
                                    <Grid item xs={6} md={4} key={index}>
                                        <img
                                            src={image}
                                            alt={`Product thumbnail ${index + 1}`}
                                            style={{
                                                width: "100%",
                                                borderRadius: "8px",
                                                objectFit: "cover",
                                            }}
                                        />
                                    </Grid>
                                ))}
                            </Grid>
                        </CardContent>
                    </Card>
                </Container>
            )}
        </>
    );
}