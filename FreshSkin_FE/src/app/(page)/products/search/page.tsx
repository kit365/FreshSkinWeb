"use client"

import Banner2 from "@/app/components/Banner/Banner2";
import Section2 from "./Section2";
import { useEffect, useState } from "react";
import { useSearchParams } from "next/navigation"; 

export default function ProductsSearchPage() {
    const [data, setData] = useState<any>(null);
    const searchParams = useSearchParams();  

    useEffect(() => {
        const fetchData = async () => {
            const keyword = searchParams.get("keyword") || "";
            console.log(keyword);
            const page = searchParams.get("page");

            const linkApi = `https://freshskinweb.onrender.com/home/search?keyword=${keyword}`;
            const api = new URL(linkApi);

            if (page) {
                api.searchParams.set("page", page);
            }

            const response = await fetch(api.href);
            const data = await response.json();
            setData(data.data);
        };

        fetchData();
    }, [searchParams]); 

    if (!data) {
        return <div>Loading...</div>;
    }

    return (
        <>
            <Banner2 />
            <Section2 data={data} />
        </>
    );
}
