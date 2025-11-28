"use client"

import { useContext, useState } from "react";
import { Context } from "./MiddlewareGetData";
import { Swiper, SwiperSlide } from 'swiper/react';
import { Swiper as SwiperType } from 'swiper';

import 'swiper/css';
import 'swiper/css/free-mode';
import 'swiper/css/navigation';
import 'swiper/css/thumbs';

import '../../swiper.css';

import { FreeMode, Navigation, Thumbs } from 'swiper/modules';

export default function Image() {
    const [thumbsSwiper, setThumbsSwiper] = useState<SwiperType | null>(null);
    const { productDetail } = useContext(Context);

    return (
        <div className="w-[450px] ml-[70px] mr-[40px]">
            <Swiper
                loop={true}
                spaceBetween={10}
                navigation={true}
                thumbs={{ swiper: thumbsSwiper }}
                modules={[FreeMode, Navigation, Thumbs]}
                className="mySwiper2"
            >
                {productDetail.thumbnail.map((item: any, index: number) => (
                    <SwiperSlide key={index}>
                        <div className="w-[450px] h-[350px] relative mb-[10px]">
                            <img src={item} className="w-full h-full object-cover" />
                        </div>
                    </SwiperSlide>
                ))}
            </Swiper>
            <Swiper
                onSwiper={setThumbsSwiper}
                loop={true}
                spaceBetween={10}
                slidesPerView={4}
                freeMode={true}
                watchSlidesProgress={true}
                modules={[FreeMode, Navigation, Thumbs]}
                className="mySwiper"
            >
                {productDetail.thumbnail.map((item: any, index: number) => (
                    <SwiperSlide key={index}>
                        <div className="w-[103px] h-[90px] hover:border hover:border-solid hover:border-secondary cursor-pointer">
                            <img src={item} className="w-full h-full object-cover" />
                        </div>
                    </SwiperSlide>
                ))}
            </Swiper>
        </div>
    );
}