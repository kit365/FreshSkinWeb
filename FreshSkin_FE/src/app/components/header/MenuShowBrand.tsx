"use client"

import { Url } from "next/dist/shared/lib/router/router";
import Link from "next/link";
import { useEffect, useState } from "react";
import { MdOutlineArrowDropDown } from "react-icons/md";

export default function MenuShowBrand(props: { text: string, link: Url }) {
    const { text = "", link = "" } = props;
    const [isOpen, setIsOpen] = useState(false);

    const handleMouseEnter = () => setIsOpen(true);
    const handleMouseLeave = () => setIsOpen(false);

    const [data, setData] = useState([]);

    useEffect(() => {
        const fetchData = async () => {
            const response = await fetch(`https://freshskinweb.onrender.com/home`);
            const data = await response.json();
            setData(data.AllBrand);
        };

        fetchData();
    }, []);


    return (
        <>
            <div
                className="relative pb-[15px] mb-[-15px]"
                onMouseEnter={handleMouseEnter}
                onMouseLeave={handleMouseLeave}
            >
                <Link href={link} className="relative">
                    <div className="px-[20px] py-[7px] text-[15px] border border-1 rounded-[8px] border-[#efefef] mr-[15px] flex items-center hover:text-[#4e7661]">
                        {text}
                        <MdOutlineArrowDropDown className="mx-[5px] text-[24px]" />
                    </div>
                </Link>
                {isOpen && (
                    <ul className="bg-white absolute top-[90%] left-0 w-[220px] z-[1030] drop-header">
                        {data.slice(0, 13).map((item: any, index: number) => (
                            <Link key={index} href={`/product-category/${item.slug}`}>
                                <li
                                    className="cursor-pointer py-[8px] pl-[15px] pr-[10px] text-[#222] hover:text-secondary drop-header-item text-[15px] hover:border-l-4 hover:border-solid hover:border-secondary"
                                >
                                    {item.title}
                                </li>
                            </Link>

                        ))}
                    </ul>
                )}
            </div>

        </>
    )
}