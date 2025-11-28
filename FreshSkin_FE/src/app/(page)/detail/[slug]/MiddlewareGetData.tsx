"use client"

import React, { createContext } from "react";
import Section1 from "./Section1";
import Section2 from "./Section2";
import SaleCode from "@/app/components/SaleCode/SaleCode";
import Rating from "./Rating";

interface Variants {
    id: number;
    volume: number;
    price: number;
    unit: string;
}
interface Brand {
    title: string;
}
interface Category {
    slug: string,
    title: string,
    parent?: Category
}
interface ProductRelated {
    title: string;
    slug: string;
    description: string;
    thumbnail: string[];
    variants: Variants[];
    discountPercent: number;
}
interface ProductDetail {
    id: number,
    thumbnail: string[];
    category: Category[],
    deal: string;
    banner: string;
    title: string;
    slug: string,
    brand: Brand;
    variants: Variants[];
    discountPercent: number;
    description: string;
    origin: string;
    skinIssues: string;
    ingredients: string;
    usageInstructions: string;
}

interface Context {
    productsRelated: ProductRelated[],
    productDetail: ProductDetail
}

export const Context = createContext<Context>({
    productsRelated: [
        {
            title: "",
            slug: "",
            description: "",
            thumbnail: [],
            variants: [{ id: 0, volume: 0, price: 0, unit: "" }],
            discountPercent: 0,
        }
    ],
    productDetail: {
        id: 0,
        thumbnail: [],
        category: [],
        deal: "",
        banner: "",
        title: "",
        slug: "",
        brand: { title: "" },
        variants: [{ id: 0, volume: 0, price: 0, unit: "" }],
        discountPercent: 0,
        description: "",
        origin: "",
        skinIssues: "",
        ingredients: "",
        usageInstructions: ""
    }
});

interface MiddlewareGetDataProps {
    data: Context;
}

export default function MiddlewareGetData({ data }: MiddlewareGetDataProps) {
    return (
        <Context.Provider value={{
            productsRelated: data.productsRelated,
            productDetail: data.productDetail
        }}>
            <Section1 />
            <SaleCode />
            <Section2 />
            <Rating />
        </Context.Provider>
    );
}
