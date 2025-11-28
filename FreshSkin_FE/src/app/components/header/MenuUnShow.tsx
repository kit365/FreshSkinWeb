"use client"

import Link from "next/link";

export default function MenuUnShow(props: { text: string, image?: string, link?: string }) {
    const { text = "", image = "", link = "" } = props;

    return (
        <>
            <Link href={link}>
                <div className="px-[20px] py-[7px] text-[15px] border border-1 rounded-[8px] border-[#efefef] mr-[15px] flex hover:text-[#4e7661]">
                    {text}
                    {image && (
                        <img src={image} className="ml-[5px]" />
                    )}
                </div>
            </Link>
        </>
    )
}