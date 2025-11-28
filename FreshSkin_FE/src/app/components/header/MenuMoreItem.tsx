"use client"

import { Url } from "next/dist/shared/lib/router/router";
import Link from "next/link";

export default function MenuMoreItem(props: { text: string, icon: string, link: Url }) {
    const { text = "", icon = "", link = "" } = props;

    return (
        <>
            <li className="flex items-center p-[10px] border-b border-solid border-[#f0f0f0] bg-transparent">
                <div className="w-[24px] h-[24px]">
                    <img src={icon} className="w-full h-full object-cover" />
                </div>
                <Link href={link}>
                    <span className="text-[14px] font-[400] text-[#00090f] ml-[10px] hover:text-primary">{text}</span>
                </Link>
            </li>
        </>
    )
}