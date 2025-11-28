"use client"

import { Box, Button, Paper, TextField, Typography } from "@mui/material";
import { useContext, useState } from "react";
import { MdDeleteForever } from "react-icons/md";
import Alert from '@mui/material/Alert';
import { ProfileAdminContext } from "../../layout";
interface Answer {
    option: string;
    score: number;
}

interface Question {
    question: string;
    answers: Answer[];
}

export default function CreateQuizAdminPage() {
    const dataProfile = useContext(ProfileAdminContext);
    const permissions = dataProfile?.permissions;
    const [alertMessage, setAlertMessage] = useState<string>("");
    const [alertSeverity, setAlertSeverity] = useState<"success" | "error" | "info" | "warning">("info");
    const handleSubmit = async (event: any) => {
        event.preventDefault();

        const data = {
            title: event.target.title.value,
            description: event.target.description.value,
            questions: questions.map(q => ({
                questionText: q.question,
                answers: q.answers.map(a => ({
                    option: a.option,
                    score: a.score ? Number(a.score) : 0,
                })),
            })),
        }

        const response = await fetch('https://freshskinweb.onrender.com/admin/question/group/create', {
            method: "POST",
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

    const [questions, setQuestions] = useState<Question[]>([
        { question: "", answers: [{ option: "", score: 0 }] }
    ]);

    const handleAddQuestion = () => {
        setQuestions([
            ...questions,
            { question: "", answers: [{ option: "", score: 0 }] }
        ]);
    };

    const handleRemoveQuestion = (indexRemove: number) => {
        const newQuestions = questions.filter((_, index) => index !== indexRemove);
        setQuestions(newQuestions);
    };

    const handleInputChange = (index: number, field: 'question' | 'answeroption' | 'answerscore', value: string | number, answerIndex?: number) => {
        const newQuestions = [...questions];
        if (field === "question") {
            newQuestions[index][field] = value as string;
        } else if (field === "answeroption" && answerIndex !== undefined) {
            newQuestions[index].answers[answerIndex].option = value as string;
        } else if (field === "answerscore" && answerIndex !== undefined) {
            newQuestions[index].answers[answerIndex].score = value ? parseFloat(value as string) : 0;
        }
        setQuestions(newQuestions);
    };

    const handleAddAnswer = (questionIndex: number) => {
        const newQuestions = [...questions];
        newQuestions[questionIndex].answers.push({ option: "", score: 0 });
        setQuestions(newQuestions);
    };

    const handleRemoveAnswer = (questionIndex: number, answerIndex: number) => {
        const newQuestions = [...questions];
        newQuestions[questionIndex].answers = newQuestions[questionIndex].answers.filter((_, index) => index !== answerIndex);
        setQuestions(newQuestions);
    };

    return (
        <>
            {permissions?.includes("quiz_create") && (
                <Box sx={{ padding: 3, backgroundColor: '#ffffff' }}>
                    <Typography variant="h5" gutterBottom>
                        Trang tạo mới bộ câu hỏi
                    </Typography>
                    {
                        alertMessage && (
                            <Alert severity={alertSeverity} sx={{ mb: 2 }}>
                                {alertMessage}
                            </Alert>
                        )
                    }

                    <Paper elevation={3} sx={{ padding: 3, marginBottom: 2 }}>
                        <form onSubmit={handleSubmit}>
                            <TextField
                                label="Bộ đề"
                                name="title"
                                variant="outlined"
                                fullWidth
                                sx={{ marginBottom: 3 }}
                                required
                            />
                            <TextField
                                label="Mô tả bộ đề"
                                name="description"
                                variant="outlined"
                                fullWidth
                                sx={{ marginBottom: 3 }}
                                required
                            />
                            <Box sx={{ marginTop: "20px", marginBottom: "20px" }}>
                                {questions.map((question, questionIndex) => (
                                    <Box key={questionIndex} sx={{ marginBottom: 3 }}>
                                        <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                                            <TextField
                                                label="Câu hỏi"
                                                variant="outlined"
                                                size="small"
                                                value={question.question}
                                                onChange={(e) => handleInputChange(questionIndex, 'question', e.target.value)}
                                                sx={{ width: "80%", mr: 5 }}
                                            />
                                            <MdDeleteForever onClick={() => handleRemoveQuestion(questionIndex)} className='text-red-400 text-[25px] ml-[10px] cursor-scoreer' />

                                        </Box>
                                        {question.answers.map((answer, answerIndex) => (
                                            <Box key={answerIndex} sx={{ display: 'flex', alignItems: 'center', mb: 2, pl: 5 }}>
                                                <TextField
                                                    label="Đáp án"
                                                    variant="outlined"
                                                    size="small"
                                                    value={answer.option}
                                                    onChange={(e) => handleInputChange(questionIndex, 'answeroption', e.target.value, answerIndex)}
                                                    sx={{ mr: 1, width: "700px" }}
                                                />
                                                <Typography variant="h6">:</Typography>
                                                <TextField
                                                    label="Điểm"
                                                    type="number"
                                                    variant="outlined"
                                                    size="small"
                                                    value={answer.score}
                                                    onChange={(e) => handleInputChange(questionIndex, 'answerscore', e.target.value, answerIndex)}
                                                    sx={{ ml: 1, width: "100px" }}
                                                />
                                                <MdDeleteForever onClick={() => handleRemoveAnswer(questionIndex, answerIndex)} className='text-red-400 text-[25px] ml-[20px] cursor-scoreer' />
                                                <Button variant="contained" sx={{ marginLeft: 5 }} onClick={() => handleAddAnswer(questionIndex)}>
                                                    Thêm đáp án
                                                </Button>
                                            </Box>
                                        ))}
                                        <Button variant="contained" onClick={handleAddQuestion}>
                                            Thêm câu hỏi
                                        </Button>
                                    </Box>
                                ))}
                            </Box>
                            <Button type="submit" variant="contained" color="primary" sx={{ width: '100%' }}>
                                Tạo mới bộ đề
                            </Button>
                        </form>
                    </Paper>
                </Box>
            )}
        </>
    );
}
