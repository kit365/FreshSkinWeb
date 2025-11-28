"use client"

import ButtonPay from "@/app/components/Button/ButtonPay";
import Link from "next/link";
import { useDispatch, useSelector } from "react-redux";
import { cartChangeQuantity, cartDecreaseQuantity, cartDelete, cartIncreaseQuantity, } from "../../(actions)/cart";
import { MdNavigateNext } from "react-icons/md";
import { useState } from "react";
import { Alert } from "@mui/material";

interface CartItem {
    image: string,
    title: string,
    price: number,
    link: string,
    variantId: number,
    volume: number,
    unit: string,
    quantity: number
}

export default function CartPage() {
    const [alertMessage, setAlertMessage] = useState<string>("");
    const [alertSeverity, setAlertSeverity] = useState<"success" | "error" | "info" | "warning">("info");
    const products = useSelector((state: any) => state.cartReducer.products);
    const totalPriceInit = useSelector((state: any) => state.cartReducer.totalPriceInit);
    const dispatchCart = useDispatch();

    const handleChange = (event: any, index: number): void => {
        const product = products[index];
        if(event.target.value > product.stock){
            setAlertMessage("Sản phẩm không đủ số lượng.");
            setAlertSeverity("error");
            setTimeout(() => setAlertMessage(""), 3000);
        }
        else{
            dispatchCart(cartChangeQuantity(event, index))
        }
    }

    const handleClickDecrease = (event: any, index: number): void => {
        dispatchCart(cartDecreaseQuantity(event, index))
    }

    const handleClickIncrease = (event: any, index: number): void => {
        event.preventDefault();
        const product = products[index];

        if (product.quantity >= product.stock) {
            setAlertMessage("Sản phẩm không đủ số lượng.");
            setAlertSeverity("error");
            setTimeout(() => setAlertMessage(""), 3000);
        }
        else {
            dispatchCart(cartIncreaseQuantity(event, index))
        }
    }

    const handleDeleteItem = (index: number): void => {
        dispatchCart(cartDelete(index))
    }

    return (
        <>
            {alertMessage && (
                <Alert severity={alertSeverity} sx={{ position: "fixed", width: "600px", height: "60px", right: "5%", top: "5%", fontSize: "16px", zIndex: "999999" }}>
                    {alertMessage}
                </Alert>
            )}
            <ul className="flex items-center container mx-auto mb-[30px]">
                <li>
                    <Link href="/" className="flex items-center">
                        <span className="text-[#333] text-[15px] font-[400] hover:text-secondary">Trang chủ</span>
                        <span><MdNavigateNext className="ml-[10px] text-[18px] mr-[10px]" /></span>
                    </Link>
                </li>
                <li className="text-secondary text-[15px] font-[400]">
                    Giỏ hàng
                </li>
            </ul>
            {products.length > 0 ? (
                <div className="container mx-auto mt-[40px]">
                    <div className="text-[22px] font-[600] text-center mb-[20px]">Giỏ hàng của bạn</div>
                    <div className="">
                        <form action="" className="">
                            <div className="uppercase flex text-center text-[14px] py-[7px] px-[10px] text-textColor font-[600] border border-solid border-[#ebebeb] rounded-tl-[10px] rounded-tr-[10px]">
                                <div className="w-[55%]">Thông tin sản phẩm</div>
                                <div className="w-[15%]">Đơn giá</div>
                                <div className="w-[15%]">Số lượng</div>
                                <div className="w-[15%]">Thành tiền</div>
                            </div>
                            {products.map((item: CartItem, index: number) => (
                                <div key={index} className={"border border-solid border-[#ebebeb] border-t-0 p-[10px] flex items-center " + (index == products.length - 1 ? "rounded-bl-[10px] rounded-br-[10px]" : "")} >
                                    <div className="w-[55%] flex items-center">
                                        <Link href={item.link} className="w-[109px] aspect-square">
                                            <img src={item.image} className="w-full h-full object-cover" />
                                        </Link>
                                        <div className="flex-1 ml-[20px]">
                                            <Link href={item.link}>
                                                <div className="text-[15px] font-[500] mb-[4px] cursor-pointer text-textColor hover:text-secondary">{item.title}</div>
                                            </Link>
                                            <div className="text-[14px] text-textColor mb-[2px]">{item.volume}ml</div>
                                            <div onClick={() => handleDeleteItem(index)} className="text-[15px] text-primary hover:text-secondary cursor-pointer mb-[10px]">Xóa</div>
                                        </div>
                                    </div>
                                    <div className="w-[15%] text-[#c90000] text-center text-[16px] font-[600]">{item.price.toLocaleString("en-US")}<sup className="underline">đ</sup></div>
                                    <div className="w-[15%] flex justify-center items-center">
                                        <button
                                            className="hover:text-white text-[14px] w-[28px] h-[28px] text-[#222] flex justify-center items-center border border-solid border-[#e5e5e5]"
                                            onClick={(event) => handleClickDecrease(event, index)}
                                        >
                                            -
                                        </button>
                                        <input
                                            type="number"
                                            name="quantity"
                                            id="quantity"
                                            value={item.quantity}
                                            onChange={(event) => handleChange(event, index)}
                                            className="text-[#00090f] text-[16px] block w-[60px] h-[28] outline-none text-center border-y border-solid border-[#ddd]"
                                        />
                                        <button
                                            className="hover:text-white text-[14px] w-[28px] h-[28px] text-[#222] flex justify-center items-center border border-solid border-[#e5e5e5]"
                                            onClick={(event) => handleClickIncrease(event, index)}
                                        >
                                            +
                                        </button>
                                    </div>
                                    <div className="w-[15%] text-[#c90000] text-center text-[16px] font-[600]">{(item.price * item.quantity).toLocaleString("en-US")}<sup className="underline">đ</sup></div>
                                </div>
                            ))}
                            <div className="flex justify-between mt-[30px]">
                                <Link href="/products" className="text-[16px] text-textColor hover:text-secondary">Tiếp tục mua hàng</Link>
                                <div className="w-[400px]">
                                    <div className="flex justify-between text-[17px]">
                                        <div className="uppercase font-[400]">Tổng tiền:</div>
                                        <div className="text-[#c90000] font-[600]">{totalPriceInit.toLocaleString("en-US")}<sup className="underline">đ</sup></div>
                                    </div>
                                    <Link href="/order">
                                        <ButtonPay className="text-[16px] py-[8px]" />
                                    </Link>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            ) : (
                <div className="container mx-auto mt-[20px] text-center">
                    <div className="text-[22px] text-textColor font-[550] mb-[25px]">Giỏ hàng của bạn</div>
                    <div className="flex justify-center m-[15px]">
                        <img src="https://res.cloudinary.com/dr53sfboy/image/upload/v1742359280/product-brand/dsadas_20250319-044120_7.webp" className="w-[32px] h-[32px] object-cover" />
                    </div>
                    <div className="mb-[180px] text-[14px] text-textColor">Không có sản phẩm nào trong giỏ hàng của bạn</div>
                    <div></div>
                </div>
            )}

        </>
    )
}