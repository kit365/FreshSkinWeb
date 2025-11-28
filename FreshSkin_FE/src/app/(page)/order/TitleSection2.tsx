"use client"

import { useSelector } from "react-redux";

export default function TitleSection2() {
    const quantity = useSelector((state: any) => state.cartReducer.totalQuantityInit);

    return (
        <>
            <div className="text-[18px] font-[600] text-[#333] py-[20px] px-[28px] border-b border-solid border-[#e1e1e1]">Đơn hàng ({quantity} sản phẩm)</div>
        </>
    )
}