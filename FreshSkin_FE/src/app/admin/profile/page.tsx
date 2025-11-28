"use client";

import React, { useContext} from "react";
import { ProfileAdminContext } from "../layout";
import {
  Card,
  CardContent,
  Typography,
  Button,
  Avatar,
  Grid,
  Box,
  Divider,
} from "@mui/material";
import EditIcon from "@mui/icons-material/Edit";
import Link from "next/link";
export default function ProfileAdmin() {
  const dataProfile = useContext(ProfileAdminContext);
  
 

  return (
    <Box
      sx={{
        padding: 3,
        display: "flex",
        justifyContent: "center",
        backgroundColor: "#f4f6f8",
      }}
    >
      <Card
        sx={{
          maxWidth: 700,
          width: "100%",
          borderRadius: 2,
          boxShadow: 3,
          backgroundColor: "#ffffff",
          overflow: "hidden",
        }}
      >
        <CardContent>
          <Grid container spacing={4} alignItems="center">
            <Grid item xs={12} sm={4} display="flex" justifyContent="center">
              <Avatar
                src={dataProfile?.avatar}
                sx={{
                  width: 120,
                  height: 120,
                  borderRadius: "50%",
                  border: "5px solid #3f51b5",
                  transition: "transform 0.3s ease-in-out",
                  "&:hover": {
                    transform: "scale(1.1)",
                  },
                }}
              />
            </Grid>
            <Grid item xs={12} sm={8}>
              <Typography
                variant="h5"
                fontWeight={600}
                color="primary"
                gutterBottom
              >
                {dataProfile?.roleTitle}
              </Typography>
              <Typography variant="h4" gutterBottom fontWeight={600}>
                {dataProfile?.firstName} {dataProfile?.lastName}
              </Typography>
              <Typography
                variant="body1"
                color="text.secondary"
                fontWeight={500}
              >
                Email:{" "}
                <span style={{ color: "#3f51b5" }}>{dataProfile?.email}</span>
              </Typography>
              <Typography
                variant="body1"
                color="text.secondary"
                fontWeight={500}
              >
                Phone:{" "}
                <span style={{ color: "#ff6f61" }}>{dataProfile?.phone}</span>
              </Typography>
              <Typography
                variant="body1"
                color="text.secondary"
                fontWeight={500}
              >
                Address:{" "}
                <span style={{ color: "#8e24aa" }}>{dataProfile?.address}</span>
              </Typography>
            </Grid>
          </Grid>
          <Divider sx={{ my: 2 }} />
          <Box sx={{ textAlign: "right" }}>
            <Link href="/admin/profile/edit">
              <Button
                variant="contained"
                color="primary"
                startIcon={<EditIcon />}
                sx={{
                  textTransform: "none",
                  padding: "8px 16px",
                  fontWeight: 600,
                  borderRadius: 2,
                  boxShadow: 2,
                  transition: "all 0.3s ease-in-out",
                  "&:hover": {
                    backgroundColor: "#3f51b5",
                    boxShadow: 4,
                    transform: "scale(1.05)",
                  },
                }}
              >
                Chỉnh sửa thông tin cá nhân
              </Button>
            </Link>
          </Box>
        </CardContent>
      </Card>
    </Box>
  );
}
