"use client";

import { useEffect, useState } from "react";
import { useParams } from "next/navigation";
import MiddlewareGetData from "./MiddlewareGetData";

interface Variants {
    id: number,
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
    slug: string;
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

export default function DetailProductPage() {
    const { slug } = useParams();
    const [isLoading, setIsLoading] = useState(true);
    const [data, setData] = useState<Context>({
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

    useEffect(() => {
        const fetchDetailProduct = async () => {
            const response = await fetch(`https://freshskinweb.onrender.com/home/products/${slug}`);
            const data = await response.json();

            setData(data.data[0]);
            setIsLoading(false);
        };

        fetchDetailProduct();
    }, [slug]);

    if (isLoading) {
        return <div>Loading...</div>;
    }

    return (
        <MiddlewareGetData data={data} />
    );
}
