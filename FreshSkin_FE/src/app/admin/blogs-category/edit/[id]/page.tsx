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

export default function EditBlogCategorytAdminPage() {
  const dataProfile = useContext(ProfileAdminContext);
  const permissions = dataProfile?.permissions;
  const [alertMessage, setAlertMessage] = useState<string>("");
  const [alertSeverity, setAlertSeverity] = useState<
    "success" | "error" | "info" | "warning"
  >("info");
  const { id } = useParams();
  const [description, setDescription] = useState("");
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState({
    title: "",
    description: "",
    featured: false,
    image: [],
  });

  useEffect(() => {
    const fetchBrand = async () => {
      const response = await fetch(
        `https://freshskinweb.onrender.com/admin/blogs/category/${id}`
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

    const formData = new FormData();
    if (!data.title) {
      setAlertMessage("Tiêu đề danh mục bài viết không được để trống.");
      setAlertSeverity("error");
      setTimeout(() => setAlertMessage(""), 5000);
      setLoading(false);
      return;
    }

    if (!description) {
      setAlertMessage("Mô tả danh mục bài viết không được để trống.");
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
      description: description,
      thumbnail: data.image,
      featured: data.featured
    };


    formData.append("request", JSON.stringify(request));

    images.forEach((image) => {
      if (image instanceof File) {
        formData.append("newImg", image);
      }
    });

    const response = await fetch(
      `https://freshskinweb.onrender.com/admin/blogs/category/edit/${id}`,
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
      {permissions?.includes("blogs-category_edit") && (
        <Box sx={{ padding: 3, backgroundColor: "#e3f2fd" }}>
          <Typography variant="h5" gutterBottom>
            Trang chỉnh sửa danh mục bài viết
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
                onEditorChange={(description: string) => setDescription(description)}
              />
              <Button
                type='submit'
                variant="contained"
                color="primary"
                sx={{ width: '100%' }}
                disabled={loading}
              >
                {loading ? "Đang cập nhật danh mục bài viết..." : "Chỉnh sửa danh mục bài viết"}
              </Button>
            </form>
          </Paper>
        </Box>
      )}
    </>
  );
}