"use client"

import Link from "next/link";
import { Swiper, SwiperSlide } from 'swiper/react';

// Import Swiper styles
import 'swiper/css';
import 'swiper/css/navigation';
import '../../(page)/swiper.css';
import { Navigation } from 'swiper/modules';

export default function Banner() {
    return (
        <>
            <div className="container mx-auto flex mt-[10px]">
                <div className="w-[858px] h-[340px] mr-[15px] ">
                    <Swiper
                        slidesPerView={1}
                        navigation={true}
                        modules={[Navigation]}
                        className="mySwiper"
                    >
                        <SwiperSlide>
                            <Link href="/products">
                                <img src="https://i.imgur.com/8ZdSKAm.jpeg" className="rounded-[15px] object-cover" />
                            </Link>
                        </SwiperSlide>
                        <SwiperSlide>
                            <Link href="/products">
                                <img src="https://res.cloudinary.com/dr53sfboy/image/upload/v1743325037/product-brand/dsa_20250330-085716.webp" className="rounded-[15px] object-cover" />
                            </Link>
                        </SwiperSlide>
                    </Swiper>
                </div>
                <div className="flex-1">
                    <div className="mb-[15px] h-[164px]">
                        <Link href="/products">
                            <img src="https://res.cloudinary.com/dr53sfboy/image/upload/v1742356503/product-brand/dsa_20250319-035503_2.webp" className="rounded-[15px]" />
                        </Link>
                    </div>
                    <div className="h-[164px]">
                        <Link href="/product-category/toner-nuoc-can-bang-da">
                            <img src="https://res.cloudinary.com/dr53sfboy/image/upload/v1742356504/product-brand/dsa_20250319-035503_3.webp" className="rounded-[15px]" />
                        </Link>
                    </div>
                </div>
            </div>
        </>
    )
}