"use client"

import Link from "next/link";
import { useContext, useEffect, useState } from "react";
import { FaStar } from "react-icons/fa6";
import { MdNavigateNext } from "react-icons/md";
import { SettingProfileContext } from "../layout";
import { useDispatch, useSelector } from "react-redux";
import { cartAddNewProduct } from "@/app/(actions)/cart";
import { Alert } from "@mui/material";

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

export default function ComparePage() {
    const [alertMessage, setAlertMessage] = useState<string>("");
    const [alertSeverity, setAlertSeverity] = useState<"success" | "error" | "info" | "warning">("info");
    const [isDeleteAlert, setIsDeleteAlert] = useState(false);
    const [productIdDelete, setProductIdDelete] = useState(0);
    const [data, setData] = useState<any>();
    const dispatchCart = useDispatch();
    const settingProfile = useContext(SettingProfileContext);

    if (!settingProfile) {
        return null;
    }

    const { profile } = settingProfile;

    const products = useSelector((state: any) => state.cartReducer.products);

    if (profile.productComparisonId) {
        useEffect(() => {
            const fetchData = async () => {
                const response = await fetch(`https://freshskinweb.onrender.com/home/products/getComparison`, {
                    method: "PATCH",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify({
                        id: profile.productComparisonId.id,
                        userID: profile.userID
                    })
                });

                const dataResponse = await response.json();

                if (dataResponse.code == 200) {
                    setData(dataResponse.data.products);
                }
            };

            fetchData();

        });
    }

    const handleAddNewProductToCart = (item: any) => {
        const data: CartItem = {
            image: item.thumbnail[0],
            title: item.title,
            price: item.variants[0].price * (1 - item.discountPercent / 100),
            link: `/detail/${item.slug}`,
            variantId: item.id,
            volume: item.variants[0].volume,
            unit: item.variants[0].unit,
            quantity: 1,
            stock: item.variants[0].stock
        };

        const existProductInCart = products.find((item: CartItem) => item.title == data.title && item.volume == data.volume);

        if (existProductInCart) {
            if (existProductInCart.quantity + data.quantity > existProductInCart.stock) {
                setAlertMessage("Sản phẩm không đủ số lượng.");
                setAlertSeverity("error");
                setTimeout(() => setAlertMessage(""), 3000);
                return;
            }
            existProductInCart.quantity += data.quantity;
        }
        else {
            products.push(data);
        }

        dispatchCart(cartAddNewProduct(products));
    }

    const handleClosePopup = () => {
        setIsDeleteAlert(false);
    };

    const handleOpenPopup = (productId: number) => {
        setProductIdDelete(productId);
        setIsDeleteAlert(true);
    };

    const handleDelete = async (productId: number) => {
        const response = await fetch(`https://freshskinweb.onrender.com/home/products/comparison/delete`, {
            method: "delete",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                id: profile.productComparisonId.id,
                productId: productId
            })
        });

        const dataResponse = await response.json();

        if (dataResponse.code == 200) {
            setAlertMessage(dataResponse.message);
            setAlertSeverity("success");
            setTimeout(() => setAlertMessage(""), 3000);
        }
    }

    return (
        <>
            <ul className="flex items-center mt-[17px] container mx-auto">
                <li>
                    <Link href="/" className="flex items-center">
                        <span className="text-[#333] text-[15px] font-[400] hover:text-secondary">Trang chủ</span>
                        <span><MdNavigateNext className="ml-[10px] text-[18px] mr-[10px]" /></span>
                    </Link>
                </li>
                <li className="text-secondary text-[15px] font-[400] line-clamp-1">
                    So sánh sản phẩm
                </li>
            </ul>
            {alertMessage && (
                <Alert severity={alertSeverity} sx={{ position: "fixed", width: "600px", height: "60px", right: "5%", top: "5%", fontSize: "16px", zIndex: "999999" }}>
                    {alertMessage}
                </Alert>
            )}
            <h1 className="uppercase my-[28px] container mx-auto text-[20px] font-[600]">So sánh sản phẩm</h1>
            {data && data.length > 0 ? (
                <table className="mx-auto container">
                    <caption className="text-[18px] font-[600] text-left p-[14px] border border-solid border-[#f0efed] border-b-0">Danh sách sản phẩm</caption>
                    <tbody>
                        <tr>
                            <td className="border border-solid border-[#f0efed] bg-white w-[25%]"></td>
                            {data && data.map((item: any, index: number) => (
                                <td key={index} className="relative border border-solid border-[#f0efed] bg-white w-[25%]">
                                    <div className="p-[10px] w-[290px] h-[290px]">
                                        <Link href={`/detail/${item.slug}`}>
                                            <img src={item.thumbnail[0]} className="rounded-[4px] w-full h-full object-cover" />
                                        </Link>
                                    </div>
                                    <div onClick={() => handleOpenPopup(item.id)} className="absolute top-[2%] right-[2%] text-primary border boder-solid border-primary p-[6px] w-[30px] h-[30px] flex items-center justify-center rounded-full shadow-md cursor-pointer hover:bg-primary hover:text-white transition duration-300">
                                        ✖
                                    </div>
                                    <div className="px-[14px] pt-[8px] pb-[12px]">
                                        <div className="text-[12px] mb-[4px] text-[#545453] font-[600] text-left hover:underline cursor-pointer">{item.brand.title}</div>
                                        <Link href={`/detail/${item.slug}`} className="text-[14px] line-clamp-2 text-left font-[700] text-[#1b1a19] hover:text-primary cursor-pointer">{item.title}</Link>
                                        {/* <div className="mt-[5px] flex items-center">
                                        <div className="flex items-center">
                                            <FaStar className="text-[#f7bf09] font-[400] text-[14px]" />
                                            <FaStar className="text-[#f7bf09] font-[400] text-[14px]" />
                                            <FaStar className="text-[#f7bf09] font-[400] text-[14px]" />
                                            <FaStar className="text-[#f7bf09] font-[400] text-[14px]" />
                                            <FaStar className="text-[#f7bf09] font-[400] text-[14px]" />
                                        </div>
                                        <div className="text-[#f7bf09] text-[12px] font-[700] mt-1 ml-2">4.8</div>
                                    </div> */}
                                        <div className="flex items-center mt-2">
                                            <div className="text-[16px] font-[700] text-[#f04438]">{parseFloat((item.variants[0].price * (1 - item.discountPercent / 100)).toFixed(0)).toLocaleString("en-US")}</div>
                                            {item.discountPercent > 0 && (
                                                <div className="text-[12px] font-[500] text-[#999794] ml-2 line-tdrough mt-1">{parseFloat((item.variants[0].price).toFixed(0)).toLocaleString("en-US")}</div>
                                            )}
                                            {item.discountPercent > 0 && (
                                                <div className="bg-[#f04438] rounded-[4px] text-[#fafafa] px-[4px] py-[3px] text-[10px] ml-[8px]">-{item.discountPercent}%</div>
                                            )}
                                        </div>
                                    </div>
                                </td>
                            ))}
                        </tr>
                        <tr>
                            <td colSpan={100} className="text-[18px] font-[600] text-left p-[14px] border border-solid border-[#f0efed] border-b-0 bg-white">Thông tin sản phẩm</td>
                        </tr>
                        <tr>
                            <td className="w-[25%] p-[14px] text-[14px] text-[#1b1a19] font-[700] border border-solid border-[#f0efed]">Xuất sứ</td>
                            {data && data.map((item: any, index: number) => (
                                <td key={index} className="w-[25%] p-[14px] text-[14px] border border-solid border-[#f0efed]">{item.origin}</td>
                            ))}
                        </tr>
                        <tr>
                            <td className="w-[25%] p-[14px] text-[14px] text-[#1b1a19] font-[700] border border-solid border-[#f0efed]">Thích hợp với</td>
                            {data && data.map((item: any, index: number) => (
                                <td key={index} className="w-[25%] p-[14px] text-[14px] border border-solid border-[#f0efed]">{item.skinIssues}</td>
                            ))}
                        </tr>
                        <tr>
                            <td className="w-[25%] p-[14px] text-[14px] text-[#1b1a19] font-[700] border border-solid border-[#f0efed]">Thành phần</td>
                            {data && data.map((item: any, index: number) => (
                                <td key={index} className="w-[25%] p-[14px] text-[14px] border border-solid border-[#f0efed]"><div
                                    dangerouslySetInnerHTML={{
                                        __html: item.ingredients
                                    }}
                                /></td>
                            ))}
                        </tr>
                        <tr>
                            <td className="w-[25%] p-[14px] text-[14px] text-[#1b1a19] font-[700] border border-solid border-[#f0efed]">Hướng dẫn sử dụng</td>
                            {data && data.map((item: any, index: number) => (
                                <td key={index} className="w-[25%] p-[14px] text-[14px] border border-solid border-[#f0efed]"><div
                                    dangerouslySetInnerHTML={{
                                        __html: item.usageInstructions
                                    }}
                                /></td>
                            ))}
                        </tr>
                        <tr>
                            <td className="w-[25%] p-[14px] text-[14px] text-[#1b1a19] font-[700] border border-solid border-[#f0efed]">Công Dụng</td>
                            {data && data.map((item: any, index: number) => (
                                <td key={index} className="w-[25%] p-[14px] text-[14px] border border-solid border-[#f0efed]"><div
                                    dangerouslySetInnerHTML={{
                                        __html: item.benefits
                                    }}
                                /></td>
                            ))}
                        </tr>
                        <tr>
                            <td className="w-[25%] p-[14px] text-[14px] text-[#1b1a19] font-[700] border border-solid border-[#f0efed]">Mô tả sản phẩm</td>
                            {data && data.map((item: any, index: number) => (
                                <td key={index} className="w-[25%] p-[14px] text-[14px] border border-solid border-[#f0efed]"><div
                                    dangerouslySetInnerHTML={{
                                        __html: item.description
                                    }}
                                /></td>
                            ))}
                        </tr>
                        <tr>
                            <td className="border border-solid border-[#f0efed] bg-white w-[25%]"></td>
                            {data && data.map((item: any, index: number) => (
                                <td key={index} onClick={() => handleAddNewProductToCart(item)} className="border border-solid border-[#f0efed] bg-white w-[25%] p-[14px]">
                                    <div className="py-[11px] text-[14px] rounded-[44px] uppercase cursor-pointer text-center font-[700] bg-primary hover:bg-secondary text-[#fafafa] border border-solid border-primary hover:border-secondary">Thêm vào giỏ hàng</div>
                                </td>
                            ))}
                        </tr>
                    </tbody>
                </table>
            ) : (
                <div className="container mx-auto rounded-[5px] py-[7px] px-[15px] font-[600] text-[14px] border border-solid text-[#6f4400] border-[#6f4400] bg-[#fdf0d5]">Bạn có không có mục nào để so sánh.</div>
            )}

            {isDeleteAlert && (
                <div onClick={handleClosePopup} className="fixed inset-0 bg-black bg-opacity-50 z-[99999999] flex justify-center items-start pt-[8%]">
                    <div className="w-[524px] rounded-[12px] bg-[#F8F8F8] pt-[30px] px-[20px] pb-[20px]">
                        <div className="text-right mr-[10px] mb-[10px] text-[17px] text-[#1b1a19] cursor-pointer hover:text-primary" onClick={handleClosePopup}>X</div>
                        <div className="text-[14px] font-[400] text-[#1b1a19]">Bạn có chắc bạn muốn xoá sản phẩm này khỏi danh sách so sánh sản phẩm của bạn không?</div>
                        <div className="pt-[24px] pb-[20px] flex items-center">
                            <div onClick={handleClosePopup} className="text-center uppercase font-[600] text-[14px] py-[11px] px-[23px] rounded-[44px] text-white bg-[#1b1a19] border boder-solid border-[#1b1a19] w-[236px] mr-[5px] cursor-pointer">Hủy</div>
                            <div onClick={() => handleDelete(productIdDelete)} className="text-center uppercase font-[600] text-[14px] py-[11px] px-[23px] rounded-[44px] text-white bg-primary hover:border-secondary border boder-solid border-primary hover:bg-secondary w-[236px] ml-[5px] cursor-pointer">Xác nhận</div>
                        </div>
                    </div>
                </div>
            )}
        </>
    )
}