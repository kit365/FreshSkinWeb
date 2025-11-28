"use client"

import CardItem from "@/app/components/Card/CardItem";
import Title from "@/app/components/title/Title";
import { useState } from "react";
import Link from "next/link";
import { Swiper, SwiperSlide } from 'swiper/react';
// Import Swiper styles
import 'swiper/css';
import 'swiper/css/navigation';
import 'swiper/css/pagination';
import 'swiper/css/grid'
import '../../(page)/swiper.css';
import { Navigation } from 'swiper/modules';

export default function Section5(props: any) {
    const { dataInit = [] } = props;

    const dataButton: any = dataInit.map((item: any) => (
        {
            data: item.products,
            currentStatus: item.slug,
            content: item.title
        }
    ));

    const [data, setData] = useState(dataInit[0].products);
    const [currentButton, setCurrentButton] = useState(dataInit[0].slug);

    const handleClick: any = (data: any, currentButton: string) => {
        setData(data);
        setCurrentButton(currentButton);
    }

    return (
        <>
            <div className="container mx-auto">
                <Title title="Da đẹp - Thêm tự tin cùng Fresh Skin" link="/products" />
                <div className="text-center mb-[20px]">
                    {dataButton.map((item: any, index: number) => (
                        <button
                            key={index}
                            onClick={() => handleClick(item.data, item.currentStatus)}
                            className={"text-[16px] hover:text-primary font-[500] px-[25px] py-[2px] " +
                                (currentButton == item.currentStatus ? "text-primary" : "text-[#333]")
                            }
                        >
                            {item.content}
                        </button>
                    ))}
                </div>
                <div className="container mx-auto flex">
                    <Link href="/products" className="w-[412px] h-[685px] mr-[30px]">
                        <img src="https://res.cloudinary.com/dr53sfboy/image/upload/v1742356511/product-brand/dsa_20250319-035511_14.jpg" className="w-full h-full object-cover rounded-[5px]" />
                    </Link>
                    <div className="flex-1 grid grid-cols-3 gap-[30px]">
                        {data.slice(0, 6).map((item: any, index: number) => (
                            <CardItem
                                key={index}
                                image={item.thumbnail}
                                brand={item.brand.title}
                                title={item.title}
                                link={`/detail/${item.slug}`}
                                priceByVolume={item.variants}
                                discount={item.discountPercent}
                            />
                        ))}
                    </div>
                </div>
            </div>
        </>
    )
}