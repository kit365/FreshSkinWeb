"use client"

import { createContext } from "react";

interface Data {
    fullname: string;
    email: string;
    address: string;
    phone: string;
    quantity: number;
    totalPrice: number;
    method: string;
    date: string;
    products: any,
    discountAmount: number,
    priceShipping: number
}

export const SuccessOrderContext = createContext<Data>({
    fullname: "",
    email: "",
    address: "",
    phone: "",
    quantity: 0,
    totalPrice: 0,
    method: "",
    date: "",
    products: [],
    discountAmount: 0,
    priceShipping: 0
});
