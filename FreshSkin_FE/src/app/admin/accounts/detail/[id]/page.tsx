"use client";

import { ProfileAdminContext } from "@/app/admin/layout";
import {
  Box,
  Card,
  CardContent,
  Chip,
  Container,
  Divider,
  Typography,
} from "@mui/material";
import { useParams } from "next/navigation";
import { useContext, useEffect, useState } from "react";
export default function DetailAccountAdmin() {
  const dataProfile = useContext(ProfileAdminContext);
  const permissions = dataProfile?.permissions;
  const { id } = useParams();
  const [data, setData] = useState({
    role: {
      title: "",
    },
    firstName: "",
    lastName: "",
    phone: "",
    email: "",
    avatar: "",
    status: "ACTIVE",
    address: "",
    createdAt: "",
  });
  useEffect(() => {
    const fetchAccount = async () => {
      const response = await fetch(
        `https://freshskinweb.onrender.com/admin/account/${id}`
      );
      const data = await response.json();
      setData(data.data);
    };

    fetchAccount();
  }, [id]);
  return (
    <>
      <Container maxWidth="md" sx={{ mt: 4 }}>
        {permissions?.includes("accounts_view") && (
          <Card elevation={3}>
            <CardContent>
              {data.avatar && (
                <Box
                  sx={{
                    width: 120,
                    height: 120,
                    backgroundSize: "cover",
                    backgroundPosition: "center",
                    backgroundImage: `url(${data.avatar})`,
                    borderRadius: "50%",
                    mb: 2,
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
                {data.firstName} {data.lastName}
              </Typography>

              <Box sx={{ textAlign: "center", mb: 2 }}>
                <Chip
                  label={
                    data.status === "ACTIVE"
                      ? "Đang hoạt động"
                      : "Không hoạt động"
                  }
                  color={data.status === "ACTIVE" ? "success" : "error"}
                  sx={{ fontSize: "1rem", fontWeight: "bold" }}
                />
              </Box>

              <Box sx={{ display: "flex", alignItems: "center", mb: 2 }}>
                <Typography
                  variant="h6"
                  sx={{
                    fontWeight: "bold",
                    color: "#fff",
                    backgroundColor: "#1d78d3",
                    padding: "6px 12px",
                    borderRadius: 1,
                    boxShadow: 1,
                    textTransform: "uppercase",
                    mr: 1,
                  }}
                >
                  Nhóm quyền:
                </Typography>
                <Typography variant="body1" sx={{ color: "#333" }}>
                  {data.role.title}
                </Typography>
              </Box>

              <Divider sx={{ my: 2 }} />

              <Box sx={{ mb: 2 }}>
                <Box sx={{ display: "flex", alignItems: "center", mb: 1 }}>
                  <Typography
                    variant="body1"
                    sx={{ fontWeight: "bold", color: "#555", mr: 1 }}
                  >
                    Email:
                  </Typography>
                  <Typography variant="body1" sx={{ color: "#555" }}>
                    {data.email}
                  </Typography>
                </Box>
                <Box sx={{ display: "flex", alignItems: "center", mb: 1 }}>
                  <Typography
                    variant="body1"
                    sx={{ fontWeight: "bold", color: "#555", mr: 1 }}
                  >
                    Số điện thoại:
                  </Typography>
                  <Typography variant="body1" sx={{ color: "#555" }}>
                    {data.phone}
                  </Typography>
                </Box>
                <Box sx={{ display: "flex", alignItems: "center", mb: 1 }}>
                  <Typography
                    variant="body1"
                    sx={{ fontWeight: "bold", color: "#555", mr: 1 }}
                  >
                    Địa chỉ:
                  </Typography>
                  <Typography variant="body1" sx={{ color: "#555" }}>
                    {data.address}
                  </Typography>
                </Box>
                <Box sx={{ display: "flex", alignItems: "center", mb: 1 }}>
                  <Typography
                    variant="body1"
                    sx={{ fontWeight: "bold", color: "#555", mr: 1 }}
                  >
                    Ngày khởi tạo:
                  </Typography>
                  <Typography variant="body1" sx={{ color: "#555" }}>
                    {data.createdAt}
                  </Typography>
                </Box>
              </Box>
              <Divider sx={{ my: 2 }} />
            </CardContent>
          </Card>
        )}
      </Container>
    </>
  );
}
