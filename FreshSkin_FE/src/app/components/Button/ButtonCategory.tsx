"use client"

import Link from "next/link";

export default function ButtonCategory(props: { text: string, slug: string }) {
    const { text = "", slug = "" } = props;

    return (
        <>
            <Link href={`/product-category/${slug}`} className="capitalize px-[14px] py-[5px] mx-[3.5px] mt-[7.5px] text-[14px] bg-[#F7F7F7] rounded-[30px] hover:bg-primary hover:text-white">{text}</Link>
        </>
    )
}