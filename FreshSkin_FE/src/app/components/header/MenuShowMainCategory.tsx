"use client"

import { Url } from "next/dist/shared/lib/router/router";
import Link from "next/link";
import { useEffect, useState } from "react";
import { MdOutlineArrowDropDown } from "react-icons/md";
import SubCategoryHeader from "../Sub-Category/SubCategoryHeader";

export default function MenuShowMainCategory(props: { text: string, link: Url }) {
    const { text = "", link = "" } = props;

    const [data, setData] = useState([]);

    useEffect(() => {
        const fetchData = async () => {
            const response = await fetch(`https://freshskinweb.onrender.com/admin/products/category/show`);
            const data = await response.json();
            setData(data.data[0].child);
        };

        fetchData();
    }, []);


    return (
        <>
            <div
                className="relative pb-[15px] mb-[-15px] sub-menu-header"
            >
                <Link href={link} className="relative">
                    <div className="px-[20px] py-[7px] text-[15px] border border-1 rounded-[8px] border-[#efefef] mr-[15px] flex items-center hover:text-[#4e7661]">
                        {text}
                        <MdOutlineArrowDropDown className="mx-[5px] text-[24px]" />
                    </div>
                </Link>
                <SubCategoryHeader items={data} />
            </div>

        </>
    )
}