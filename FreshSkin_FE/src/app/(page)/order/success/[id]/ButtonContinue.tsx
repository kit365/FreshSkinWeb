"use client"

import Link from "next/link";

export default function ButtonContinue() {
    return (
        <>
            <div className="rounded-[4px] ml-[60%] w-[206px] cursor-pointer mt-[45px] bg-[#72a834] hover:bg-[#557E27] border border-solid border-[#72a834] hover:border-[#557E27] text-white text-[17px] font-[450] py-[18px] text-center">
                <Link href="/">
                    Tiếp tục mua hàng
                </Link>
            </div>
        </>
    )
}