"use client"

import { Box, Button, Paper, TextField, Typography } from "@mui/material";
import { useParams } from "next/navigation";
import { useContext, useEffect, useState } from "react";
import Alert from '@mui/material/Alert';
import dynamic from "next/dynamic";
import { ProfileAdminContext } from "@/app/admin/layout";

const TinyEditor = dynamic(() => import("../../../../../../TinyEditor"), {
    ssr: false,
});

export default function EditRouteSkinAdminPage() {
    const { id } = useParams();
    const dataProfile = useContext(ProfileAdminContext);
    const permissions = dataProfile?.permissions;

    const [skinType, setSkinType] = useState<string>("");
    const [routineTitle, setRoutineTitle] = useState<string>("");
    const [routineDescription, setRoutineDescription] = useState<string>("");
    const [rountines, setRountines] = useState<any>([]);

    const [loading, setLoading] = useState(false);
    const [alertMessage, setAlertMessage] = useState<string>("");
    const [alertSeverity, setAlertSeverity] = useState<"success" | "error" | "info" | "warning">("info");

    useEffect(() => {
        const fetchData = async () => {
            const response = await fetch(`https://freshskinweb.onrender.com/admin/skin-care-routines/${id}`);
            const data = await response.json();
            setRountines(data.data.rountineStep);
            setRoutineTitle(data.data.title);
            setRoutineDescription(data.data.description);
            setSkinType(data.data.skinType.type);
        }

        fetchData();
    }, []);

    for (const item of rountines) {
        delete item["id"];
        delete item["product"];
    }

    const handleSubmit = async () => {
        setLoading(true);
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
            title: routineTitle,
            description: routineDescription,
            rountineStep: rountines
        };
        
        console.log(data);
        const response = await fetch(`https://freshskinweb.onrender.com/admin/skin-care-routines/update/${id}`, {
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
            setLoading(false);
            setTimeout(() => {
                setAlertMessage(""); 
            }, 2000);
        }
    }


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
        const newRountines = rountines.filter((_: any, index: number) => index !== indexRountine);
        setRountines(newRountines);
    };

    const handleChangeRountine = (indexRountine: number, field: "content" | "step", value: any) => {
        const newRountines = [...rountines];
        newRountines[indexRountine][field] = value;
        setRountines(newRountines);
    };

    return (
        <>
            {alertMessage && (
                <Alert severity={alertSeverity} sx={{ position: "fixed", width: "600px", height: "60px", right: "5%", top: "5%", fontSize: "16px", zIndex: "999999" }}>
                    {alertMessage}
                </Alert>
            )}

            {permissions?.includes("rountine_edit") && (
                <div>
                    <Typography variant="h5" sx={{ mb: 1, pt: 2 }}>
                        Chỉnh sửa {skinType}
                    </Typography>

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
                                        value={item.step}
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
                            {loading ? "Đang cập nhật lộ trình..." : "Chỉnh sửa lộ trình"}
                        </Button>
                    </Box>
                </div>
            )}
        </>
    )
}