"use client"

import ButtonSeeAll from "@/app/components/Button/ButtonSeeAll";
import CardItem from "@/app/components/Card/CardItem";
import Title from "@/app/components/title/Title";
import { Swiper, SwiperSlide } from 'swiper/react';

// Import Swiper styles
import 'swiper/css';
import 'swiper/css/navigation';
import '../../(page)/swiper.css';
import { Navigation } from 'swiper/modules';

export default function Section3(props: any) {
    const { dataInit = [] } = props;

    return (
        <>
            <div className="container mx-auto">
                <Title title="Top sản phẩm bán chạy" link="/products" />
                <Swiper
                    slidesPerView={5}
                    spaceBetween={25}
                    navigation={true}
                    modules={[Navigation]}
                    className="mySwiper"
                >
                    {dataInit.map((item: any, index: number) => (
                        <SwiperSlide key={index}>
                            <CardItem
                                key={index}
                                image={item.thumbnail}
                                brand={item.brand.title}
                                title={item.title}
                                banner={item.banner}
                                deal="https://res.cloudinary.com/dr53sfboy/image/upload/v1742383908/product-brand/dsa_20250319-113148_3.webp"
                                link={`/detail/${item.slug}`}
                                priceByVolume={item.variants}
                                discount={item.discountPercent}
                            />
                        </SwiperSlide>
                    ))}
                </Swiper>
                <ButtonSeeAll link="/product-category/top-ban-chay" />
            </div>
        </>
    )
}