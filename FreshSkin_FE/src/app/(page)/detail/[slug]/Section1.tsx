"use client"

import Link from "next/link";
import Image from "./Image";
import Information from "./Information";
import { MdNavigateNext } from "react-icons/md";
import { useContext } from "react";
import { Context } from "./MiddlewareGetData";

export default function Section1() {
    const { productDetail } = useContext(Context);

    return (
        <>
            <ul className="flex items-center mt-[17px] container mx-auto px-3 whitespace-nowrap">
                <li>
                    <Link href="/" className="flex items-center flex-nowrap">
                        <span className="text-[#333] text-[15px] font-[400] hover:text-secondary">Trang chủ</span>
                        <span><MdNavigateNext className="ml-[10px] text-[18px] mr-[10px]" /></span>
                    </Link>
                </li>
                {productDetail.category.length > 0 && productDetail.category[0].parent && productDetail.category[0]?.parent?.slug !== "" && (
                    <li>
                        <Link href={`/product-category/${productDetail.category[0].parent.slug}`} className="flex items-center">
                            <span className="text-[#333] text-[15px] font-[400] hover:text-secondary">{productDetail.category[0]?.parent?.title}</span>
                            <span><MdNavigateNext className="ml-[10px] text-[18px] mr-[10px]" /></span>
                        </Link>
                    </li>
                )}
                <li>
                    <Link href={`/product-category/${productDetail.category[0]?.slug}`} className="flex items-center">
                        <span className="text-[#333] text-[15px] font-[400] hover:text-secondary">{productDetail.category[0]?.title}</span>
                        <span><MdNavigateNext className="ml-[10px] text-[18px] mr-[10px]" /></span>
                    </Link>
                </li>
                <li className="text-secondary text-[15px] font-[400] line-clamp-[1]">
                    {productDetail.title}
                </li>
            </ul>
            <div className="container mx-auto flex mt-[25px] items-center">
                <Image />
                <Information />
            </div>
        </>

    );
}
