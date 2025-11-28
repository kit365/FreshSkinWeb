"use client"

import ButtonSeeAll from "@/app/components/Button/ButtonSeeAll";
import CardItem from "@/app/components/Card/CardItem";
import Title from "@/app/components/title/Title";
import { useState } from "react";
import { Swiper, SwiperSlide } from 'swiper/react';

// Import Swiper styles
import 'swiper/css';
import 'swiper/css/navigation';
import '../../(page)/swiper.css';
import { Navigation } from 'swiper/modules';

export default function Section9(props: any) {
    const { dataInit = [] } = props;

    // console.log(dataInit);

    const [data, setData] = useState(dataInit[0]);

    console.log(data);
    console.log(dataInit);

    // const dataButton: any = dataInit.map((item: any) => (
    //     {
    //         data: item.products,
    //         currentStatus: item.slug,
    //         content: item.title
    //     }
    // ));

    // const [currentButton, setCurrentButton] = useState(dataInit[0].slug);

    const handleClick: any = (data: any) => {
        setData(data);
    }

    return (
        <>
            <div className="container mx-auto">
                <Title title="Tóc khỏe tóc đẹp" link="/products" />

                <div className="text-center mb-[20px]">
                    {dataInit.map((item: any, index: number) => (
                        <button
                            key={index}
                            onClick={() => handleClick(item)}
                            className={"text-[16px] hover:text-primary font-[500] px-[25px] py-[2px] " +
                                (data.title == item.title ? "text-primary" : "text-[#333]")
                            }
                        >
                            {item.title}
                        </button>
                    ))}
                </div>
                <Swiper
                    slidesPerView={5}
                    spaceBetween={20}
                    navigation={true}
                    modules={[Navigation]}
                    className="mySwiper"
                >
                    {data.products.map((item: any, index: number) => (
                        <SwiperSlide key={index}>
                            <CardItem
                                key={index}
                                image={item.thumbnail}
                                brand={item.brand?.title}
                                title={item.title}
                                link={`/detail/${item.slug}`}
                                priceByVolume={item.variants}
                                discount={item.discountPercent}
                            />
                        </SwiperSlide>
                    ))}
                </Swiper>
                {/* <ButtonSeeAll link={`/product-category/${currentButton}`} /> */}
            </div>
        </>
    )
}