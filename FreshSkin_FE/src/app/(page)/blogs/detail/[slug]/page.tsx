"use client"

import { useParams } from "next/navigation";
import { useEffect, useState } from "react"
import SeeMore from "../../SeeMore";
import { CiClock1 } from "react-icons/ci";
import { FaUser } from "react-icons/fa6";
import Link from "next/link";
import { MdNavigateNext } from "react-icons/md";

export default function BlogDetail() {
    const { slug } = useParams();

    const [data, setData] = useState({
        title: "",
        content: "",
        author: "",
        createdAt: "",
        blogCategory: {
            slug: "",
            title: ""
        }
    });
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        const fetchBlogDetail = async () => {
            const response = await fetch(`https://freshskinweb.onrender.com/home/blogs/${slug}`);
            const data = await response.json();
            setData(data.data);
            setIsLoading(false);
        };

        fetchBlogDetail();
    }, [])

    if (isLoading) {
        return <div>Loading...</div>;
    }

    const formatDate = (dateString: string) => {
        if (!dateString) return "";

        const isISO = dateString.includes("T");
        const date = isISO ? new Date(dateString) : new Date(dateString.split("/").reverse().join("-"));

        const days = ["Chủ Nhật", "Thứ Hai", "Thứ Ba", "Thứ Tư", "Thứ Năm", "Thứ Sáu", "Thứ Bảy"];
        const dayName = days[date.getDay()];

        const day = String(date.getDate()).padStart(2, "0");
        const month = String(date.getMonth() + 1).padStart(2, "0");
        const year = date.getFullYear();

        return `${dayName}, ${day}/${month}/${year}`;
    };

    return (
        <>
            <ul className="flex items-center mt-[17px] container mx-auto px-3">
                <li>
                    <Link href="/" className="flex items-center">
                        <span className="text-[#333] text-[15px] font-[400] hover:text-secondary">Trang chủ</span>
                        <span><MdNavigateNext className="ml-[10px] text-[18px] mr-[10px]" /></span>
                    </Link>
                </li>
                <li>
                    <Link href={`/blogs/${data.blogCategory.slug}`} className="flex items-center">
                        <span className="text-[#333] text-[15px] font-[400] hover:text-secondary">{data.blogCategory.title}</span>
                        <span><MdNavigateNext className="ml-[10px] text-[18px] mr-[10px]" /></span>
                    </Link>
                </li>
                <li className="text-secondary text-[15px] font-[400] line-clamp-1">
                    {data.title}
                </li>
            </ul>
            <div className="container mx-auto flex items-end">
                <div className="w-[75%] px-[10px]">
                    <div className="text-[24px] font-[650] my-[15px] text-[#333]">
                        {data.title}
                    </div>
                    <div className="flex">
                        <span className="flex items-center text-[14px] font-[400] text-[#acacac] mr-[15px]">
                            <CiClock1 className="mr-[5px]" />
                            {formatDate(data.createdAt)}
                        </span>
                        <span className="flex items-center text-[14px] font-[400] text-[#acacac] mr-[10px]">
                            <FaUser className="mr-[5px]" />
                            {data.author}
                        </span>
                    </div>
                    <div className="mt-[50px]" dangerouslySetInnerHTML={{ __html: data.content }}></div>
                </div>
                <SeeMore className="active" />
            </div>
        </>
    )
}