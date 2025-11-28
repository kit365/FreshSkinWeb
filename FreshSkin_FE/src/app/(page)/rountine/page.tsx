"use client"

import { useContext, useEffect, useState } from "react"
import { SettingProfileContext } from "../layout";
import { Swiper, SwiperSlide } from 'swiper/react';

// Import Swiper styles
import 'swiper/css';
import 'swiper/css/navigation';
import '../../(page)/swiper.css';
import { Navigation } from 'swiper/modules';
import Title from "@/app/components/title/Title";
import CardItem from "@/app/components/Card/CardItem";

export default function RountinePage() {
    const [data, setData] = useState<any>({
        description: "",
        title: "",
        rountineStep: []
    });

    const settingProfile = useContext(SettingProfileContext);

    if (!settingProfile) {
        return null;
    }

    const { profile } = settingProfile;

    useEffect(() => {
        const fetchData = async () => {
            const response = await fetch(`https://freshskinweb.onrender.com/home/skincare/rountine`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    skinType: profile.skinType
                })
            });

            const dataResponse = await response.json();
            console.log(dataResponse);
            setData(dataResponse.data);
        };

        fetchData();
    }, [profile]);

    return (
        <>
            <div className="container mx-auto mt-[20px]">
                <div className="text-[26px] font-[600] text-center mb-[30px]">{data?.title}</div>
                <div dangerouslySetInnerHTML={{ __html: data?.description }}></div>
                <div className="text-[26px] font-[600] text-center mt-[70px] mb-[30px]">Các bước cụ thể</div>
                {data?.rountineStep && data.rountineStep.length > 0 && data.rountineStep.map((item: any, index: number) => (
                    <div key={index} className="mb-[40px]">
                        <div className="text-[20px] font-[550] mb-[10px]">Bước {index + 1}: <span>{item.step}</span></div>
                        <div className="" dangerouslySetInnerHTML={{ __html: item.content }}></div>
                        {item.product.length > 0 && (
                            <Title title="Gợi ý sản phẩm" />
                        )}
                        <Swiper
                            slidesPerView={5}
                            spaceBetween={25}
                            navigation={true}
                            modules={[Navigation]}
                            className="mySwiper"
                        >
                            {item.product.map((product: any, index: number) => (
                                <SwiperSlide key={index}>
                                    <CardItem
                                        image={product.thumbnail}
                                        brand={product.brand.title}
                                        title={product.title}
                                        link={`/detail/${product.slug}`}
                                        priceByVolume={product.variants}
                                        discount={product.discountPercent}
                                    />
                                </SwiperSlide>
                            ))}
                        </Swiper>
                    </div>
                ))}
            </div>
        </>
    )
}