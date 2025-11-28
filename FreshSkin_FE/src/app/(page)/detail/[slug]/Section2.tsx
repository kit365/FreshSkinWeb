"use client"

import { useContext, useState } from "react";
import { Context } from "./MiddlewareGetData";
import Link from "next/link";

export default function Section2() {
    const { productDetail, productsRelated } = useContext(Context);

    const [dataCurrent, setDataCurrent] = useState("Mô tả sản phẩm")

    return (
        <>
            <div className="container mt-[50px] mx-auto flex">
                <div className="px-[10px] w-[75%]">
                    <ul className="text-[16px] flex font-[600] border-b border-solid border-[#dee2e6]">
                        <li onClick={() => setDataCurrent("Mô tả sản phẩm")} className={`cursor-pointer pb-[8px] ` + (dataCurrent == "Mô tả sản phẩm" ? "text-primary border-b-2 border-solid border-primary" : "text-textColor")}>Mô tả sản phẩm</li>
                        <li onClick={() => setDataCurrent("Thành phần sản phẩm")} className={`ml-[35px] cursor-pointer ` + (dataCurrent == "Thành phần sản phẩm" ? "text-primary border-b-2 border-solid border-primary" : "text-textColor")}>Thành phần sản phẩm</li>
                        <li onClick={() => setDataCurrent("Hướng dẫn sử dụng")} className={`ml-[35px] cursor-pointer ` + (dataCurrent == "Hướng dẫn sử dụng" ? "text-primary border-b-2 border-solid border-primary" : "text-textColor")}>Hướng dẫn sử dụng</li>
                    </ul>

                    {dataCurrent == "Thành phần sản phẩm" && (
                        <div className="py-[10px] text-[14px]" dangerouslySetInnerHTML={{ __html: productDetail.ingredients }}></div>
                    )}
                    {dataCurrent == "Mô tả sản phẩm" && (
                        <div className="py-[10px] text-[14px]" dangerouslySetInnerHTML={{ __html: productDetail.description }}></div>
                    )}
                    {dataCurrent == "Hướng dẫn sử dụng" && (
                        <div className="py-[10px] text-[14px]" dangerouslySetInnerHTML={{ __html: productDetail.usageInstructions }}></div>
                    )}
                </div>
                <div className="px-[10px] flex-1">
                    <div className="uppercase text-[15px] font-[600] border-b border-solid border-[#dee2e6] py-[8px]">Sản phẩm liên quan</div>
                    <div className="p-[5px]">
                        {productsRelated.slice(0, 5).map((item: any, index: number) => (
                            <Link key={index} href={`/detail/${item.slug}`}>
                                <div className="py-[4px] flex items-center border-b border-solid border-[#ededed]">
                                    <div className="w-[60px] aspect-square">
                                        <img src={item.thumbnail[0]} className="w-full h-full object-cover" />
                                    </div>
                                    <div className="flex-1 ml-[10px]">
                                        <div className="text-[14px] pb-[3px] text-textColor font-[500] line-clamp-2">{item.title}</div>
                                        <div className="flex">
                                            <span className="text-[14px] text-primary pr-[10px]">{(item.variants[0].price * (1 - item.discountPercent / 100)).toLocaleString()}<sup className="underline">đ</sup></span>
                                            {item.discountPercent > 0 && (
                                                <span className="text-[12px] text-[#9e9e9e] line-through mt-[3px] font-[400]">{(item.variants[0].price).toLocaleString()}<sup className="underline">đ</sup></span>
                                            )}
                                        </div>
                                    </div>
                                </div>
                            </Link>
                        ))}
                        {productsRelated[5] && (
                            <Link href={`/detail/${productsRelated[5].slug}`}>
                                <div className="py-[4px] flex items-center">
                                    <div className="w-[60px] aspect-square">
                                        <img src={productsRelated[5].thumbnail[0]} className="w-full h-full object-cover" />
                                    </div>
                                    <div className="flex-1 ml-[10px]">
                                        <div className="text-[14px] pb-[3px] text-textColor font-[500] line-clamp-2">{productsRelated[5].title}</div>
                                        <div className="flex">
                                            <span className="text-[14px] text-primary pr-[10px]">{(productsRelated[5].variants[0].price * (1 - productsRelated[5].discountPercent / 100)).toLocaleString()}<sup className="underline">đ</sup></span>
                                            <span className="text-[12px] text-[#9e9e9e] line-through mt-[3px] font-[400]">{(productsRelated[5].variants[0].price).toLocaleString()}<sup className="underline">đ</sup></span>
                                        </div>
                                    </div>
                                </div>
                            </Link>
                        )}

                    </div>
                </div>
            </div>
        </>
    )
}