"use client"

import { useSelector } from "react-redux"

export default function FeePay() {
    const totalPrice = useSelector((state: any) => state.cartReducer.totalPriceInit);
    const priceVoucher = useSelector((state: any) => state.cartReducer.priceVoucher);
    const provinceChoosen = useSelector((state: any) => state.orderReducer.provinceChoosen);
    const feeShip = useSelector((state: any) => state.orderReducer.feeShip);
    
    return (
        <>
            <div className="ml-[28px] border-b border-solid border-[#d9d9d9] pt-[30px] pb-[20px]">
                <div className="flex items-center justify-between text-[#737373] text-[14px]">
                    <span>Tạm tính</span>
                    {priceVoucher > 0 ? (
                        <span>{priceVoucher.toLocaleString("en-US")}<sup className="underline">đ</sup></span>
                    ): (
                        <span>{totalPrice.toLocaleString("en-US")}<sup className="underline">đ</sup></span>
                    )}
                </div>
                <div className="flex items-center justify-between text-[#737373] text-[14px] mt-[15px]">
                    <span>Phí vận chuyển</span>
                    {provinceChoosen ? (
                        <span>{feeShip.toLocaleString("en-US")}<sup className="underline">đ</sup></span>
                    ) : (
                        <span>-</span>
                    )}
                </div>
                <div className="flex items-center justify-between text-[#737373] text-[14px] mt-[15px]">
                    <span>Khuyến mại</span>
                    {priceVoucher > 0 ? (
                        <span>- {(totalPrice - priceVoucher).toLocaleString("en-US")}<sup className="underline">đ</sup></span>
                    ): (
                        <span>-</span>
                    )}
                </div>
            </div>
        </>
    )
}