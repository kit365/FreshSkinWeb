"use client";

import {
  Box,
  Typography,
  TextField,
  Select,
  MenuItem,
  InputLabel,
  FormControl,
  Button,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Checkbox,
  Chip,
  Tooltip,
  Stack,
  Pagination,
} from "@mui/material";
import DeleteIcon from "@mui/icons-material/Delete";
import { BiDetail } from "react-icons/bi";
import { MdDeleteOutline, MdEditNote } from "react-icons/md";
import Alert from "@mui/material/Alert";
import { useContext, useEffect, useState } from "react";
import Link from "next/link";
import { ProfileAdminContext } from "../layout";

export default function ProductsAdminPage() {
  const dataProfile = useContext(ProfileAdminContext);
  const permissions = dataProfile?.permissions;

  const [data, setData] = useState({
    totalPages: 1,
    totalItems: 1,
    pageSize: 4,
    currentPage: 1,
    products: [],
  });
  const [alertMessage, setAlertMessage] = useState<string>("");
  const [alertSeverity, setAlertSeverity] = useState<
    "success" | "error" | "info" | "warning"
  >("info");
  const linkApi = "https://freshskinweb.onrender.com/admin/products";



  // Hiển thị lựa chọn mặc định
  const [filterStatus, setFilterStatus] = useState("");
  const [keyword, setKeyword] = useState("");
  const [sort, setSort] = useState("position-desc");
  const [page, setPage] = useState(1);
  const [changeMulti, setChangeMulti] = useState("ACTIVE");

  useEffect(() => {
    const urlCurrent = new URL(location.href);
    const api = new URL(linkApi);

    // Lọc theo trạng thái
    const statusCurrent = urlCurrent.searchParams.get("status");
    setFilterStatus(statusCurrent ?? "");

    if (statusCurrent) {
      api.searchParams.set("status", statusCurrent);
    } else {
      api.searchParams.delete("status");
    }
    // Hết Lọc theo trạng thái

    // Tìm kiếm sản phẩm
    const keywordCurrent = urlCurrent.searchParams.get("keyword");
    setKeyword(keywordCurrent ?? "");

    if (keywordCurrent) {
      api.searchParams.set("keyword", keywordCurrent);
    } else {
      api.searchParams.delete("keyword");
    }
    // Hết Tìm kiếm sản phẩm

    // Phân trang
    const pageCurrent = urlCurrent.searchParams.get("page");
    setPage(pageCurrent ? parseInt(pageCurrent) : 1);

    if (pageCurrent) {
      api.searchParams.set("page", pageCurrent);
    } else {
      api.searchParams.delete("page");
    }
    // Hết Phân trang

    // Sắp xếp theo tiêu chí
    const sortKeyCurrent = urlCurrent.searchParams.get("sortKey");
    const sortValueCurrent = urlCurrent.searchParams.get("sortValue");

    if (sortKeyCurrent && sortValueCurrent) {
      setSort(`${sortKeyCurrent}-${sortValueCurrent}`);
    } else {
      setSort("position-desc");
    }

    if (sortKeyCurrent && sortValueCurrent) {
      api.searchParams.set("sortKey", sortKeyCurrent);
      api.searchParams.set("sortValue", sortValueCurrent);
    } else {
      api.searchParams.delete("sortKey");
      api.searchParams.delete("sortValue");
    }
    // Hết Sắp xếp theo tiêu chí

    console.log(api.href);

    const fetchProducts = async () => {
      const response = await fetch(api.href);
      const data = await response.json();
      setData(data.data);
    };

    fetchProducts();
  }, []);

  // Lọc theo trạng thái
  const handleChangeFilterStatus = async (event: any) => {
    const value = event.target.value;
    const url = new URL(location.href);

    if (value) {
      url.searchParams.set("status", value);
    } else {
      url.searchParams.delete("status");
    }

    location.href = url.href;
  };
  // Hết Lọc theo trạng thái

  // Tìm kiếm sản phẩm
  const handleSumbitSearch = async (event: any) => {
    event.preventDefault();

    const value = event.target.keyword.value;
    const url = new URL(location.href);

    if (value) {
      url.searchParams.set("keyword", value);
    } else {
      url.searchParams.delete("keyword");
    }

    location.href = url.href;
  };
  // Hết Tìm kiếm sản phẩm

  // Thay đổi trạng thái 1 sản phẩm
  const handleChangeStatusOneProduct = async (
    status: string,
    dataPath: string
  ) => {
    const statusChange = status;
    const path = `${linkApi}${dataPath}`;

    const data = {
      statusEdit: "editStatus",
      status: statusChange,
    };

    const response = await fetch(path, {
      method: "PATCH",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(data),
    });

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
  // Hết Thay đổi trạng thái 1 sản phẩm

  // Thay đổi trạng thái nhiều sản phẩm
  const handleChangeMulti = async (event: any) => {
    event.preventDefault();

    const statusChange = changeMulti;

    const path = `${linkApi}/change-multi`;

    const data: any = {
      id: selectedCategories,
      status: statusChange,
    };

    const response = await fetch(path, {
      method: "PATCH",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(data),
    });

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
  const [selectedCategories, setSelectedCategories] = useState<number[]>([]);

  const handleInputChecked = (
    event: React.ChangeEvent<HTMLInputElement>,
    id: number
  ) => {
    if (event.target.checked) {
      setSelectedCategories((prev) => [...prev, id]);
    } else {
      setSelectedCategories((prev) => prev.filter((item) => item !== id));
    }
  };
  // Hết Thay đổi trạng thái nhiều sản phẩm

  // Xóa một sản phẩm
  const handleDeleteOneProduct = async (id: number) => {
    const confirm: boolean = window.confirm(
      "Bạn có chắc muốn xóa sản phẩm này không?"
    );
    if (confirm) {
      const path = `${linkApi}/deleteT/${id}`;

      const response = await fetch(path, {
        method: "PATCH",
        headers: {
          "Content-Type": "application/json",
        },
      });

      const dataResponse = await response.json();

      if (dataResponse.code === 200) {
        setAlertMessage(dataResponse.message);
        setAlertSeverity("success");
        setTimeout(() => location.reload(), 2000);
      } else {
        setAlertMessage(dataResponse.message);
        setAlertSeverity("error");
      }
    }
  };
  // Hết Xóa một sản phẩm

  // Thay đổi vị trí sản phẩm
  const handleChangePosition = async (event: any, id: number) => {
    const newPosition = parseInt(event.target.value);

    if (newPosition < 0) {
      alert("Vị trí phải là một số không âm");
      return;
    }

    const path = `${linkApi}/update/${id}`;

    console.log({
      statusEdit: "editPosition",
      position: newPosition,
    });

    const response = await fetch(path, {
      method: "PATCH",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        statusEdit: "editPosition",
        position: newPosition,
      }),
    });

    const dataResponse = await response.json();

    if (dataResponse.code === 200) {
      location.reload();
    }
  };
  // Hết Thay đổi vị trí sản phẩm

  // Sắp xếp theo tiêu chí
  const handleChangeSort = async (event: any) => {
    const value = event.target.value;
    const url = new URL(location.href);

    if (value) {
      const [sortKey, sortValue] = value.split("-");
      url.searchParams.set("sortKey", sortKey);
      url.searchParams.set("sortValue", sortValue);
    } else {
      url.searchParams.delete("sortKey");
      url.searchParams.delete("sortValue");
    }

    location.href = url.href;
  };
  // Hết Sắp xếp theo tiêu chí

  // Phân trang
  const handlePagination = (event: any, page: number) => {
    const url = new URL(location.href);

    if (page) {
      url.searchParams.set("page", page.toString());
    } else {
      url.searchParams.delete("page");
    }

    location.href = url.href;
  };
  // Hết phân trang

  return (
    <>
      {alertMessage && (
        <Alert severity={alertSeverity} sx={{ mb: 2 }}>
          {alertMessage}
        </Alert>
      )}
      {permissions?.includes("products_view") &&
        permissions.includes("products_edit") && (
          <Box p={3}>
            {/* Header */}
            <Typography variant="h5" gutterBottom>
              Trang danh sách sản phẩm
            </Typography>

            {/* Bộ lọc và Tìm kiếm */}
            <Paper elevation={1} sx={{ p: 2, mb: 3, bgcolor: "white" }}>
              <Typography
                variant="subtitle1"
                fontWeight="bold"
                marginBottom={2}
                gutterBottom
              >
                Bộ lọc, Tìm kiếm và Sắp xếp
              </Typography>

              <Box display="flex" flexWrap="wrap" gap={2}>
                {/* Bộ lọc */}
                <FormControl sx={{ width: "5%", minWidth: 200 }}>
                  <InputLabel id="filter-label" shrink={true}>
                    Bộ lọc
                  </InputLabel>
                  <Select
                    labelId="filter-label"
                    label="Bộ lọc"
                    value={filterStatus}
                    displayEmpty
                    onChange={handleChangeFilterStatus}
                  >
                    <MenuItem value="">Tất cả</MenuItem>
                    <MenuItem value="active">Hoạt động</MenuItem>
                    <MenuItem value="inactive">Dừng hoạt động</MenuItem>
                  </Select>
                </FormControl>

                {/* Tìm kiếm */}
                <form
                  onSubmit={handleSumbitSearch}
                  style={{ flex: 1, minWidth: 200 }}
                >
                  <Box display="flex" gap={1}>
                    <TextField
                      label="Nhập từ khóa..."
                      variant="outlined"
                      fullWidth
                      name="keyword"
                      defaultValue={keyword}
                      InputLabelProps={{ shrink: true }}
                    />
                    <Button
                      variant="contained"
                      color="success"
                      type="submit"
                      sx={{ backgroundColor: "#374785" }}
                    >
                      Tìm
                    </Button>
                  </Box>
                </form>

                {/* Sắp xếp */}
                <FormControl fullWidth sx={{ maxWidth: 250 }}>
                  <InputLabel id="sort-label" shrink={true}>
                    Sắp xếp
                  </InputLabel>
                  <Select
                    labelId="sort-label"
                    label="Sắp xếp"
                    value={sort}
                    displayEmpty
                    onChange={handleChangeSort}
                  >
                    <MenuItem value="position-desc">Vị trí giảm dần</MenuItem>
                    <MenuItem value="position-asc">Vị trí tăng dần</MenuItem>
                    <MenuItem value="price-desc">Giá giảm dần</MenuItem>
                    <MenuItem value="price-asc">Giá tăng dần</MenuItem>
                    <MenuItem value="title-desc">Tiêu đề từ Z đến A</MenuItem>
                    <MenuItem value="title-asc">Tiêu đề từ A đến Z</MenuItem>
                  </Select>
                </FormControl>
              </Box>
            </Paper>

            {/* Table */}
            <Paper sx={{ backgroundColor: "white", p: 2 }}>
              <Typography variant="h6" gutterBottom sx={{ marginLeft: "20px" }}>
                Danh sách
              </Typography>
              <Box display="flex" gap={20} flexWrap="wrap">
                <form
                  onSubmit={handleChangeMulti}
                  style={{ flex: 1, gap: "8px" }}
                >
                  {selectedCategories.length > 0 && (
                    <Box display="flex" gap={0.5}>
                      <Select
                        fullWidth
                        sx={{ maxWidth: 200 }}
                        name="status"
                        value={changeMulti}
                        displayEmpty
                        onChange={(e) => setChangeMulti(e.target.value)}
                      >
                        <MenuItem value="ACTIVE">Hoạt động</MenuItem>
                        <MenuItem value="INACTIVE">Dừng hoạt động</MenuItem>
                        <MenuItem value="SOFT_DELETED">Xóa</MenuItem>
                      </Select>
                      <Button
                        variant="contained"
                        color="success"
                        type="submit"
                        sx={{
                          width: "120px",
                          backgroundColor: "#374785",
                          color: "#ffffff",
                        }}
                      >
                        Áp dụng
                      </Button>
                    </Box>
                  )}
                </form>
                <Button
                  variant="contained"
                  startIcon={<DeleteIcon />}
                  sx={{
                    backgroundColor: "#757575",
                    "&:hover": { backgroundColor: "#616161" },
                  }}
                >
                  <Link href="/admin/products/trash">Thùng rác</Link>
                </Button>
                <Button
                  variant="outlined"
                  color="success"
                  sx={{ borderColor: "#374785", color: "#374785" }}
                >
                  <Link href="/admin/products/create">+ Thêm mới</Link>
                </Button>
              </Box>
              <TableContainer
                component={Paper}
                sx={{ maxHeight: 500, bgcolor: "#f9f9f9" }}
              >
                <Table stickyHeader>
                  <TableHead>
                    <TableRow>
                      <TableCell></TableCell>
                      <TableCell sx={{ color: "#374785", fontWeight: "bold" }}>
                        STT
                      </TableCell>
                      <TableCell sx={{ color: "#374785", fontWeight: "bold" }}>
                        Hình ảnh
                      </TableCell>
                      <TableCell sx={{ color: "#374785", fontWeight: "bold" }}>
                        Tiêu đề
                      </TableCell>
                      <TableCell sx={{ color: "#374785", fontWeight: "bold" }}>
                        Giá
                      </TableCell>
                      <TableCell sx={{ color: "#374785", fontWeight: "bold" }}>
                        Trạng thái
                      </TableCell>
                      <TableCell sx={{ color: "#374785", fontWeight: "bold" }}>
                        Vị trí
                      </TableCell>
                      <TableCell sx={{ color: "#374785", fontWeight: "bold" }}>
                        Hành động
                      </TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {data.products.map((product: any, index: number) => (
                      <TableRow
                        key={product.id}
                        sx={{
                          bgcolor: index % 2 === 0 ? "#ffffff" : "#f1f1f1",
                        }}
                      >
                        <TableCell padding="checkbox">
                          <Checkbox
                            onChange={(event) =>
                              handleInputChecked(event, product.id)
                            }
                          />
                        </TableCell>
                        <TableCell>
                          {(data.currentPage - 1) * data.pageSize + index + 1}
                        </TableCell>
                        <TableCell>
                          <img
                            src={product.thumbnail[0]}
                            alt={product.title}
                            style={{
                              width: 100,
                              height: 100,
                              objectFit: "cover",
                            }}
                          />
                        </TableCell>
                        <TableCell>{product.title}</TableCell>
                        <TableCell>
                          {product.variants[0].price.toLocaleString("en-US")}
                        </TableCell>
                        <TableCell>
                          {product.status === "ACTIVE" && (
                            <Chip
                              label="Hoạt động"
                              color="success"
                              size="small"
                              variant="outlined"
                              onClick={() =>
                                handleChangeStatusOneProduct(
                                  "inactive",
                                  `/update/${product.id}`
                                )
                              }
                            />
                          )}
                          {product.status === "INACTIVE" && (
                            <Chip
                              label="Dừng hoạt động"
                              color="error"
                              size="small"
                              variant="outlined"
                              onClick={() =>
                                handleChangeStatusOneProduct(
                                  "active",
                                  `/update/${product.id}`
                                )
                              }
                            />
                          )}
                        </TableCell>
                        <TableCell>
                          <TextField
                            type="number"
                            variant="outlined"
                            size="small"
                            sx={{
                              width: "60px",
                            }}
                            onChange={(e) =>
                              handleChangePosition(e, product.id)
                            }
                            defaultValue={product.position}
                            InputProps={{
                              inputProps: { min: 0, step: 1 },
                            }}
                          />
                        </TableCell>
                        {/* <TableCell>
                         {product.createdBy}
                         <br />
                         {product.createdAt}
                     </TableCell>
                     <TableCell>
                         {product.updatedBy}
                         <br />
                         {product.updatedAt}
                     </TableCell> */}
                        <TableCell>
                          <div className="flex">
                            <Tooltip title="Chi tiết" placement="top">
                              <Link
                                href={`/admin/products/detail/${product.id}`}
                              >
                                <BiDetail className="text-[25px] text-[#138496] mr-2" />
                              </Link>
                            </Tooltip>
                            <Tooltip title="Sửa" placement="top">
                              <Link href={`/admin/products/edit/${product.id}`}>
                                <MdEditNote className="text-[25px] text-[#E0A800]" />
                              </Link>
                            </Tooltip>
                            <Tooltip
                              title="Xóa"
                              placement="top"
                              className="cursor-pointer"
                              onClick={() => handleDeleteOneProduct(product.id)}
                            >
                              <MdDeleteOutline className="text-[25px] text-[#C62828] ml-1" />
                            </Tooltip>
                          </div>
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </TableContainer>
            </Paper>

            {/* Pagination */}
            <Stack spacing={2} marginTop={2}>
              <Pagination
                count={data.totalPages}
                color="primary"
                page={page}
                variant="outlined"
                shape="rounded"
                siblingCount={1}
                sx={{
                  "& .MuiPaginationItem-root": {
                    backgroundColor: "white",
                    color: "blue",
                    "&:hover": {
                      backgroundColor: "#e0e0e0",
                    },
                  },
                  "& .Mui-selected": {
                    backgroundColor: "blue",
                    color: "white",
                  },
                }}
                onChange={handlePagination}
              />
            </Stack>
          </Box>
        )}
    </>
  );
}
