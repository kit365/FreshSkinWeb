"use client"

import Link from "next/link";

export default function ButtonSeeAll(props: {
    link?: string
}) {
    
    const { link = "" } = props;

    return (
        <>
            <div className="flex justify-center">
                <Link href={link}>
                    <button className="mt-[30px] px-[30px] py-[5px] text-[14px] font-[600] border border-1 border-primary rounded-[30px] hover:bg-primary hover:text-white">Xem tất cả</button>
                </Link>
            </div>
        </>
    )
}