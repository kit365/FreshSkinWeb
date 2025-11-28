"use client"

import Link from "next/link";

export default function TitleFooter(props: { title: string }) {
    const { title = "" } = props;
    return (
        <>
            <Link href="">
                <div className="uppercase text-[14px] font-[600] mb-[15px] mt-[25px]">{title}</div>
            </Link>
        </>
    )
}