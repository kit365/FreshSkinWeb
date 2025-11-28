"use client"

import { useSelector } from "react-redux";

interface Product {
    image: string,
    title: string,
    price: number,
    link: string,
    volume: number,
    unit: string,
    quantity: number
}

export default function Products() {
    const products = useSelector((state: any) => state.cartReducer.products);
    console.log(products);

    return (
        <>  
            <div className="pl-[28px] py-[14px] max-h-[230px] overflow-y-auto">
                {products.map((item: Product, index: number) => (
                    <div key={index} className="flex items-center relative mb-[15px]">
                        <div className="w-[50px] aspect-square bg-white rounded-[8px] border border-solid border-[rgba(0, 0, 0, .1)] relative mr-[20px]">
                            <img src={item.image} className="w-full h-full object-contain" />
                            <span className="absolute right-[-8px] top-[-7px] text-[11px] bg-[#2a9dcc] text-white w-[18px] h-[18px] flex justify-center items-center rounded-[32px]">{item.quantity}</span>
                        </div>
                        <div className="flex-1">
                            <div className="text-[14px] text-[#333] font-[400] line-clamp-2">{item.title}</div>
                            <div className="text-[12px] text-[#969696] font-[350]">{item.volume}{item.unit.toLowerCase()}</div>
                        </div>
                        <div className="text-[14px] w-[73px] text-[#969696] ml-[35px]">{(item.price * item.quantity).toLocaleString("en-US")}<sup className="underline">đ</sup></div>
                    </div>
                ))}
            </div>
        </>
    )
}