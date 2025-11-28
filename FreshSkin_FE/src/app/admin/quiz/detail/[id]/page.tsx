"use client";

import { useParams } from "next/navigation";
import { useContext, useEffect, useState } from "react";
import {
  Box,
  Paper,
  Typography,
  Grid,
  Divider,
  Chip,
  Card,
  CardContent,
  CardHeader,
  List,
  ListItem,
  ListItemText,
} from "@mui/material";
import { ProfileAdminContext } from "@/app/admin/layout";

interface Answer {
  option: string;
  score: number;
}

interface Question {
  question: string;
  answers: Answer[];
}

export default function DetailQuizAdmin() {
  const dataProfile = useContext(ProfileAdminContext);
  const permissions = dataProfile?.permissions;
  const { id } = useParams();
  const [data, setData] = useState<any>(null);
  const [questions, setQuestions] = useState<Question[]>([]);
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

  if (!data) {
    return <Typography>Loading...</Typography>;
  }

  return (
    <>
      {permissions?.includes("quiz_view") && (
        <Box sx={{ padding: 3 }}>
          {/* Title and Description */}
          <Paper sx={{ padding: 3, marginBottom: 3 }}>
            <Typography variant="h5" gutterBottom>
              Chi tiết bộ câu hỏi
            </Typography>
            <Typography variant="h6" gutterBottom>
              Tiêu đề: {data.title}
            </Typography>
            <Typography variant="body1" paragraph>
              Mô tả: {data.description}
            </Typography>
            <Divider sx={{ marginY: 2 }} />
            <Grid container spacing={2}>
              <Grid item xs={12} md={6}>
                <Typography variant="subtitle1">Trạng thái:</Typography>
                <Chip
                  label={data.status}
                  color={data.status === "ACTIVE" ? "success" : "error"}
                />
              </Grid>
            </Grid>
          </Paper>

          {/* Questions Section */}
          <Typography variant="h6" gutterBottom>
            Các câu hỏi:
          </Typography>
          {questions.map((question, index) => (
            <Card key={index} sx={{ marginBottom: 3 }}>
              <CardHeader
                title={`Câu hỏi ${index + 1}: ${question.question}`}
              />
              <CardContent>
                <List>
                  {question.answers.map((answer, answerIndex) => (
                    <ListItem key={answerIndex}>
                      <ListItemText
                        primary={answer.option}
                        secondary={`Điểm: ${answer.score}`}
                      />
                    </ListItem>
                  ))}
                </List>
              </CardContent>
            </Card>
          ))}
        </Box>
      )}
    </>
  );
}
