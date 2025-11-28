"use client"

import Link from "next/link";
import { useContext, useEffect, useState } from "react";
import { MdCancel, MdNavigateNext } from "react-icons/md";
import { SettingProfileContext } from "../../layout";
import ProfileLeft from "@/app/components/ProfileUser/ProfileLeft";
import { Alert, Tooltip } from "@mui/material";
import Cookies from "js-cookie";
import { useRouter } from "next/navigation";
import { TiTick } from "react-icons/ti";

export default function OrdersHistoryPage() {
    const [alertMessage, setAlertMessage] = useState<string>("");
    const [alertSeverity, setAlertSeverity] = useState<
        "success" | "error" | "info" | "warning"
    >("info");
    const [data, setData] = useState([]);
    const router = useRouter();
    const tokenUser = Cookies.get("tokenUser");

    useEffect(() => {
        if (!tokenUser) {
            router.push("/user/login");
        }
    }, [tokenUser]);

    const settingProfile = useContext(SettingProfileContext);

    if (!settingProfile) {
        return null;
    }

    const { profile } = settingProfile;

    useEffect(() => {
        if (profile.userID != 0) {
            const fetchData = async () => {
                const response = await fetch(`https://freshskinweb.onrender.com/home/orders/user/${profile.userID}`);
                const dataResponse = await response.json();
                setData(dataResponse.data);
            };

            fetchData();
        }

    }, [profile]);

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
                    location.reload();
                }, 2000)
            } else {
                setAlertMessage(dataResponse.message);
                setAlertSeverity("error");
                setTimeout(() => {
                    setAlertMessage("");
                }, 2000)
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
                    Đơn hàng
                </li>
            </ul>
            <div className="flex container mx-auto px-[15px] mt-[40px]">
                <ProfileLeft />
                <div className="px-[15px] flex-1">
                    <div className="uppercase text-[19px] font-[400] text-[#212B25] mb-[27px]">Đơn hàng của bạn</div>
                    {data && (
                        <div>
                            <table className="w-full border-collapse">
                                <thead className="sticky">
                                    <tr className="h-[35px]">
                                        <th className="w-[18%] text-white p-[5px] text-[14px] border border-solid border-[#ebebeb] bg-secondary">Đơn hàng</th>
                                        <th className="w-[18%] text-white p-[5px] text-[14px] border border-solid border-[#ebebeb] bg-secondary">Ngày</th>
                                        <th className="w-[28%] text-white p-[5px] text-[14px] border border-solid border-[#ebebeb] bg-secondary">Địa chỉ</th>
                                        <th className="w-[18%] text-white p-[5px] text-[14px] border border-solid border-[#ebebeb] bg-secondary">Giá trị đơn hàng</th>
                                        <th className="w-[18%] text-white p-[5px] text-[14px] border border-solid border-[#ebebeb] bg-secondary">TT đơn hàng</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {data.length == 0 && (
                                        <tr className="">
                                            <td colSpan={5} className="border border-solid border-[#ebebeb] text-[#1c1c1c] text-[14px] pt-[20px] pb-[30px] text-center">
                                                Không có đơn hàng nào
                                            </td>
                                        </tr>
                                    )}
                                    {data.length > 0 && (
                                        data.map((item: any, index: number) => (
                                            <tr key={index}>
                                                <td className="w-[18%] cursor-pointer text-[#2F80ED] hover:text-textColor py-[20px] px-[5px] text-[14px] border border-solid border-[#ebebeb] text-center">
                                                    <Link href={`/user/orders/${item.orderId}`}>#{item.orderId}</Link>
                                                </td>
                                                <td className="w-[18%] text-[#1C1C1C] py-[20px] px-[5px] text-[14px] border border-solid border-[#ebebeb] text-center">{item.orderDate.slice(0, 10)}</td>
                                                <td className="w-[28%] text-[#1C1C1C] py-[20px] px-[5px] text-[14px] border border-solid border-[#ebebeb] text-center">{item.address}</td>
                                                <td className="w-[18%] text-[#1C1C1C] py-[20px] px-[5px] text-[14px] border border-solid border-[#ebebeb] text-center">{(item.totalPrice).toLocaleString("en-US")}<sup className="underline">đ</sup></td>
                                                {item.orderStatus == "PENDING" && (
                                                    <td className="w-[18%] text-[#1C1C1C] py-[20px] px-[5px] text-[14px] border border-solid border-[#ebebeb] text-center">
                                                        <span>Đang xử lý</span> <Tooltip title="Hủy đơn hàng"><MdCancel onClick={() => { handleCancel(item.orderId) }} className="text-red-500 text-[16px] ml-[41%] mt-[5px] cursor-pointer" /></Tooltip>
                                                    </td>
                                                )}
                                                {item.orderStatus == "CANCELED" && (
                                                    <td className="w-[18%] text-[#1C1C1C] py-[20px] px-[5px] text-[14px] border border-solid border-[#ebebeb] text-center">Đã hủy</td>
                                                )}
                                                {item.orderStatus == "DELIVERING" && (
                                                    <td className="w-[18%] text-[#1C1C1C] py-[20px] px-[5px] text-[14px] border border-solid border-[#ebebeb] text-center">
                                                        <span>Đang giao</span> <Tooltip title="Xác nhận đã nhận đơn hàng"><TiTick onClick={() => { handleConfirm(item.orderId) }} className="text-green-500 text-[20px] ml-[41%] mt-[5px] cursor-pointer" /></Tooltip >
                                                    </td>
                                                )}
                                                {item.orderStatus == "COMPLETED" && (
                                                    <td className="w-[18%] text-[#1C1C1C] py-[20px] px-[5px] text-[14px] border border-solid border-[#ebebeb] text-center">Đã hoàn thành</td>
                                                )}
                                            </tr>
                                        ))
                                    )}
                                </tbody>
                            </table>
                        </div>
                    )}
                </div>
            </div>
        </>
    )
}