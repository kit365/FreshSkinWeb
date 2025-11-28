"use client"

import { cartTotalPriceVoucher } from "@/app/(actions)/cart";
import { Alert } from "@mui/material";
import { useEffect, useState } from "react";
import { FaAngleRight } from "react-icons/fa6";
import { useDispatch, useSelector } from "react-redux";

export default function SaleCode() {
    const totalPrice = useSelector((state: any) => (state.cartReducer.totalPriceInit));
    const dispatchCart = useDispatch();
    const [alertMessage, setAlertMessage] = useState<string>("");
    const [alertSeverity, setAlertSeverity] = useState<"success" | "error" | "info" | "warning">("info");
    const [copied, setCopied] = useState(false);
    const [data, setData] = useState([{
        voucherId: "",
        name: "",
        type: "",
        discountValue: 0,
        maxDiscount: 0,
        minOrderValue: 0,
        showCondition: false
    }]);

    const [value, setValue] = useState("");

    useEffect(() => {
        if (value !== "") {
            setInputIsFocused(true);
            setCurrentValueInput(value);
        }
    }, [value]);


    useEffect(() => {
        const fetchData = async () => {
            const response = await fetch(`https://freshskinweb.onrender.com/admin/vouchers/valid`);
            const dataResponse = await response.json();
            setData(dataResponse.data.map((item: any) => ({
                ...item,
                showCondition: false
            })));
        };

        fetchData();
    }, []);

    const toggleCondition = (index: number) => {
        setData(prevData => {
            const newData = [...prevData];
            newData[index] = {
                ...newData[index],
                showCondition: !newData[index].showCondition,
            };
            return newData;
        });
    }

    //Popup
    const [isPopupOpen, setIsPopupOpen] = useState(false);

    const handleOpenPopup = () => {
        setIsPopupOpen(true);
    };

    const handleClosePopup = () => {
        setIsPopupOpen(false);
    };

    const handleCopy = async (code: string) => {
        await navigator.clipboard.writeText(code);
        setCopied(true);
    };

    const handleSubmitVoucher = () => {
        const currentVoucher = data.find((item: any) => item.name == currentValueInput);
        if (!currentVoucher) {
            dispatchCart(cartTotalPriceVoucher(0, ""));
            setAlertMessage("Mã giảm giá không hợp lệ");
            setAlertSeverity("error");
            setTimeout(() => setAlertMessage(""), 5000);
            return;
        }
        if (totalPrice > currentVoucher.minOrderValue) {
            let priceAfterApplyVoucher = totalPrice;
            let discountAmount = 0;

            if (currentVoucher.type === "PERCENTAGE") {
                discountAmount = totalPrice * (currentVoucher.discountValue / 100);
                discountAmount = Math.min(discountAmount, currentVoucher.maxDiscount);
            } else {
                discountAmount = currentVoucher.discountValue;
            }

            priceAfterApplyVoucher = totalPrice - discountAmount;
            if (priceAfterApplyVoucher < 0) {
                priceAfterApplyVoucher = 0;
            }
            dispatchCart(cartTotalPriceVoucher(priceAfterApplyVoucher, currentValueInput));
        }
    }

    const [inputIsFocused, setInputIsFocused] = useState(value !== "");
    const [currentValueInput, setCurrentValueInput] = useState(value);

    const handleFocusInput = () => setInputIsFocused(true);

    const handleInput = (event: any) => setCurrentValueInput(event.target.value);

    const handleBlurInput = () => {
        if (currentValueInput.length === 0) {
            setInputIsFocused(false);
        }
    };

    return (
        <>
            {alertMessage && (
                <Alert severity={alertSeverity} sx={{ position: "fixed", width: "600px", height: "60px", right: "5%", top: "5%", fontSize: "16px", zIndex: "999999" }}>
                    {alertMessage}
                </Alert>
            )}
            <div className="ml-[28px] pt-[24px] pb-[12px] border-t border-solid border-[#d9d9d9] flex items-center justify-between">
                <div className="flex-1">
                    <div className="relative">
                        <label
                            htmlFor="voucher"
                            className={`text-[#999] absolute left-[11px] transition-all duration-200 ${inputIsFocused ? "text-[13px] top-[5px]" : "text-[15px] top-[12px]"
                                }`}
                        >
                            Nhập mã giảm giá
                        </label>
                        <input
                            name="voucher"
                            id="voucher"
                            className="pt-[16px] pb-[2px] px-[11px] w-full h-[44px] bg-white text-[#333] rounded-[4px] border border-solid border-[#d9d9d9] focus:border-[#72a834] outline-none input-checkout mb-[10px] text-[14px] font-[450]"
                            onFocus={handleFocusInput}
                            onBlur={handleBlurInput}
                            onInput={handleInput}
                            value={currentValueInput}
                            onChange={handleInput}
                        />
                    </div>
                </div>
                <div onClick={handleSubmitVoucher} className="rounded-[4px] cursor-pointer ml-[12px] pt-[10px] mb-[10px] bg-[#72a834] border border-solid border-[#72a834] text-white text-[14px] font-[450] h-[44px] px-[20px]">
                    Áp dụng
                </div>
            </div>
            <div className="ml-[28px] flex items-center justify-between">
                <div className="flex items-center">
                    <img className="w-[20px] mr-[4px]" src="https://bizweb.dktcdn.net/100/495/928/themes/921279/assets/coupon-icon.png?1742461447222" />
                    <span className="text-[14px] text-textColor">Mã khuyến mãi</span>
                </div>
                <div onClick={handleOpenPopup} className="cursor-pointer text-[#009EF6] flex items-center text-[14px]">Xem chi tiết <FaAngleRight /></div>
            </div>

            {isPopupOpen && (
                <div className="fixed inset-0 bg-black bg-opacity-50 z-[99999999] flex justify-center items-start pt-[8%]" onClick={handleClosePopup}>
                    <div className="container mx-auto w-[650px] max-h-[80%] bg-[#fff] py-[10px] px-[15px] rounded-[10px] overflow-y-auto" onClick={(e) => e.stopPropagation()}>
                        <div className="uppercase text-[16px] text-textColor font-[550] mb-[10px]">Mã khuyến mãi</div>
                        {data && data.length > 0 && data.map((item: any, index: number) => (
                            <div key={index} className="mb-[15px]">
                                <div className="border border-dashed border-primary rounded-[5px] h-[85px] flex items-center">
                                    {item.type === "PERCENTAGE" ? (
                                        <div className="bg-primary w-[110px] text-[30px] py-[10px] text-[#fff] font-[500] h-full flex justify-center items-center">{item.discountValue}%</div>
                                    ) : (
                                        <div className="bg-primary w-[110px] text-[30px] py-[10px] text-[#fff] font-[500] h-full flex justify-center items-center">{((item.discountValue / 1000)).toFixed(0)}k</div>
                                    )}
                                    <div className="px-[10px] h-full flex-1 flex items-center bg-[#E4EDD5]">
                                        <div className="w-[70%]">
                                            <div className="text-[14px] text-textColor font-[450] mb-[3px]">Mã khuyến mãi <span className="text-primary font-[600]">{item.name}</span></div>
                                            {item.type === "PERCENTAGE" ? (
                                                <div className="text-[14px] text-textColor font-[450]">Mã giảm {item.discountValue}% cho đơn hàng tối thiểu {item.minOrderValue / 1000}k.</div>
                                            ) : (
                                                <div className="text-[14px] text-textColor font-[450]">Mã giảm {item.discountValue / 1000}k cho đơn hàng tối thiểu {item.minOrderValue / 1000}k.</div>
                                            )}
                                        </div>
                                        <div className="flex-1">
                                            <div onClick={() => handleCopy(item.name)} className="bg-primary hover:bg-[#F4AE05] flex justify-center items-center text-white px-[15px] cursor-pointer h-[32px] font-[500] text-[16px] rounded-[5px]">Sao chép mã</div>
                                            <div onClick={() => toggleCondition(index)} className="text-[#2E72D2] hover:text-[#F4AE05] cursor-pointer text-[14px] text-center underline mt-[5px]">Điều kiện</div>
                                        </div>
                                    </div>
                                </div>
                                {item.showCondition && (
                                    <div className="bg-[#fff4db] p-[5px] text-[#000] text-[14px] rounded-[5px]">
                                        Áp dụng cho đơn hàng từ {(item.minOrderValue / 1000).toFixed(0)}k với khuyến mãi tối đa {(item.maxDiscount / 1000).toFixed(0)}k
                                    </div>
                                )}
                            </div>
                        ))}
                    </div>
                </div>
            )}
        </>
    )
}