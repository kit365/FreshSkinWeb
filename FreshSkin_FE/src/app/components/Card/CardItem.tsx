"use client"

import { cartAddNewProduct } from "@/app/(actions)/cart";
import Link from "next/link";
import { useEffect, useState } from "react";
import ReactDOM from 'react-dom';
import { useDispatch, useSelector } from "react-redux";
import { Swiper, SwiperSlide } from 'swiper/react';
import { Swiper as SwiperType } from 'swiper';

// Import Swiper styles
import 'swiper/css';
import 'swiper/css/free-mode';
import 'swiper/css/navigation';
import 'swiper/css/thumbs';

import '../../(page)/swiper.css';

// import required modules
import { FreeMode, Navigation, Thumbs } from 'swiper/modules';
import { Alert } from "@mui/material";

interface PriceByVolume {
    id: number;
    volume: number;
    price: number;
    unit: string;
    stock: number;
}

interface CartItem {
    image: string,
    title: string,
    price: number,
    link: string,
    variantId: number,
    volume: number,
    unit: string,
    quantity: number,
    stock: number
}

export default function CardItem(props: {
    image: string[],
    brand: string,
    title: string,
    deal?: string,
    banner?: string,
    className?: string,
    link: string,
    priceByVolume: PriceByVolume[],
    discount: number
}) {
    const { image = [], brand = "", title = "", banner = "", deal = "", className = "", link = "", priceByVolume = [], discount = 0 } = props;

    const [thumbsSwiper, setThumbsSwiper] = useState<SwiperType | null>(null);
    const dispatchCart = useDispatch();

    const products = useSelector((state: any) => state.cartReducer.products);
    const [currentVolume, setCurrentVolume] = useState<any>(priceByVolume[0]);

    useEffect(() => {
        setCurrentVolume(priceByVolume[0]);
    }, [priceByVolume]);

    const [alertMessage, setAlertMessage] = useState<string>("");
    const [alertSeverity, setAlertSeverity] = useState<"success" | "error" | "info" | "warning">("info");
    const [quantity, setQuantity] = useState(1);

    const handleChange = (event: any): void => {
        const value = parseInt(event.target.value);
        if (value >= 1 && value <= currentVolume.stock) {
            setQuantity(value);
        } else if (value > currentVolume.stock) {
            setQuantity(currentVolume.stock);
        } else {
            setQuantity(1);
        }
    };

    const handleClickDecrease = (): void => {
        if (quantity - 1 >= 1) {
            setQuantity(quantity - 1);
        }
    }

    const handleClickIncrease = (): void => {
        if (quantity < currentVolume.stock) {
            setQuantity(quantity + 1);
        }
    };

    const handleAddNewProductToCart = () => {
        const data: CartItem = {
            image: image[0],
            title: title,
            price: currentVolume.price * (1 - discount / 100),
            link: link,
            variantId: currentVolume.id,
            volume: currentVolume.volume,
            unit: currentVolume.unit,
            quantity: quantity,
            stock: currentVolume.stock
        };

        const existProductInCart = products.find((item: CartItem) => item.title == data.title && item.volume == data.volume);

        if (existProductInCart) {
            if (existProductInCart.quantity + data.quantity > currentVolume.stock) {
                setAlertMessage("Sản phẩm không đủ số lượng.");
                setAlertSeverity("error");
                setTimeout(() => setAlertMessage(""), 3000);
                return;
            }
            existProductInCart.quantity += data.quantity;
        }
        else {
            if (data.quantity > currentVolume.stock) {
                setAlertMessage("Sản phẩm không đủ số lượng.");
                setAlertSeverity("error");
                setTimeout(() => setAlertMessage(""), 3000);
                setTimeout(() => setAlertMessage(""), 3000);
                return;
            }
            products.push(data);
        }

        dispatchCart(cartAddNewProduct(products));
    }

    //Popup
    const [isPopupOpen, setIsPopupOpen] = useState(false);

    const handleOpenPopup = () => {
        setIsPopupOpen(true);
    };

    const handleClosePopup = () => {
        setThumbsSwiper(null);
        setIsPopupOpen(false);
    };

    return (
        <>
            <div className="bg-white rounded-[10px] w-[226px]">
                {alertMessage && (
                    <Alert severity={alertSeverity} sx={{ position: "fixed", width: "600px", height: "60px", right: "5%", top: "5%", fontSize: "16px", zIndex: "999999" }}>
                        {alertMessage}
                    </Alert>
                )}
                <div className="w-[226px] aspect-square relative group">
                    <Link href={link}>
                        <img src={image[0]} alt="Image" className="w-full h-full object-cover rounded-[10px]" />
                    </Link>
                    {banner && (
                        <img src={banner} className="absolute bottom-0" />
                    )}
                    {deal && (
                        <img src={deal} className="absolute top-[2%] left-[2%]" />
                    )}
                    <div
                        onClick={() => { handleOpenPopup() }}
                        className="cursor-pointer rounded-[30px] absolute bottom-[12%] left-[27%] bg-primary hover:bg-secondary py-[8px] px-[10px] hidden text-[14px] text-white w-[98px] h-[36px] font-[500] card-see-quick group-hover:block"
                    >
                        Xem nhanh
                    </div>
                </div>

                <div className={`text-center mt-[5px] w-[226px] h-[100px]` + className}>
                    {brand && (
                        <div className="uppercase text-[14px] font-[600 text-[#4e7661] mb-[5px] hover:text-primary">{brand}</div>
                    )}
                    <Link href={link}>
                        <div className="text-[14px] font-[400] mx-[5px] line-clamp-2 mb-[5px] hover:text-secondary">{title}</div>
                    </Link>
                    <div className="flex items-center mb-[10px] justify-center">
                        <span className="text-[#c90000] text-[15px] font-[500] mr-[5px]">{(currentVolume.price * (1 - discount / 100)).toLocaleString("en-US")}<sup className="underline">đ</sup></span>
                        {discount !== 0 && (
                            <span className="text-[#98a4a9] text-[12px] font-[300] line-through mr-[5px]">{currentVolume.price.toLocaleString("en-US")}<sup className="underline">đ</sup></span>
                        )}
                        {discount !== 0 && (
                            <span className="text-white bg-primary py-[3px] rounded-[3px] text-[12px] min-w-11">-{discount}%</span>
                        )}
                    </div>
                </div>
            </div>

            {isPopupOpen && ReactDOM.createPortal(
                <div className="fixed inset-0 bg-black bg-opacity-50 z-[99999999] flex justify-center items-start pt-[8%]" onClick={handleClosePopup}>
                    <div className="bg-white rounded-[5px] w-[840px] p-5 relative" onClick={(e) => e.stopPropagation()}>
                        <button
                            className="absolute top-2 right-2 text-gray-500 hover:text-black"
                            onClick={handleClosePopup}
                        >
                            ✕
                        </button>

                        <div className="flex items-center px-[5px]">
                            <div className="w-[256px] mr-[10px]">
                                <Swiper
                                    loop={true}
                                    spaceBetween={10}
                                    navigation={true}
                                    thumbs={{ swiper: thumbsSwiper }}
                                    modules={[FreeMode, Navigation, Thumbs]}
                                    className="mySwiper2"
                                >
                                    {image.map((item: any, index: number) => (
                                        <SwiperSlide key={index}>
                                            <img className="w-full h-full object-cover" src={item} alt="Image" />
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
                                    {image.map((item: any, index: number) => (
                                        <SwiperSlide key={index}>
                                            <img className="w-full h-full hover:border hover:border-solid hover:border-secondary object-cover border border-solid border-[#e9edf5] cursor-pointer" src={item} />
                                        </SwiperSlide>
                                    ))}
                                </Swiper>
                            </div>
                            <div className="flex-1">
                                <div className="text-[23px] font-[600] text-[#333]">{title}</div>
                                <div className="flex mt-[10px] items-center mb-[10px]">
                                    <div className="text-[20px] font-[600] text-primary">{(currentVolume.price * (1 - discount / 100)).toLocaleString("en-US")}<sup className="underline">đ</sup></div>
                                    {discount !== 0 && (
                                        <div className="text-[16px] font-[600] text-[#a5a5a5] ml-[10px] line-through">{currentVolume.price.toLocaleString("en-US")}<sup className="underline">đ</sup></div>
                                    )}
                                </div>
                                <div>
                                    <div className="text-[14px] text-[#0090F] mb-[10px]">Dung Tích: <span className="text-secondary font-[600]">{currentVolume.volume}{currentVolume.unit.toLowerCase()}</span></div>
                                    {currentVolume.stock > 0 ? (
                                        <div className="text-[14px] text-[#0090F] mb-[10px]">Tình trạng: <span className="text-primary font-[600]">Còn hàng</span></div>
                                    ) : (
                                        <div className="text-[14px] text-[#0090F] mb-[10px]">Tình trạng: <span className="text-[#C5332D] font-[600]">Hết hàng</span></div>
                                    )}
                                    <div className="flex items-center mb-[10px]">
                                        {priceByVolume.map((item: any, index: number) => (
                                            <button
                                                key={index}
                                                className={"text-[14px] mr-[10px] rounded-[5px] min-w-[30px] h-[30px] cursor-pointer border border-solid text-center p-[5px] " +
                                                    (item.volume === currentVolume.volume
                                                        ? "bg-secondary border-[#ddd] text-white"
                                                        : "border-[#e4e4e4] text-[#00090F]"
                                                    )}
                                                onClick={() => setCurrentVolume(item)}
                                            >
                                                {item.volume}{item.unit.toLowerCase()}
                                            </button>
                                        ))}
                                    </div>
                                </div>
                                <div className="flex items-center">
                                    {currentVolume.stock > 0 && (
                                        <div className="mb-[10px] flex">
                                            <button
                                                className="hover:bg-primary hover:text-white text-[18px] w-[40px] h-[40px] text-[#333] flex justify-center items-center rounded-tl-[5px] rounded-bl-[5px] border border-solid border-[#ddd]"
                                                onClick={handleClickDecrease}
                                            >
                                                -
                                            </button>
                                            <input
                                                type="number"
                                                name="quantity"
                                                id="quantity"
                                                value={quantity}
                                                onChange={() => handleChange(event)}
                                                className="text-[#00090f] text-[16px] block w-[60px] h-[40px] outline-none text-center border-y border-solid border-[#ddd]"
                                            />
                                            <button
                                                className="hover:bg-primary hover:text-white text-[18px] w-[40px] h-[40px] text-[#333] flex justify-center items-center rounded-tr-[5px] rounded-br-[5px] border border-solid border-[#ddd]"
                                                onClick={handleClickIncrease}
                                            >
                                                +
                                            </button>
                                        </div>
                                    )}
                                    {currentVolume.stock > 0 && (
                                        <button className="mb-[10px] px-[25px] h-[40px] flex items-center bg-secondary hover:bg-white text-[12px] uppercase text-white hover:text-secondary rounded-[5px] ml-[15px] border borde-solid border-secondary" onClick={handleAddNewProductToCart}>
                                            Thêm vào giỏ hàng
                                        </button>
                                    )}
                                </div>
                            </div>
                        </div>
                    </div>
                </div>,
                document.body
            )}
        </>
    )
}