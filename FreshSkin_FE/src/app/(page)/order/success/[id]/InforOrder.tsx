"use client"

import InforOderText from "./InforOrderText";
import InforOrderTitle from "./InforTitleText";
import { useContext } from "react";
import { SuccessOrderContext } from "./SuccessOrderContext";

export default function InforOrder() {
    const { email, fullname, phone, address, method } = useContext(SuccessOrderContext);

    return (
        <>
            <div className="w-full p-[16px] mx-[32px] mt-[16px] border border-solid border-[#dadada]">
                <div className="w-full flex justify-between">
                    <div className="w-[50%] pr-[16px]">
                        <InforOrderTitle title="Thông tin mua hàng" />
                        <InforOderText info={fullname} />
                        <InforOderText info={email} />
                        <InforOderText info={phone} />
                    </div>
                    <div className="w-[50%] pl-[16px]">
                        <InforOrderTitle title="Địa chỉ nhận hàng" />
                        <InforOderText info={fullname} />
                        <InforOderText info={address} />
                        <InforOderText info={phone} />
                    </div>
                </div>
                <div className="w-full flex justify-between mt-[10px]">
                    <div className="w-[50%] pr-[16px]">
                        <InforOrderTitle title="Phương thức thanh toán" />
                        <InforOderText info={(method == "QR") ? "Chuyển khoản" : "Thanh toán khi nhận hàng"} />
                    </div>
                    <div className="w-[50%] pl-[16px]">
                        <InforOrderTitle title="Phương thức vận chuyển" />
                        <InforOderText info="Giao hàng tận nơi" />
                    </div>
                </div>
            </div>
        </>
    )
}