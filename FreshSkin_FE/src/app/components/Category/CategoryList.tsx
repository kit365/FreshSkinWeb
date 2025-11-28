"use client"

import CategoryItem from "./CategoryItem";
import { Swiper, SwiperSlide } from 'swiper/react';
import 'swiper/css';
import 'swiper/css/navigation';
import '../../(page)/swiper.css';
import { Navigation } from 'swiper/modules';

export default function CategoryList(props: any) {
    const { data } = props;

    return (
        <>
            <Swiper
                navigation={true} modules={[Navigation]}
                className="mySwiper"
                spaceBetween={20}
                slidesPerView={4}
            >
                {data.length > 0 && data.map((item: any, index: number) => (
                    <SwiperSlide key={index}>
                        <CategoryItem
                            title={item.title}
                            quantity={item.products.length}
                            image1={item.image[0]}
                            image2={item.products[0]?.thumbnail[0] || ''}
                            image3={item.products[1]?.thumbnail[0] || ''}
                            image4={item.products[2]?.thumbnail[0] || ''}
                            image5={item.products[3]?.thumbnail[0] || ''}
                            link={`/product-category/${item.slug}`}
                            link1={`/detail/${item.products[0]?.slug}`}
                            link2={`/detail/${item.products[1]?.slug}`}
                            link3={`/detail/${item.products[2]?.slug}`}
                            link4={`/detail/${item.products[3]?.slug}`}
                        />
                    </SwiperSlide>
                ))}
            </Swiper>
        </>
    )
}