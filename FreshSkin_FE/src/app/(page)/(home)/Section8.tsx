import CardItem from "@/app/components/Card/CardItem";
import { Swiper, SwiperSlide } from 'swiper/react';

// Import Swiper styles
import 'swiper/css';
import 'swiper/css/pagination';
import '../../(page)/swiper.css';
// import required modules
import { Pagination } from 'swiper/modules';

export default function Section8(props: any) {
    const { dataInit = [] } = props;

    return (
        <>
            <div className="h-[660px] bg-[url('../../public/demo/bg-section8.webp')] bg-cover mt-[50px]">
                <div className="container mx-auto flex justify-end ">
                    <div className="w-[730px] mt-[30px]">
                        <div className="text-center mb-[30px]">
                            <div className="text-secondary font-[600] text-[46px]">FRESH SKIN</div>
                            <div className="font-[400] text-[16px] max-w-[430px] inline-block">Không phải những người đẹp là những người hạnh phúc, mà những người hạnh phúc mới là những người đẹp.</div>
                            <div className="w-[64px] h-[34px] ml-[46%] mt-[7px]">
                                <img src="https://res.cloudinary.com/dr53sfboy/image/upload/v1742359280/product-brand/dsadas_20250319-044119_6.webp" className="w-full h-full object-cover" />
                            </div>
                        </div>
                        <Swiper
                            slidesPerView={3}
                            spaceBetween={30}
                            pagination={{
                              clickable: true,
                            }}
                            modules={[Pagination]}
                            className="mySwiper swiper-2"
                        >
                            {dataInit.map((item: any, index: number) => (
                                <SwiperSlide key={index}>
                                    <CardItem
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
                    </div>
                </div>
            </div>
        </>
    );
}