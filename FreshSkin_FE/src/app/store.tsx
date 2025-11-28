import { combineReducers, createStore } from "redux";
import { cartReducer } from "./(reducers)/cart";
import { orderReducer } from "./(reducers)/order";
import { quizReducer } from "./(reducers)/quiz";

const allReducer = combineReducers({
    cartReducer,
    orderReducer,
    quizReducer
})

export const store = createStore(allReducer);
