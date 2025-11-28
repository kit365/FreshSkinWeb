"use client"

import Link from "next/link";

export default function Title(props: { title: string, link?: string }) {
    const { title = "", link = "" } = props;
    
    return (
        <>
            <Link href={link} className="relative hover:text-[#4E7661]">
                <div className="uppercase text-[26px] text-center z-1 py-[40px] font-[500] ">{title}</div>
                <img src="signature.webp" className="absolute left-[43%] bottom-[15%]" />
            </Link>
        </>
    )
}