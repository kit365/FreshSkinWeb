"use client"

import Link from "next/link";
import { FaSquare } from "react-icons/fa6";

export default function ContentFooter(props: { title: string, link: string }) {
    const { title = "", link = "" } = props;

    return (
        <>
            <div className="px-[5px] hover:text-primary">
                <Link href={link}>
                    <FaSquare className="text-[5px] inline-flex mr-[10px]" />
                    <span className="text-[14px] font-[400]">{title}</span>
                </Link>
            </div>
        </>
    )
}