"use client"

import Link from "next/link";
import { useEffect, useState } from "react";
import { MdNavigateNext } from "react-icons/md";
import ProfileLeft from "@/app/components/ProfileUser/ProfileLeft";
import { useParams, useRouter } from "next/navigation";
import Cookies from "js-cookie";
import { Alert } from "@mui/material";

export default function DetailOrderUserPage() {
    const { id } = useParams();
    const [data, setData] = useState<any>({
        firstName: "",
        lastName: "",
        email: "",
        address: "",
        phoneNumber: "",
        totalPrice: 0,
        paymentMethod: "",
        orderDate: "",
        orderItems: [],
        orderStatus: "",
        orderId: "",
        discountAmount: 0,
        priceShipping: 0
    });

    const router = useRouter();

    const [alertMessage, setAlertMessage] = useState<string>("");
    const [alertSeverity, setAlertSeverity] = useState<
        "success" | "error" | "info" | "warning"
    >("info");

    const tokenUser = Cookies.get("tokenUser");

    useEffect(() => {
        if (!tokenUser) {
            router.push("/user/login");
        }
    }, [tokenUser]);

    useEffect(() => {
        const fetchData = async () => {
            const response = await fetch(`https://freshskinweb.onrender.com/admin/orders/${id}`);
            const dataResponse = await response.json();
            setData(dataResponse.data)
        };

        fetchData();
    }, [])

    console.log(data);

    const handleCancel = async (id: number) => {
        const isConfirm = confirm("Bạn có chắc muốn hủy đơn hàng?");
        if (isConfirm) {
            const path = `https://freshskinweb.onrender.com/admin/orders/edit/${id}`;

            const response = await fetch(path, {
                method: "PATCH",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    orderStatus: "CANCELED"
                })
            });

            const dataResponse = await response.json();

            if (dataResponse.code === 200) {
                setAlertMessage("Đã hủy đơn hàng thành công!");
                setAlertSeverity("success");
                setTimeout(() => {
                    router.push("/user/orders");
                }, 2000)
            } else {
                setAlertMessage(dataResponse.message);
                setAlertSeverity("error");
            }
        }
    }

    const handleConfirm = async (id: number) => {
        const isConfirm = confirm("Bạn có chắc đã nhận được đơn đơn hàng?");
        if (isConfirm) {
            const path = `https://freshskinweb.onrender.com/admin/orders/edit/${id}`;

            const response = await fetch(path, {
                method: "PATCH",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    orderStatus: "COMPLETED"
                })
            });

            const dataResponse = await response.json();

            if (dataResponse.code === 200) {
                setAlertMessage("Đã xác nhận đơn hàng thành công!");
                setAlertSeverity("success");
                setTimeout(() => {
                    location.reload();
                }, 2000)
            } else {
                setAlertMessage(dataResponse.message);
                setAlertSeverity("error");
            }
        }
    }

    return (
        <>
            {alertMessage && (
                <Alert severity={alertSeverity} sx={{ position: "fixed", width: "600px", height: "60px", right: "5%", top: "5%", fontSize: "16px", zIndex: "999999" }}>
                    {alertMessage}
                </Alert>
            )}
            <ul className="flex items-center container mx-auto px-3">
                <li>
                    <Link href="/" className="flex items-center">
                        <span className="text-[#333] text-[15px] font-[400] hover:text-secondary">Trang chủ</span>
                        <span><MdNavigateNext className="ml-[10px] text-[18px] mr-[10px]" /></span>
                    </Link>
                </li>
                <li>
                    <Link href="/user/profile" className="flex items-center">
                        <span className="text-[#333] text-[15px] font-[400] hover:text-secondary">Tài khoản</span>
                        <span><MdNavigateNext className="ml-[10px] text-[18px] mr-[10px]" /></span>
                    </Link>
                </li>
                <li className="text-secondary text-[15px] font-[400]">
                    Đơn hàng #{id}
                </li>
            </ul>
            <div className="flex container mx-auto px-[15px] mt-[40px]">
                <ProfileLeft />
                <div className="px-[15px] flex-1">
                    <div className="text-[19px] font-[400] text-[#212B25] mb-[27px]">Chi tiết đơn hàng #{id}</div>
                    {data.orderStatus == "PENDING" && (
                        <div className="flex items-center justify-between">
                            <div className="text-[14px] text-textColor">Trạng thái đơn hàng: <span className="font-[600] text-[#E49C06]">Đang xử lý</span></div>
                            <div className="text-[14px] text-textColor">Bạn có muốn: <span onClick={() => handleCancel(parseInt(data.orderId))} className="font-[600] text-[#DD153C] cursor-pointer">Hủy đơn hàng</span></div>
                        </div>
                    )}
                    {data.orderStatus == "DELIVERING" && (
                        <div className="flex items-center justify-between">
                            <div className="text-[14px] text-textColor">Trạng thái đơn hàng: <span className="font-[600] text-[#E49C06]">Đang xử lý</span></div>
                            <div onClick={() => { handleConfirm(data.orderId) }} className="text-[14px] text-primary">Xác nhận đã nhận đơn hàng</div>
                        </div>
                    )}
                    {data.orderStatus == "CANCELED" && (
                        <div className="text-[14px] text-textColor">Trạng thái đơn hàng: <span className="font-[600] text-[#DD153C]">Đã hủy</span></div>
                    )}
                    {data.orderStatus == "COMPLETED" && (
                        <div className="text-[14px] text-textColor">Trạng thái đơn hàng: <span className="font-[600] text-[#33A140]">Đã xác nhận</span></div>
                    )}
                    {data.paymentStatus == "PENDING" && (
                        <div className="text-[14px] text-textColor mt-[5px]">Trạng thái thanh toán: <span className="font-[600] text-[#E49C06]">Chờ thanh toán</span></div>
                    )}
                    {data.paymentStatus == "PAID" && (
                        <div className="text-[14px] text-textColor mt-[5px]">Trạng thái thanh toán: <span className="font-[600] text-[#33A140]">Đã thanh toán</span></div>
                    )}
                    {data.paymentStatus == "FAILED" && (
                        <div className="text-[14px] text-textColor mt-[5px]">Trạng thái thanh toán: <span className="font-[600] text-[#DD153C]">Thanh toán thất bại</span></div>
                    )}
                    {data.paymentStatus == "CANCELED" && (
                        <div className="text-[14px] text-textColor mt-[5px]">Trạng thái thanh toán: <span className="font-[600] text-[#DD153C]">Thanh toán bị hủy</span></div>
                    )}
                    {data.paymentStatus == "REFUNDED" && (
                        <div className="text-[14px] text-textColor mt-[5px]">Trạng thái thanh toán: <span className="font-[600] text-[#E49C06]">Hoàn tiền</span></div>
                    )}
                    <div className="pl-[15px] mt-[30px] flex">
                        <div className="w-[60%] pr-[10px]">
                            <div className="uppercase text-[16px] font-[350] mb-[6px]">Địa chỉ giao hàng</div>
                            <div className="h-[110px] pt-[13px] pr-[25px] pb-[30px] pl-[20px] rounded-[5px] border border-solid border-[#e0e0e0]">
                                <div className="uppercase font-[650] text-[14px] text-[#212B25] mb-[8px]">{data.firstName} {data.lastName}</div>
                                <div className="text-[14px] mb-[8px] text-[#212B25]">Địa chỉ: {data.address}</div>
                                <div className="text-[14px] mb-[8px] text-[#212B25]">Số điện thoại: {data.phoneNumber}</div>
                            </div>
                        </div>
                        <div className="flex-1 pl-[5px]">
                            <div className="uppercase text-[16px] font-[350] mb-[6px]">Thanh toán</div>
                            <div className="h-[110px] pt-[13px] pr-[25px] pb-[30px] pl-[20px] rounded-[5px] border border-solid border-[#e0e0e0]">
                                {data.paymentMethod == "CASH" ? "Thanh toán khi nhận hàng" : "Chuyển khoản"}
                            </div>
                        </div>
                    </div>
                    <div className="ml-[15px] px-[20px] border border-solid border-[#e0e0e0] mt-[24px] rounded-[5px]">
                        <table className="w-full">
                            <thead>
                                <tr>
                                    <td className="text-textColor text-left py-[25px] text-[16px] font-[350] border-b border-solid border-[#e0e0e0]">Sản phẩm</td>
                                    <td className="text-textColor text-center py-[25px] text-[16px] font-[350] border-b border-solid border-[#e0e0e0]">Đơn giá</td>
                                    <td className="text-textColor text-center py-[25px] text-[16px] font-[350] border-b border-solid border-[#e0e0e0]">Số lượng</td>
                                    <td className="text-textColor text-center py-[25px] text-[16px] font-[350] border-b border-solid border-[#e0e0e0]">Tổng</td>
                                </tr>
                            </thead>
                            <tbody className="w-full">
                                {data.orderItems.map((item: any, index: number) => (
                                    <tr key={index}>
                                        <td className="text-left py-[25px] text-[16px] font-[350] border-b border-solid border-[#e0e0e0]">
                                            <div className="flex items-center justify-center">
                                                <div className="w-[90px] aspect-square">
                                                    <img src={item.productVariant.product.thumbnail[0]} className="w-full h-full object-cover" />
                                                </div>
                                                <div className="text-[14px] flex-1 mx-[15px] text-textColor">{item.productVariant.product.title}</div>
                                            </div>
                                        </td>
                                        <td className="text-textColor text-center py-[25px] text-[16px] px-[15px] font-[350] border-b border-solid border-[#e0e0e0]">{(item.productVariant.price * (1 - item.productVariant.product.discountPercent / 100)).toLocaleString("en-US")}<sup className="underline">đ</sup></td>
                                        <td className="text-textColor text-center py-[25px] text-[16px] px-[10px] font-[350] border-b border-solid border-[#e0e0e0]">{item.quantity}</td>
                                        <td className="text-textColor text-center py-[25px] text-[16px] px-[10px] font-[350] border-b border-solid border-[#e0e0e0]">{(item.productVariant.price * (1 - item.productVariant.product.discountPercent / 100) * item.quantity).toLocaleString("en-US")}<sup className="underline">đ</sup></td>
                                    </tr>
                                ))}
                                <tr>
                                    <td colSpan={2} className="p-[15px] pt-[25px] text-[16px] text-[#1c1c1c] text-right">Khuyến mại</td>
                                    {data.discountAmount > 0 ? (
                                        <td colSpan={2} className="flex-1 p-[15px] text-[16px] text-[#1c1c1c] text-right">- {data.discountAmount.toLocaleString("en-US")}<sup className="underline">đ</sup></td>
                                    ) : (
                                        <td colSpan={2} className="flex-1 p-[15px] text-[16px] text-[#1c1c1c] text-right">- 0<sup className="underline">đ</sup></td>
                                    )}
                                </tr>
                                <tr>
                                    <td colSpan={2} className="p-[15px] pt-[25px] text-[16px] text-[#1c1c1c] text-right">Phí vận chuyển</td>
                                    {data.priceShipping ? (
                                        <td colSpan={2} className="flex-1 p-[15px] text-[16px] text-[#1c1c1c] text-right">{data.priceShipping.toLocaleString("en-US")}<sup className="underline">đ</sup></td>
                                    ) : (
                                        <td colSpan={2} className="flex-1 p-[15px] text-[16px] text-[#1c1c1c] text-right">0<sup className="underline">đ</sup></td>
                                    )}
                                </tr>
                                <tr>
                                    <td colSpan={2} className="p-[15px] pt-[25px] text-[16px] text-[#1c1c1c] text-right">Tổng tiền</td>
                                    <td colSpan={2} className="flex-1 p-[15px] text-[19px] font-[600] text-[#CA170E] text-right">{data.totalPrice.toLocaleString("en-US")}<sup className="underline">đ</sup></td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </>
    )
}