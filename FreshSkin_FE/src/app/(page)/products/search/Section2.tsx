"use client"

import { useEffect, useState } from "react";
import CardItem from "@/app/components/Card/CardItem";
import Pagination from "@/app/components/Pagination/Pagination";
import Link from "next/link";
import { MdNavigateNext } from "react-icons/md";

interface Section2Props {
    data: any;
}

export default function Section2({ data }: Section2Props) {
    const [title, setTitle] = useState("");
    const [products, setProducts] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [totalPages, setTotalPages] = useState(0);
    const [currentPage, setCurrentPage] = useState(0);

    useEffect(() => {
        setTitle(data.title || "");
        setProducts(data.products || []);
        setTotalPages(data.page?.totalPages || 0);
        setCurrentPage(data.page?.page || 1);
        setIsLoading(false);
    }, [data]);

    if (isLoading) {
        return <div>Loading...</div>;
    }

    return (
        <div className="container mx-auto">
            <ul className="flex items-center mt-[17px]">
                <li>
                    <Link href="/" className="flex items-center">
                        <span className="text-[#333] text-[15px] font-[400] hover:text-secondary">Trang chủ</span>
                        <MdNavigateNext className="ml-[10px] text-[18px] mr-[10px]" />
                    </Link>
                </li>
                <li className="text-secondary text-[15px] font-[400]">
                    Tìm kiếm: {title}
                </li>
            </ul>
            {products.length > 0 ? (
                <div>
                    <div className="text-[24px] my-[15px] text-[#00090f] font-[450]">Có {data.page.totalItems} kết quả tìm kiếm phù hợp</div>
                    <div className="flex items-start">
                        <div className="flex-1 ml-[40px] mt-[15px]">
                            <div className="grid grid-cols-4 gap-[20px] mt-[20px]">
                                {products.map((item: any, index: number) => (
                                    <CardItem
                                        key={index}
                                        image={item.thumbnail}
                                        brand={item.brand.title}
                                        title={item.title}
                                        banner={item.banner}
                                        deal={item.deal}
                                        link={`/detail/${item.slug}`}
                                        priceByVolume={item.variants}
                                        discount={item.discountPercent}
                                    />
                                ))}
                            </div>
                            <Pagination totalPages={totalPages} currentPage={currentPage} />
                        </div>
                    </div>
                </div>
            ) : (
                <div className="text-[17px] text-textColor mt-[20px]">Không tìm thấy bất kỳ kết quả nào với từ khóa trên.</div>
            )}

        </div>
    );
}
