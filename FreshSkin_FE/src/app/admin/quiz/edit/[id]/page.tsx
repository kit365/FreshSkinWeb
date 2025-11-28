"use client";

import { Box, Button, Paper, TextField, Typography } from "@mui/material";
import { useParams } from "next/navigation";
import { useEffect, useState, useCallback, useContext } from "react";
import { MdDeleteForever } from "react-icons/md";
import Alert from '@mui/material/Alert';
import { ProfileAdminContext } from "@/app/admin/layout";
interface Answer {
  option: string;
  score: number;
}

interface Question {
  question: string;
  answers: Answer[];
}

export default function EditQuizAdminPage() {
  const { id } = useParams();
  const dataProfile = useContext(ProfileAdminContext);
  const permissions = dataProfile?.permissions;
  const [data, setData] = useState<any>(null);
  const [loading, setLoading] = useState(false);
  const [questions, setQuestions] = useState<Question[]>([]);
  const [alertMessage, setAlertMessage] = useState<string>("");
  const [alertSeverity, setAlertSeverity] = useState<"success" | "error" | "info" | "warning">("info");
  useEffect(() => {
    const fetchInfo = async () => {
      const response = await fetch(
        `https://freshskinweb.onrender.com/admin/question/group/${id}`
      );
      const result = await response.json();
      const groupData = result.data;

      const mappedQuestions = groupData.questions.map((question: any) => ({
        question: question.questionText,
        answers: question.answers.map((answer: any) => ({
          option: answer.option ? answer.option.toString() : "",
          score: answer.score,
        })),
      }));

      setQuestions(mappedQuestions);
      setData(groupData);
    };

    fetchInfo();
  }, [id]);

  const handleInputChange = useCallback(
    (index: number, field: "question" | "answeroption" | "answerscore", value: string | number, answerIndex?: number) => {
      setQuestions((prevQuestions) => {
        const newQuestions = [...prevQuestions];
        if (field === "question") {
          newQuestions[index][field] = value as string;
        } else if (field === "answeroption" && answerIndex !== undefined) {
          newQuestions[index].answers[answerIndex].option = value as string;
        } else if (field === "answerscore" && answerIndex !== undefined) {
          newQuestions[index].answers[answerIndex].score = value
            ? parseFloat(value as string)
            : 0;
        }
        return newQuestions;
      });
    },
    []
  );

  const handleSubmit = async (event: any) => {
    event.preventDefault();
    setLoading(true);

    if (!data.title) {
      setAlertMessage("Tiêu đề bộ đề không được để trống.");
      setAlertSeverity("error");
      setTimeout(() => setAlertMessage(""), 5000);
      setLoading(false);
      return;
    }

    if (!data.description) {
      setAlertMessage("Mô tả bộ đề không được để trống.");
      setAlertSeverity("error");
      setTimeout(() => setAlertMessage(""), 5000);
      setLoading(false);
      return;
    }

    if (questions.length < 1) {
      setAlertMessage("Câu hỏi bộ đề không được để trống.");
      setAlertSeverity("error");
      setTimeout(() => setAlertMessage(""), 5000);
      setLoading(false);
      return;
    }

    const dataSubmit = {
      title: data.title,
      description: data.description,
      questions: questions.map((q) => ({
        questionText: q.question,
        answers: q.answers.map((a) => ({
          option: a.option,
          score: a.score ? Number(a.score) : 0,
        })),
      })),
    };

    const response = await fetch(
      `https://freshskinweb.onrender.com/admin/question/group/edit/${id}`,
      {
        method: "PATCH",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(dataSubmit),
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

  const handleAddQuestion = useCallback(() => {
    setQuestions((prevQuestions) => [
      ...prevQuestions,
      { question: "", answers: [{ option: "", score: 0 }] },
    ]);
  }, []);

  const handleRemoveQuestion = useCallback((indexRemove: number) => {
    setQuestions((prevQuestions) =>
      prevQuestions.filter((_, index) => index !== indexRemove)
    );
  }, []);

  const handleAddAnswer = useCallback((questionIndex: number) => {
    setQuestions((prevQuestions) => {
      const newQuestions = [...prevQuestions];
      newQuestions[questionIndex].answers.push({ option: "", score: 0 });
      return newQuestions;
    });
  }, []);

  const handleRemoveAnswer = useCallback((questionIndex: number, answerIndex: number) => {
    setQuestions((prevQuestions) => {
      const newQuestions = [...prevQuestions];
      newQuestions[questionIndex].answers = newQuestions[questionIndex].answers.filter(
        (_, index) => index !== answerIndex
      );
      return newQuestions;
    });
  }, []);

  if (!data) {
    return <Typography>Loading...</Typography>;
  }

  return (
    <>
      {alertMessage && (
        <Alert severity={alertSeverity} sx={{ position: "fixed", width: "600px", height: "60px", right: "5%", top: "5%", fontSize: "16px", zIndex: "999999" }}>
          {alertMessage}
        </Alert>
      )}

      {permissions?.includes("quiz_edit") && (
        <Box sx={{ padding: 3, backgroundColor: "#ffffff" }}>
          <Typography variant="h5" gutterBottom>
            Trang chỉnh sửa bộ câu hỏi
          </Typography>

          {data && (
            <Paper elevation={3} sx={{ padding: 3, marginBottom: 2 }}>
              <form onSubmit={handleSubmit}>
                <TextField
                  label="Bộ đề"
                  name="title"
                  variant="outlined"
                  fullWidth
                  sx={{ marginBottom: 3 }}
                  value={data.title}
                  onChange={(e) =>
                    setData((prevData: any) => ({
                      ...prevData,
                      title: e.target.value,
                    }))
                  }
                />
                <TextField
                  label="Mô tả bộ đề"
                  name="description"
                  variant="outlined"
                  fullWidth
                  sx={{ marginBottom: 3 }}
                  value={data.description}
                  onChange={(e) =>
                    setData((prevData: any) => ({
                      ...prevData,
                      description: e.target.value,
                    }))
                  }
                />
                <Box sx={{ marginTop: "20px", marginBottom: "20px" }}>
                  {questions.map((question, questionIndex) => (
                    <Box key={questionIndex} sx={{ marginBottom: 3 }}>
                      <Box sx={{ display: "flex", alignItems: "center", mb: 2 }}>
                        <TextField
                          label="Câu hỏi"
                          variant="outlined"
                          size="small"
                          value={question.question}
                          onChange={(e) =>
                            handleInputChange(questionIndex, "question", e.target.value)
                          }
                          sx={{ width: "80%", mr: 5 }}
                        />
                        <MdDeleteForever
                          onClick={() => handleRemoveQuestion(questionIndex)}
                          className="text-red-400 text-[25px] ml-[10px] cursor-pointer"
                        />
                      </Box>
                      {question.answers.map((answer, answerIndex) => (
                        <Box
                          key={answerIndex}
                          sx={{
                            display: "flex",
                            alignItems: "center",
                            mb: 2,
                            pl: 5,
                          }}
                        >
                          <TextField
                            label="Đáp án"
                            variant="outlined"
                            size="small"
                            value={answer.option}
                            onChange={(e) =>
                              handleInputChange(
                                questionIndex,
                                "answeroption",
                                e.target.value,
                                answerIndex
                              )
                            }
                            sx={{ mr: 1, width: "700px" }}
                          />
                          <Typography variant="h6">:</Typography>
                          <TextField
                            label="Điểm"
                            type="number"
                            variant="outlined"
                            size="small"
                            value={answer.score}
                            onChange={(e) =>
                              handleInputChange(
                                questionIndex,
                                "answerscore",
                                e.target.value,
                                answerIndex
                              )
                            }
                            sx={{ ml: 1, width: "100px" }}
                          />
                          <MdDeleteForever
                            onClick={() => handleRemoveAnswer(questionIndex, answerIndex)}
                            className="text-red-400 text-[25px] ml-[20px] cursor-pointer"
                          />
                          <Button
                            variant="contained"
                            sx={{ marginLeft: 5 }}
                            onClick={() => handleAddAnswer(questionIndex)}
                          >
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
                <Box sx={{ py: 4 }}>
                  <Button
                    type='submit'
                    variant="contained"
                    color="primary"
                    sx={{ width: '100%' }}
                    disabled={loading}
                    onClick={handleSubmit}
                  >
                    {loading ? "Đang chỉnh sửa bộ câu hỏi..." : "Chỉnh sựa bộ câu hỏi"}
                  </Button>
                </Box>
              </form>
            </Paper>
          )}
        </Box>
      )}
    </>
  );
}
