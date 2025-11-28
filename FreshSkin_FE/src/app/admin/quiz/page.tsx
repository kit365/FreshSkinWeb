"use client"

import { Box, Typography, Button, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Tooltip, Chip, FormControl, InputLabel, Select, MenuItem, TextField } from "@mui/material";
import { MdDeleteOutline, MdEditNote } from "react-icons/md";

import { useContext, useEffect, useState } from "react";
import Link from "next/link";
import { BiDetail } from "react-icons/bi";
import Alert from '@mui/material/Alert';
import { ProfileAdminContext } from "../layout";

interface ScoreRange {
    min?: number;
    max?: number;
}

export default function QuizAdminPage() {
    const [data, setData] = useState([]);
    const [dataScoreRange, setDataScoreRangge] = useState<any>([]);
    const dataProfile = useContext(ProfileAdminContext);
    const permissions = dataProfile?.permissions;
    const linkApi = 'https://freshskinweb.onrender.com/admin/question/group';
    const [filterStatus, setFilterStatus] = useState("");
    const [keyword, setKeyword] = useState("");
    const [scoreRanges, setScoreRanges] = useState<Record<number, ScoreRange>>({});

    useEffect(() => {
        const urlCurrent = new URL(location.href);
        const api = new URL(linkApi);

        // Lọc theo trạng thái
        const statusCurrent = urlCurrent.searchParams.get('status');
        setFilterStatus(statusCurrent ?? "");

        if (statusCurrent) {
            api.searchParams.set('status', statusCurrent);
        }
        else {
            api.searchParams.delete('status');
        }
        // Hết Lọc theo trạng thái

        // Tìm kiếm sản phẩm
        const keywordCurrent = urlCurrent.searchParams.get('keyword');
        setKeyword(keywordCurrent ?? "");

        if (keywordCurrent) {
            api.searchParams.set('keyword', keywordCurrent);
        }
        else {
            api.searchParams.delete('keyword');
        }
        // Hết Tìm kiếm sản phẩm

        const fetchQuiz = async () => {
            const response = await fetch(api.href);
            const data = await response.json();
            setData(data.data.QuestionGroup);
        };

        const fetchScoreRange = async () => {
            const response = await fetch(`https://freshskinweb.onrender.com/admin/skintypes/score-range`);
            const data = await response.json();
            setDataScoreRangge(data.data.items);
        }

        fetchQuiz();
        fetchScoreRange();
    }, []);

    // Lọc theo trạng thái
    const handleChangeFilterStatus = async (event: any) => {
        const value = event.target.value;
        const url = new URL(location.href);

        if (value) {
            url.searchParams.set("status", value);
        }
        else {
            url.searchParams.delete("status");
        }

        location.href = url.href;
    }
    // Hết Lọc theo trạng thái

    // Tìm kiếm sản phẩm
    const handleSumbitSearch = async (event: any) => {
        event.preventDefault();

        const value = event.target.keyword.value;
        const url = new URL(location.href);

        if (value) {
            url.searchParams.set("keyword", value);
        }
        else {
            url.searchParams.delete("keyword");
        }

        location.href = url.href;
    }
    // Hết Tìm kiếm sản phẩm

    // Thay đổi trạng thái 1 sản phẩm
    const handleChangeStatusOneQuiz = async (status: string, dataPath: string) => {
        const statusChange = status;
        const path = `${linkApi}${dataPath}`;
        const data = {
            status: statusChange
        }
        const response = await fetch(path, {
            method: "PATCH",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(data)
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
    // Hết Thay đổi trạng thái 1 sản phẩm

    // Xóa vĩnh viễn 1 bộ đề
    const handleDeleteOneQuiz = async (id: number) => {
        const confirm: boolean = window.confirm("Bạn có chắc muốn xóa vĩnh viễn bộ đề này không?");
        if (confirm) {
            const path = `${linkApi}/delete/${id}`;

            const response = await fetch(path, {
                method: "DELETE",
                headers: {
                    "Content-Type": "application/json"
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

    }
    // Hết Xóa vĩnh viễn 1 bộ đề
    const [alertMessage, setAlertMessage] = useState<string>("");
    const [alertSeverity, setAlertSeverity] = useState<"success" | "error" | "info" | "warning">("info");

    // Tạo range điểm
    const handleSubmitRangeScore = async (event: any, skinTypeId: number) => {
        event.preventDefault();

        const { min = 0, max = 0 } = scoreRanges[skinTypeId] || {};

        const response = await fetch(`https://freshskinweb.onrender.com/admin/skintypes/score-range/edit/${skinTypeId}`, {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                minScore: min,
                maxScore: max
            }),
        });

        const dataResponse = await response.json();
        if (dataResponse.code == 200) {
            location.reload();
        }
    }

    return (
        <>
            {permissions?.includes("quiz_edit") && permissions.includes("quiz_view") && (
                <Box p={3}>
                    {/* Header */}
                    <Typography variant="h5" gutterBottom>
                        Trang bộ đề câu hỏi
                    </Typography>
                    {
                        alertMessage && (
                            <Alert severity={alertSeverity} sx={{ mb: 2 }}>
                                {alertMessage}
                            </Alert>
                        )
                    }
                    {/* Bộ lọc và Tìm kiếm */}
                    <Paper elevation={1} sx={{ p: 2, mb: 2, bgcolor: "white" }} >
                        <Typography variant="subtitle1" fontWeight="bold" marginBottom={2} gutterBottom>
                            Bộ lọc và Tìm kiếm
                        </Typography>
                        <Box display="flex" flexWrap="wrap">
                            <FormControl sx={{ width: '30%', marginRight: '20px' }} >
                                <InputLabel id="filter-label" shrink={true}>Bộ lọc</InputLabel>
                                <Select labelId="filter-label" label="Bộ lọc" value={filterStatus} displayEmpty onChange={handleChangeFilterStatus} >
                                    <MenuItem value="">Tất cả</MenuItem>
                                    <MenuItem value="active">Hoạt động</MenuItem>
                                    <MenuItem value="inactive">Dừng hoạt động</MenuItem>
                                </Select>
                            </FormControl>
                            <form onSubmit={handleSumbitSearch} style={{ flex: 1, gap: "8px" }}>
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
                                    <Button variant="contained" color="success" type="submit" sx={{ backgroundColor: "#374785" }}>
                                        Tìm
                                    </Button>
                                </Box>
                            </form>
                        </Box>
                    </Paper>

                    {/* Tạo range điểm */}
                    <Paper elevation={1} sx={{ p: 2, mb: 2, bgcolor: "white" }} >
                        <Typography variant="subtitle1" fontWeight="bold" marginBottom={2} gutterBottom>
                            Kết quả dựa trên thang điểm
                        </Typography>
                        <Box display="flex" flexDirection="column" gap={2}>
                            {dataScoreRange.map((item: any, index: number) => (
                                <Box key={index} display="flex" alignItems="center" justifyContent="space-between" sx={{ marginBottom: 1 }}>
                                    <Typography variant="body1">{item.type}</Typography>
                                    <Box display="flex" alignItems="center" gap={1}>
                                        <TextField
                                            label="Điểm min"
                                            variant="outlined"
                                            type="number"
                                            size="small"
                                            defaultValue={item.minScore}
                                            sx={{ width: '100px' }}
                                            onChange={(e) => setScoreRanges(prev => ({
                                                ...prev,
                                                [item.id]: { ...prev[item.id], min: Number(e.target.value) }
                                            }))}
                                        />
                                        <TextField
                                            label="Điểm max"
                                            variant="outlined"
                                            type="number"
                                            size="small"
                                            defaultValue={item.maxScore}
                                            sx={{ width: '100px' }}
                                            onChange={(e) => setScoreRanges(prev => ({
                                                ...prev,
                                                [item.id]: { ...prev[item.id], max: Number(e.target.value) }
                                            }))}
                                        />
                                        <Button
                                            variant="contained"
                                            color="primary"
                                            size="small"
                                            onClick={() => {
                                                const foundItem = dataScoreRange.find((type: any) => type.type === item.type);
                                                if (foundItem && foundItem.id) {
                                                    handleSubmitRangeScore(event, foundItem.id);
                                                }
                                            }}
                                        >
                                            Cập nhật
                                        </Button>
                                    </Box>
                                </Box>
                            ))}
                        </Box>
                    </Paper>

                    {/* Table */}
                    <Paper sx={{ backgroundColor: "white", p: 2 }}>
                        <Box display="flex" gap={20} flexWrap="wrap">
                            <Button
                                variant="outlined"
                                color="success"
                                sx={{ borderColor: 'green', color: 'green', marginLeft: "88%" }}
                            >
                                <Link href="/admin/quiz/create">
                                    + Thêm mới
                                </Link>
                            </Button>
                        </Box>
                        <TableContainer sx={{ marginTop: "40px" }} component={Paper}>
                            <Table>
                                <TableHead>
                                    <TableRow>
                                        <TableCell>STT</TableCell>
                                        <TableCell>Tiêu đề</TableCell>
                                        <TableCell>Mô tả</TableCell>
                                        <TableCell>Trạng thái</TableCell>
                                        {/* <TableCell>Tạo bởi</TableCell> */}
                                        {/* <TableCell>Cập nhật bởi</TableCell> */}
                                        <TableCell>Hành động</TableCell>
                                    </TableRow>
                                </TableHead>
                                <TableBody>
                                    {data.map((item: any, index: number) => (
                                        <TableRow key={item.id}>
                                            <TableCell>{index + 1}</TableCell>
                                            <TableCell>{item.title}</TableCell>
                                            <TableCell>{item.description}</TableCell>
                                            <TableCell>
                                                {item.status === "ACTIVE" && (
                                                    <Chip
                                                        label="Hoạt động"
                                                        color="success"
                                                        size="small"
                                                        variant="outlined"
                                                        onClick={() => handleChangeStatusOneQuiz("INACTIVE", `/edit/${item.id}`)}
                                                    />
                                                )}
                                                {item.status === "INACTIVE" && (
                                                    <Chip
                                                        label="Dừng hoạt động"
                                                        color="error"
                                                        size="small"
                                                        variant="outlined"
                                                        onClick={() => handleChangeStatusOneQuiz("ACTIVE", `/edit/${item.id}`)}
                                                    />
                                                )}
                                            </TableCell>
                                            {/* <TableCell>
                                    {brand.createdBy}
                                    <br />
                                    {brand.createdAt}
                                </TableCell>
                                <TableCell>
                                    {brand.updatedBy}
                                    <br />
                                    {brand.updatedAt}
                                </TableCell> */}
                                            <TableCell>
                                                <div className="flex">
                                                    <Tooltip title="Chi tiết" placement="top">
                                                        <Link href={`/admin/quiz/detail/${item.id}`}>
                                                            <BiDetail className="text-[25px] text-[#138496] mr-2" />
                                                        </Link>
                                                    </Tooltip>
                                                    <Tooltip title="Sửa" placement="top">
                                                        <Link href={`/admin/quiz/edit/${item.id}`}>
                                                            <MdEditNote className="text-[25px] text-[#E0A800]" />
                                                        </Link>
                                                    </Tooltip>
                                                    <Tooltip title="Xóa" placement="top" className="cursor-pointer" onClick={() => handleDeleteOneQuiz(item.id)}>
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
                </Box>
            )}
        </>
    );
}