export const quizAnswer = (question: string, answer: string) => {
    return {
        type: "ANSWER_QUESTION",
        question: question,
        answer: answer
    }
}

export const quizReset = () => {
    return {
        type: "QUIZ_RESET"
    }
}