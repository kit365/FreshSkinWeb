"use client"

import { LuDot } from "react-icons/lu";

export default function AboveHeader() {
    return(
        <>
            <div className="bg-[#93C9A4] py-[5px] flex justify-center text-white text-[14px] items-center">
                <div className="px-[15px]">
                    100% Chính Hãng
                </div>
                <LuDot />
                <div className="px-[15px]">
                    Thanh toán an toàn
                </div>
                <LuDot />
                <div className="px-[15px]">
                    Giá tốt nhất
                </div>
            </div>
        </>
    )
}