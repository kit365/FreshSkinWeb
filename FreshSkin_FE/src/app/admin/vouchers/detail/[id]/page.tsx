"use client";

import React, { useContext, useEffect, useState } from "react";
import {
  Box,
  Typography,
  Grid,
  Paper,
  Divider,
  Chip,
} from "@mui/material";

import { ProfileAdminContext } from "../../../layout";
import { useParams } from "next/navigation";

export default function VoucherDetailPage() {
  const dataProfile = useContext(ProfileAdminContext);
  const permissions = dataProfile?.permissions;

  const [data, setData] = useState({
    discountValue: 0,
    endDate: "",
    maxDiscount: 0,
    minOrderValue: 0,
    name: "",
    startDate: "",
    type: "",
    usageLimit: 0,
    voucherId: "",
    used: 0
  });

  const { id } = useParams();

  useEffect(() => {
    const fetchData = async () => {
      const response = await fetch(
        `https://freshskinweb.onrender.com/admin/vouchers/${id}`
      );
      const dataResponse = await response.json();
      setData(dataResponse.data);
    };

    fetchData();
  }, [id]);

  return (
    <>
      {permissions?.includes("vouchers_view") && (
        <Box sx={{ padding: 3, backgroundColor: "#f9f9f9", minHeight: "100vh" }}>
          <Typography variant="h5" gutterBottom sx={{ fontWeight: "bold" }}>
            Chi tiết mã giảm giá
          </Typography>
          <Typography variant="subtitle1" color="textSecondary" gutterBottom>
            ID Mã giảm giá: <strong>{data.voucherId}</strong>
          </Typography>
          <Paper
            elevation={3}
            sx={{
              padding: 4,
              borderRadius: 2,
              mt: 2,
              backgroundColor: "#ffffff",
            }}
          >
            <Typography
              variant="h6"
              gutterBottom
              sx={{ fontWeight: "bold", color: "#333" }}
            >
              Thông tin mã giảm giá
            </Typography>
            <Divider sx={{ marginBottom: 3 }} />

            <Grid container spacing={3}>
              <Grid item xs={12} md={6}>
                <Typography variant="subtitle1" color="textSecondary">
                  <strong>Tên mã giảm giá:</strong>
                </Typography>
                <Typography variant="body1">{data.name}</Typography>
              </Grid>
              <Grid item xs={12} md={6}>
                <Typography variant="subtitle1" color="textSecondary">
                  <strong>Loại mã giảm giá:</strong>
                </Typography>
                <Typography variant="body1">
                  {data.type === "PERCENTAGE" ? "Phần trăm" : "Cố định"}
                </Typography>
              </Grid>
              <Grid item xs={12} md={6}>
                <Typography variant="subtitle1" color="textSecondary">
                  <strong>Giá trị giảm giá:</strong>
                </Typography>
                <Typography variant="body1">{data.discountValue}%</Typography>
              </Grid>
              <Grid item xs={12} md={6}>
                <Typography variant="subtitle1" color="textSecondary">
                  <strong>Giảm tối đa:</strong>
                </Typography>
                <Typography variant="body1">
                  {data.maxDiscount.toLocaleString()} VND
                </Typography>
              </Grid>
              <Grid item xs={12} md={6}>
                <Typography variant="subtitle1" color="textSecondary">
                  <strong>Giá trị đơn hàng tối thiểu:</strong>
                </Typography>
                <Typography variant="body1">
                  {data.minOrderValue.toLocaleString()} VND
                </Typography>
              </Grid>
              <Grid item xs={12} md={6}>
                <Typography variant="subtitle1" color="textSecondary">
                  <strong>Giới hạn sử dụng:</strong>
                </Typography>
                <Typography variant="body1">{data.usageLimit} lần</Typography>
              </Grid>
              <Grid item xs={12} md={6}>
                <Typography variant="subtitle1" color="textSecondary">
                  <strong>Ngày bắt đầu:</strong>
                </Typography>
                <Typography variant="body1">{data.startDate}</Typography>
              </Grid>
              <Grid item xs={12} md={6}>
                <Typography variant="subtitle1" color="textSecondary">
                  <strong>Ngày kết thúc:</strong>
                </Typography>
                <Typography variant="body1">{data.endDate}</Typography>
              </Grid>
            </Grid>

            <Divider sx={{ marginY: 3 }} />

            <Box sx={{ textAlign: "center", marginTop: 3 }}>
              <Chip
                label={
                  new Date(data.endDate) >= new Date() && data.used > 0
                    ? "Còn hiệu lực"
                    : "Hết hiệu lực"
                }
                color={new Date(data.endDate) >= new Date() ? "success" : "error"}
                sx={{ fontSize: "16px", padding: "6px 12px" }}
              />
            </Box>
          </Paper>
        </Box>
      )}
    </>
  );
}