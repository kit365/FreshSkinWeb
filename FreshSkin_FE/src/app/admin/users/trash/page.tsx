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
  Chip,
  Tooltip,
  Checkbox,
} from "@mui/material";
import {
  MdDeleteOutline,
  MdOutlineSettingsBackupRestore,
} from "react-icons/md";
import Link from "next/link";
import { useContext, useEffect, useState } from "react";
import { ProfileAdminContext } from "../../layout";
import { IoReturnDownBackOutline } from "react-icons/io5";
import Alert from "@mui/material/Alert";
export default function UserAdminPage() {
  const dataProfile = useContext(ProfileAdminContext);
  const permissions = dataProfile?.permissions;

  const [data, setData] = useState([]);

  const linkApi = "https://freshskinweb.onrender.com/admin/users/trash";

  const [inputChecked, setInputChecked] = useState<number[]>([]);
  // Hiển thị lựa chọn mặc định
  const [filterStatus, setFilterStatus] = useState("");
  const [keyword, setKeyword] = useState("");
  const [changeMulti, setChangeMulti] = useState("RESTORED");
  const [alertMessage, setAlertMessage] = useState<string>("");
  const [alertSeverity, setAlertSeverity] = useState<
    "success" | "error" | "info" | "warning"
  >("info");

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

    // Tìm kiếm tài khoản
    const keywordCurrent = urlCurrent.searchParams.get("keyword");
    setKeyword(keywordCurrent ?? "");

    if (keywordCurrent) {
      api.searchParams.set("keyword", keywordCurrent);
    } else {
      api.searchParams.delete("keyword");
    }
    // Hết Tìm kiếm tài khoản

    const fetchAccounts = async () => {
      const response = await fetch(api.href);
      const data = await response.json();
      setData(data.data.users);
    };

    fetchAccounts();
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

  // Tìm kiếm tài khoản
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

  // Thay đổi trạng thái nhiều tài khoản
  const handleChangeMulti = async (event: any) => {
    event.preventDefault();

    const statusChange = changeMulti;

    const path = `${linkApi}/change-multi`;

    const data: any = {
      id: inputChecked,
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

  const handleInputChecked = (event: any, id: number) => {
    if (event.target.checked) {
      setInputChecked((prev) => [...prev, id]);
    } else {
      setInputChecked((prev) => prev.filter((id) => id !== id));
    }
  };
  // Hết Thay đổi trạng thái nhiều tài khoản

  // Xóa vĩnh viễn một sản phẩm
  const handleDeleteOneUser = async (id: number) => {
    const confirm: boolean = window.confirm(
      "Bạn có chắc muốn xóa vĩnh viễn tài khoản này không?"
    );
    if (confirm) {
      const path = `${linkApi}/delete/${id}`;

      const response = await fetch(path, {
        method: "DELETE",
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

  // Khôi phục một sản phẩm
  const handleRestoreOneUser = async (id: number) => {
    const confirm: boolean = window.confirm("Bạn có chắc muốn khôi phục tài khoản này không?");
    if (confirm) {
      const path = `${linkApi}/restore/${id}`;

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
  // Hết Khôi phục một sản phẩm

  return (
    <>
      {alertMessage && (
        <Alert severity={alertSeverity} sx={{ mb: 2 }}>
          {alertMessage}
        </Alert>
      )}
      {permissions?.includes("accounts_view") &&
        permissions?.includes("accounts_edit") && (
          <Box p={3}>
            {/* Header */}
            <Typography variant="h5" gutterBottom>
              Trang thùng rác tài khoản người dùng
            </Typography>

            {/* Bộ lọc và Tìm kiếm */}
            <Paper elevation={1} sx={{ p: 2, mb: 2, bgcolor: "white" }}>
              <Typography
                variant="subtitle1"
                fontWeight="bold"
                marginBottom={2}
                gutterBottom
              >
                Bộ lọc và Tìm kiếm
              </Typography>
              <Box display="flex" flexWrap="wrap">
                <FormControl sx={{ width: "30%", marginRight: "20px" }}>
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
                    <MenuItem value="ACTIVE">Hoạt động</MenuItem>
                    <MenuItem value="INACTIVE">Dừng hoạt động</MenuItem>
                  </Select>
                </FormControl>
                <form
                  onSubmit={handleSumbitSearch}
                  style={{ flex: 1, gap: "8px" }}
                >
                  <Box display="flex">
                    <TextField
                      label="Nhập từ khóa..."
                      variant="outlined"
                      fullWidth
                      name="keyword"
                      defaultValue={keyword}
                      InputLabelProps={{
                        shrink: true,
                      }}
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
                  <Box display="flex">
                    <Select
                      fullWidth
                      name="status"
                      value={changeMulti}
                      displayEmpty
                      onChange={(e) => setChangeMulti(e.target.value)}
                    >
                      <MenuItem value="RESTORED">Khôi phục</MenuItem>
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
                </form>
                <Button
                  variant="outlined"
                  color="success"
                  sx={{ borderColor: "green", color: "green" }}
                >
                  <Link href="/admin/users" className="flex items-center">
                    <IoReturnDownBackOutline className="text-[25px] mr-[5px]" />
                    Danh sách tài khoản
                  </Link>
                </Button>
              </Box>
              <TableContainer sx={{ marginTop: "40px" }} component={Paper}>
                <Table>
                  <TableHead>
                    <TableRow>
                      <TableCell></TableCell>
                      <TableCell>STT</TableCell>
                      <TableCell>Avatar</TableCell>
                      <TableCell>Họ</TableCell>
                      <TableCell>Tên</TableCell>
                      <TableCell>Tên tài khoản</TableCell>
                      <TableCell>Số điện thoại</TableCell>
                      <TableCell>Trạng thái</TableCell>
                      <TableCell>Khôi phục</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {data.map((account: any, index: number) => (
                      <TableRow key={index}>
                        <TableCell
                          padding="checkbox"
                          onClick={(event) =>
                            handleInputChecked(event, account.userID)
                          }
                        >
                          <Checkbox />
                        </TableCell>
                        <TableCell>{index + 1}</TableCell>
                        <TableCell>
                          <img
                            src={account.avatar}
                            style={{
                              width: 100,
                              height: 100,
                              objectFit: "cover",
                            }}
                          />
                        </TableCell>
                        <TableCell>{account.firstName}</TableCell>
                        <TableCell>{account.lastName}</TableCell>
                        <TableCell>{account.username}</TableCell>
                        <TableCell>{account.phone}</TableCell>
                        <TableCell>
                          {account.status === "ACTIVE" && (
                            <Chip
                              label="Hoạt động"
                              color="success"
                              size="small"
                              variant="outlined"                             
                            />
                          )}
                          {account.status === "INACTIVE" && (
                            <Chip
                              label="Dừng hoạt động"
                              color="error"
                              size="small"
                              variant="outlined"                            
                            />
                          )}
                        </TableCell>
                        <TableCell>
                          <div className="flex">
                            <Tooltip title="Khôi phục" placement="top">
                              <MdOutlineSettingsBackupRestore
                                className="text-[25px] text-blue-500 cursor-pointer"
                                onClick={() =>
                                  handleRestoreOneUser(account.userID)
                                }
                              />
                            </Tooltip>                       
                          </div>
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </TableContainer>
            </Paper>
          </Box>
        )}
    </>
  );
}