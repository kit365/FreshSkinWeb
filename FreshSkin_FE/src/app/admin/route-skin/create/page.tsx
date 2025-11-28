"use client"

import {
    Box,
    Button,
    Paper,
    Radio,
    RadioGroup,
    FormControlLabel,
    TextField,
    Typography,
} from "@mui/material";
import { useContext, useEffect, useState } from "react";
import Alert from "@mui/material/Alert";
import { ProfileAdminContext } from "@/app/admin/layout";
import dynamic from "next/dynamic";
const TinyEditor = dynamic(() => import('../../../../../TinyEditor'), {
    ssr: false
});
export default function EditRouteSkinAdminPage() {
    const dataProfile = useContext(ProfileAdminContext);
    const permissions = dataProfile?.permissions;

    const [listSkinType, setListSkinType] = useState([]);

    useEffect(() => {
        const fetchSkintypes = async () => {
            const response = await fetch("https://freshskinweb.onrender.com/admin/skintypes/show");
            const data = await response.json();
            setListSkinType(data.data);
        };

        fetchSkintypes();
    }, []);

    const [loading, setLoading] = useState(false);
    const [alertMessage, setAlertMessage] = useState<string>("");
    const [alertSeverity, setAlertSeverity] = useState<"success" | "error" | "info" | "warning">("info");

    const [routineTitle, setRoutineTitle] = useState<string>("");
    const [routineDescription, setRoutineDescription] = useState<string>("");

    const [rountines, setRountines] = useState([
        {
            step: "",
            content: "",
        },
    ]);

    const handleAddRountine = () => {
        setRountines([
            ...rountines,
            {
                step: "",
                content: "",
            },
        ]);
    };

    const handleDeleteRountine = (indexRountine: number) => {
        const newRountines = rountines.filter((_, index: number) => index !== indexRountine);
        setRountines(newRountines);
    };

    const handleChangeRountine = (indexRountine: number, field: "content" | "step", value: any) => {
        const newRountines = [...rountines];
        newRountines[indexRountine][field] = value;
        setRountines(newRountines);
    };

    const [selectedSkinType, setSelectedSkinType] = useState<number | null>(null);

    const handleChangeSkinType = (event: React.ChangeEvent<HTMLInputElement>) => {
        setSelectedSkinType(parseInt(event.target.value));
    };

    const handleSubmit = async () => {
        setLoading(true);

        if (!selectedSkinType) {
            setAlertMessage("Địa chỉ tài khoản không được để trống.");
            setAlertSeverity("error");
            setTimeout(() => setAlertMessage(""), 5000);
            setLoading(false);
        }

        if (!routineTitle) {
            setAlertMessage("Tiêu đề lộ trình không được để trống.");
            setAlertSeverity("error");
            setTimeout(() => setAlertMessage(""), 5000);
            setLoading(false);
        }

        if (!routineDescription) {
            setAlertMessage("Mô tả lộ trình không được để trống.");
            setAlertSeverity("error");
            setTimeout(() => setAlertMessage(""), 5000);
            setLoading(false);
        }

        if (rountines.length < 1) {
            setAlertMessage("Các bước lộ trình không được để trống.");
            setAlertSeverity("error");
            setTimeout(() => setAlertMessage(""), 5000);
            setLoading(false);
        }

        const data = {
            skinType: selectedSkinType,
            title: routineTitle,
            description: routineDescription,
            rountineStep: rountines
        };

        const responese = await fetch('https://freshskinweb.onrender.com/admin/skin-care-routines/create', {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(data)
        })

        const dataResponse = await responese.json();
        if (dataResponse.code === 200) {
            setAlertMessage(dataResponse.message);
            setAlertSeverity("success");
            setTimeout(() => location.reload(), 2000);
        } else {
            setAlertMessage(dataResponse.message);
            setAlertSeverity("error");
            setLoading(false);
            setTimeout(() => {
                setAlertMessage(""); 
            }, 2000);
        }
    };

    return (
        <>
            {alertMessage && (
                <Alert severity={alertSeverity} sx={{ position: "fixed", width: "600px", height: "60px", right: "5%", top: "5%", fontSize: "16px", zIndex: "999999" }}>
                    {alertMessage}
                </Alert>
            )}

            {permissions?.includes("rountine_create") && (
                <div>
                    <Typography variant="h5" sx={{ mb: 1, pt: 2 }}>
                        Chọn loại da
                    </Typography>
                    <RadioGroup row value={selectedSkinType?.toString() ?? ""} onChange={handleChangeSkinType}>
                        {listSkinType.map((item: any, index: number) => (
                            <FormControlLabel
                                key={index}
                                value={item.id.toString()}
                                control={<Radio />}
                                label={item.type}
                            />
                        ))}
                    </RadioGroup>

                    <TextField
                        label="Tiêu đề lộ trình"
                        variant="outlined"
                        fullWidth
                        value={routineTitle}
                        onChange={(e) => setRoutineTitle(e.target.value)}
                        sx={{ mb: 2, mt: 2, bgcolor: "white" }}
                    />

                    <TinyEditor value={routineDescription} onEditorChange={(content: string) => setRoutineDescription(content)} />

                    <Box sx={{ display: "flex", flexDirection: "column", gap: 3, mt: 3 }}>
                        {rountines.map((item: any, index: number) => (
                            <Paper key={index} elevation={2} sx={{ padding: 2, backgroundColor: "#fdfdfd" }}>
                                <Typography variant="subtitle1" fontWeight="bold" gutterBottom>
                                    Bước {index + 1}
                                </Typography>

                                <Box sx={{ display: "flex", flexDirection: { xs: "column", md: "row" }, gap: 2 }}>
                                    <TextField
                                        label="Tiêu đề mỗi bước"
                                        variant="outlined"
                                        value={item.title}
                                        onChange={(e) => handleChangeRountine(index, "step", e.target.value)}
                                        sx={{ width: { xs: "100%", md: "25%" } }}
                                    />
                                    <TinyEditor value={item.content} onEditorChange={(content: string) => handleChangeRountine(index, "content", content)} />
                                    <TextField
                                        label="Nội dung"
                                        variant="outlined"
                                        multiline
                                        minRows={2}
                                        value={item.content}
                                        onChange={(e) => handleChangeRountine(index, "content", e.target.value)}
                                        sx={{ flex: 1 }}
                                    />
                                </Box>

                                <Box sx={{ display: "flex", justifyContent: "flex-end", mt: 2 }}>
                                    <Button variant="outlined" color="error" onClick={() => handleDeleteRountine(index)}>
                                        Xoá bước
                                    </Button>
                                </Box>
                            </Paper>
                        ))}

                        <Button onClick={handleAddRountine} variant="contained" color="secondary" sx={{ alignSelf: "flex-start" }}>
                            + Thêm bước
                        </Button>
                    </Box>

                    <Box sx={{ py: 4 }}>
                        <Button
                            type='submit'
                            variant="contained"
                            color="primary"
                            sx={{ width: '100%' }}
                            disabled={loading}
                            onClick={handleSubmit}
                        >
                            {loading ? "Đang tạo mới lộ trình..." : "Tạo mới lộ trình"}
                        </Button>
                    </Box>
                </div>
            )}
        </>
    );
}
