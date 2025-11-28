interface initState {
    email: string,
    firstName: string,
    lastName: string,
    phoneNumber: string,
    address: string,
    totalAmount: number,
    totalPrice: number,
    paymentMethod: string,
    provinceChoosen: boolean,
    feeShip: number
}

const initialState: initState = {
    email: "",
    firstName: "",
    lastName: "",
    phoneNumber: "",
    address: "",
    totalAmount: 0,
    totalPrice: 0,
    paymentMethod: "",
    provinceChoosen: false,
    feeShip: 0
}

export const orderReducer = (state = initialState, action: any) => {
    switch (action.type) {
        case "PROVINCE_CHOOSEN":
            return {
                ...state,
                provinceChoosen: action.provinceChoosen
            };
        case "FEE_SHIP":
            return {
                ...state,
                feeShip: action.feeShip
            };
        default:
            return state;
    }
}