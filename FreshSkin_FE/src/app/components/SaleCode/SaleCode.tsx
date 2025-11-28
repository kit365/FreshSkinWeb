"use client"

import { useEffect, useState } from "react";

export default function SaleCode() {
    const [data, setData] = useState<any>([]);
    const [copied, setCopied] = useState(false);

    useEffect(() => {
        const fecthData = async () => {
            const response = await fetch('https://freshskinweb.onrender.com/admin/vouchers/valid');
            const dataResponse = await response.json();
            setData(dataResponse.data)
        };

        fecthData();
    }, []);

    const dataFinal = data.filter((item: any) => item.type == "PERCENTAGE");
    

    const handleCopy = async (code: string) => {
        await navigator.clipboard.writeText(code);
        setCopied(true);
    };

    return (
        <>
            <div className="container mx-auto mt-[30px] rounded-[10px] sale-code">
                <div className="text-[22px] font-[700] text-white text-center p-[10px]">Mua nhiều giảm giá</div>
                <div className="grid grid-cols-4 gap-[15px] px-[20px] pb-[20px]">
                    {dataFinal.slice(0,4).map((item: any, index: number) => (
                        <div key={index} className="w-[296px] bg-white p-[10px] flex items-center">
                            <div className="w-[70px] aspect-square relative">
                                <img src="/demo/sale-deal.webp" className="w-full h-full" />
                                <span className="text-[18px] font-[600] text-[#ffe70c] absolute top-[33%] left-[27%]">{item.discountValue}%</span>
                            </div>
                            <div className="flex-1 ml-[10px]">
                                <div className="text-primary text-[14px] font-[700]">
                                    NHẬP MÃ: {item.name}
                                </div>
                                <div className="text-[12px] mt-[3px] font-[400]">Mã giảm {item.discountValue}% cho đơn hàng tối thiểu {item.minOrderValue.toLocaleString("en-US")
                                }đ.</div>
                                <div
                                    onClick={() => handleCopy(item.name)}
                                    className="bg-primary hover:bg-secondary cursor-pointer text-white py-[3px] px-[10px] mt-[3px] rounded-[40px] inline-flex text-[10px] font-[400]">
                                    Sao chép mã
                                </div>
                            </div>
                        </div>
                    ))}
                </div>
            </div>
        </>
    )
}