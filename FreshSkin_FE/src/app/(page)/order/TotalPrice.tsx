"use client"

import Link from "next/link";
import { IoIosArrowBack } from "react-icons/io";
import { useSelector } from "react-redux";

export default function TotalPrice() {
    const totalPrice = useSelector((state: any) => (state.cartReducer.totalPriceInit));
    const priceVoucher = useSelector((state: any) => (state.cartReducer.priceVoucher));
    const feeShip = useSelector((state: any) => (state.orderReducer.feeShip));

    return (
        <>
            <div className="ml-[28px]">
                <div className="flex items-center justify-between mt-[15px]">
                    <span className="text-[16px] text-[#737373]">Tổng cộng</span>
                    {priceVoucher > 0 ? (
                        <span className="text-[20px] text-[#2a9dcc]">{(priceVoucher + feeShip).toLocaleString("en-US")}<sup className="underline">đ</sup></span>
                    ) : (
                        <span className="text-[20px] text-[#2a9dcc]">{(totalPrice + feeShip).toLocaleString("en-US")}<sup className="underline">đ</sup></span>
                    )}
                </div>
                <div className="mt-[15px] flex justify-between items-center">
                    <Link href="/cart" className="text-[14px] text-[#2a9dcc] hover:text-[#2a6395] flex items-center group">
                        <div className="relative">
                            <IoIosArrowBack className="text-[16px] transition-transform group-hover:-translate-x-1" />
                        </div>
                        <span className="ml-[2px]">Quay về giỏ hàng</span>
                    </Link>
                    <button type="submit" className="uppercase rounded-[4px] ml-[12px] mb-[10px] bg-[#72a834] hover:bg-[#557E27] border border-solid border-[#72a834] hover:border-[#557E27] text-white text-[14px] font-[450] h-[44px] px-[22px]">
                        Đặt hàng
                    </button>
                </div>
            </div>
        </>
    )
}