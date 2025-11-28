"use client"

import QuizQuestion from "@/app/components/Quiz/QuizQuestion";
import { useContext, useEffect, useState } from "react";
import { IoChevronBack, IoChevronForwardOutline } from "react-icons/io5";
import { SiTicktick } from "react-icons/si";
import { useDispatch, useSelector } from "react-redux";
import { SettingProfileContext } from "../../layout";
import { quizReset } from "@/app/(actions)/quiz";

type QuestionData = {
    question: string;
    name: string;
    answerA?: string;
    answerB?: string;
    answerC?: string;
    answerD?: string;
    valueA?: string; 
    valueB?: string;
    valueC?: string;
    valueD?: string;
};

type Answer = {
    option: string;
    score: number;
};

type Question = {
    questionText: string;
    answers: Answer[];
};

type Quiz = {
    questions: Question[];
    id: number;
};

export default function QuizQuestionPage() {
    const [currentQuestion, setCurrentQuestion] = useState(1);
    const listAnswers = useSelector((state: any) => state.quizReducer);
    const [choosenQuiz, setChoosenQuiz] = useState<Quiz>({
        questions: [],
        id: 0
    });
    const [isLoading, setIsLoading] = useState(true);
    const dispatchQuiz = useDispatch();

    useEffect(() => {
        const fetchQuiz = async () => {
            const response = await fetch("https://freshskinweb.onrender.com/admin/question/group");
            const dataResponse = await response.json();
            if (dataResponse.data.QuestionGroup.length > 0) {
                const randomIndex = Math.floor(Math.random() * dataResponse.data.QuestionGroup.length);
                setChoosenQuiz(dataResponse.data.QuestionGroup[randomIndex]);
            }
            setIsLoading(false);
        };

        fetchQuiz();
    }, []);

    const settingProfile = useContext(SettingProfileContext);
        
    if (!settingProfile) {
        return null;
    }

    const { profile } = settingProfile;

    const data: QuestionData[] = [];

    choosenQuiz.questions.forEach((item: any, index: number) => {        
        const dataOneQuestion: QuestionData  = {
            question: item.questionText,
            name: `q${index+1}`,
        }

        item.answers.forEach((answer: any, answerIndex: number) => {
            if(answerIndex == 0){
                dataOneQuestion["answerA"] = answer.option;
                dataOneQuestion["valueA"] = `q${index+1}a`;
            }
            if(answerIndex == 1){
                dataOneQuestion["answerB"] = answer.option;
                dataOneQuestion["valueB"] = `q${index+1}b`;
            }
            if(answerIndex == 2){
                dataOneQuestion["answerC"] = answer.option;
                dataOneQuestion["valueC"] = `q${index+1}c`;
            }
            if(answerIndex == 3){
                dataOneQuestion["answerD"] = answer.option;
                dataOneQuestion["valueD"] = `q${index+1}d`;
            }
        })

        data.push(dataOneQuestion);
    })
        

    const handlePreviousQuestion = () => {
        if (currentQuestion > 1) {
            setCurrentQuestion(currentQuestion - 1);
        }
    }

    const handleNextQuestion = () => {
        if (currentQuestion < data.length) {
            setCurrentQuestion(currentQuestion + 1);
        }
    }


    const handleSubmitQuiz = async (event: any) => {
        event.preventDefault();
        let totalScore: number = 0;

        for(const answer in listAnswers){
            const answerResult = listAnswers[answer];
            const question: number = parseInt(answerResult[1]);
            const res: string = answerResult[answerResult.length - 1];
            const currentQuestion = choosenQuiz.questions[question-1];
            if(res == "a"){
                const currentAnswer = currentQuestion.answers[0];
                totalScore += currentAnswer.score;
            }
            else if(res == "b"){
                const currentAnswer = currentQuestion.answers[1];
                totalScore += currentAnswer.score;
            }
            else if(res == "c"){
                const currentAnswer = currentQuestion.answers[2];
                totalScore += currentAnswer.score;
            }
            else{
                const currentAnswer = currentQuestion.answers[3];
                totalScore += currentAnswer.score;
            }          
        }

        const response = await fetch('https://freshskinweb.onrender.com/admin/skin/result/create', {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                user: profile.userID,
                questionGroup: choosenQuiz.id,
                totalScore: totalScore
            })
        });

        const dataResponse = await response.json();

        if(dataResponse.code == 200){
            dispatchQuiz(quizReset());
            window.location.href = "/quiz/result";
        }

    }

    if (isLoading || choosenQuiz.questions.length === 0) {
        return <div>Loading...</div>;
    }

    return (
        <>
            <QuizQuestion
                question={data[currentQuestion - 1]?.question}
                answerA={data[currentQuestion - 1]?.answerA}
                answerB={data[currentQuestion - 1]?.answerB}
                answerC={data[currentQuestion - 1]?.answerC}
                answerD={data[currentQuestion - 1]?.answerD}
                name={data[currentQuestion - 1]?.name}
                valueA={data[currentQuestion - 1]?.valueA}
                valueB={data[currentQuestion - 1]?.valueB}
                valueC={data[currentQuestion - 1]?.valueC}
                valueD={data[currentQuestion - 1]?.valueD}
            />
            <div className="border-t-4 border-solid border-[#f7f9fc] mt-[70px] flex justify-center pt-[22px]">
                <div className="w-[568px] flex justify-end h-[40px]">
                    <div
                        onClick={() => {
                            if (currentQuestion > 1) {
                                handlePreviousQuestion();
                            }
                        }}
                        className={`px-[22px] cursor-pointer rounded-[8px] text-[#52766e] text-[14px] hover:bg-[#aed8d3] font-[600] border border-solid border-[#52766e] hover:border-[#BFBFBF] flex items-center mr-[10px] ${currentQuestion === 1 ? 'cursor-not-allowed opacity-50' : ''}`}
                    >
                        <IoChevronBack className="mr-[5px]" />
                        Trở lại
                    </div>
                    {currentQuestion != data.length ? (
                        <div
                            onClick={handleNextQuestion}
                            className="px-[22px] cursor-pointer rounded-[8px] text-white text-[14px] bg-[#9acba4] hover:bg-[#52766e] font-[600] flex items-center border border-solid"
                        >
                            Tiếp theo
                            <IoChevronForwardOutline className="ml-[5px]" />
                        </div>
                    ) : (
                        <button
                            className="px-[22px] cursor-pointer rounded-[8px] text-white text-[14px] bg-[#9acba4] hover:bg-[#52766e] font-[600] flex items-center border border-solid"
                            type="submit"
                            onClick={handleSubmitQuiz}
                        >
                            Hoàn thành
                            <SiTicktick className="ml-[5px]" />
                        </button>
                    )}
                </div>
            </div>
        </>
    )
}