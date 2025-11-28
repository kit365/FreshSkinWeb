"use client";

import dynamic from "next/dynamic";
import React, { useContext, useEffect, useState } from "react";
import {
  Box,
  Typography,
  TextField,
  Button,
  Paper,
  FormControl,
  RadioGroup,
  FormControlLabel,
  Radio,
} from "@mui/material";
import { useParams } from "next/navigation";
import UploadImage from "@/app/components/Upload/UploadImage";
import { ProfileAdminContext } from "@/app/admin/layout";
import Alert from "@mui/material/Alert";
const TinyEditor = dynamic(() => import("../../../../../../TinyEditor"), {
  ssr: false,
});

export default function EditBrandtAdminPage() {
  const dataProfile = useContext(ProfileAdminContext);
  const permissions = dataProfile?.permissions;
  const [alertMessage, setAlertMessage] = useState<string>("");
  const [loading, setLoading] = useState(false);
  const [alertSeverity, setAlertSeverity] = useState<
    "success" | "error" | "info" | "warning"
  >("info");
  const { id } = useParams();
  const [description, setDescription] = useState("");

  const [data, setData] = useState({
    title: "",
    description: "",
    featured: false,
    image: [],
  });

  useEffect(() => {
    const fetchBrand = async () => {
      const response = await fetch(
        `https://freshskinweb.onrender.com/admin/products/brand/${id}`
      );
      const data = await response.json();
      setData(data.data);
      setDescription(data.data.description);
    };

    fetchBrand();
  }, []);

  const handleChangeFeatured = (event: any) => {
    setData({ ...data, featured: event.target.value === "true" });
  };

  const [images, setImages] = useState<(File)[]>([]);

  const handleImageChange = (newImages: (File)[]) => {
    setImages(newImages);
  };

  const handleRemoveDefaultImage = (index: number) => {
    setData(prevData => ({
      ...prevData,
      image: prevData.image.filter((_, i) => i !== index)
    }));
  };

  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setLoading(true);

    if (!data.title) {
      setAlertMessage("Tên thương hiệu không được để trống.");
      setAlertSeverity("error");
      setTimeout(() => setAlertMessage(""), 5000);
      setLoading(false);
      return;
    }

    if (!description) {
      setAlertMessage("Mô tả thương hiệu không được để trống.");
      setAlertSeverity("error");
      setTimeout(() => setAlertMessage(""), 5000);
      setLoading(false);
      return;
    }

    if ((images.length + data.image.length) < 1) {
      setAlertMessage("Phải chọn tối thiểu 1 ảnh.");
      setAlertSeverity("error");
      setTimeout(() => setAlertMessage(""), 5000);
      setLoading(false);
      return;
    }

    const request = {
      title: data.title,
      image: data.image,
      description: description,
      featured: data.featured
    };

    const formData = new FormData();

    formData.append("request", JSON.stringify(request));

    images.forEach((image) => {
      if (image instanceof File) {
        formData.append("newImg", image);
      }
    });

    const response = await fetch(
      `https://freshskinweb.onrender.com/admin/products/brand/edit/${id}`,
      {
        method: "PATCH",
        body: formData,
      }
    );

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
      {alertMessage && (
        <Alert severity={alertSeverity} sx={{ position: "fixed", width: "600px", height: "60px", right: "5%", top: "5%", fontSize: "16px", zIndex: "999999" }}>
          {alertMessage}
        </Alert>
      )}
      {permissions?.includes("brands_edit") && (
        <Box sx={{ padding: 3, backgroundColor: "#e3f2fd" }}>
          <Typography variant="h5" gutterBottom>
            Trang chỉnh sửa thương hiệu
          </Typography>

          <Paper elevation={3} sx={{ padding: 3, marginBottom: 2 }}>
            <form onSubmit={handleSubmit}>
              <TextField
                label="Tên thương hiệu"
                name="title"
                variant="outlined"
                fullWidth
                sx={{ marginBottom: 3 }}
                value={data.title}
                onChange={(e) =>
                  setData({ ...data, title: e.target.value })
                }
              />
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
                label="Chỉnh sửa hình ảnh"
                id="upload-images"
                name="images"
                defaultImages={data.image}
                onImageChange={handleImageChange}
                onRemoveDefaultImage={handleRemoveDefaultImage}
              />
              <h4>Mô tả</h4>
              <TinyEditor
                value={description}
                onEditorChange={(content: string) => setDescription(content)}
              />
              <Button
                type='submit'
                variant="contained"
                color="primary"
                sx={{ width: '100%' }}
                disabled={loading}
              >
                {loading ? "Đang cập nhật thương hiệu..." : "Chỉnh sửa thương hiệu"}
              </Button>
            </form>
          </Paper>
        </Box>
      )}
    </>
  );
}