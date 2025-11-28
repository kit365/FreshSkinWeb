"use client"

import FormInput from "@/app/components/Form/FormInput";
import { useState } from "react";
import { FaSearch } from "react-icons/fa";

export default function SearchOrderPage() {
    const [status, setStatus] = useState("phone");

    const [data, setData] = useState([]);

    const handleSubmitPhone = async (event: any) => {
        event.preventDefault();
        const phone = event.target.phone.value;

        const response = await fetch(`https://freshskinweb.onrender.com/home/orders?phone=${phone}`);
        const dataResponse = await response.json();
        setData(dataResponse.data);
    }

    const handleSubmitEmail = async (event: any) => {
        event.preventDefault();
        const email = event.target.email.value;

        const response = await fetch(`https://freshskinweb.onrender.com/home/orders?email=${email}`);
        const dataResponse = await response.json();
        setData(dataResponse.data);
    }

    const handleSubmitPhoneEmail = async (event: any) => {
        event.preventDefault();
        const phone = event.target.phone.value;
        const email = event.target.email.value;

        const response = await fetch(`https://freshskinweb.onrender.com/home/orders?email=${email}&phone=${phone}`);
        const dataResponse = await response.json();
        setData(dataResponse.data);
    }

    return (
        <>
            <div className="bg-[#F5F5F5] py-[35px] px-[18%]">
                <div className="container bg-white p-[25px] rounded-[5px] w-full flex items-start">
                    <div className="bg-[rgba(228,228,228,1)] rounded-[5px] w-[460px] px-[10px] search-order">
                        <div className="flex items-center justify-center text-[17px] font-[700] py-[18px] mb-[15px] border-b boder-solid border-white">
                            <FaSearch className="mr-[5px]" />
                            Kiểm tra đơn hàng của bạn
                        </div>
                        <div className="">
                            <div className="text-[13px] mb-[5px]">Kiểm tra bằng</div>
                            <div className="flex items-center mb-[15px]">
                                <input
                                    type="radio"
                                    id="phone"
                                    name="option"
                                    value="phoneOption"
                                    defaultChecked
                                    onClick={() => setStatus("phone")}
                                />
                                <label htmlFor="phone" className="text-[13px] font-[400] ml-[10px] mr-[15px]">Số điện thoại</label>
                                <input
                                    type="radio"
                                    id="email"
                                    name="option"
                                    value="emailOption"
                                    onClick={() => setStatus("email")}
                                />
                                <label htmlFor="email" className="text-[13px] font-[400] ml-[10px] mr-[15px]">Email</label>
                                <input
                                    type="radio"
                                    id="both"
                                    name="option"
                                    value="bothOption"
                                    onClick={() => setStatus("both")}
                                />
                                <label htmlFor="both" className="text-[13px] font-[400] ml-[10px] mr-[15px]">Số điện thoại và Email</label>
                            </div>
                            {status == "phone" && (
                                <form onSubmit={handleSubmitPhone} className="">
                                    <label className="block text-[13px] mb-[5px]" htmlFor="phoneInput">Số điện thoại</label>
                                    <FormInput
                                        placeholder="0909 xxx xxx"
                                        name="phone"
                                        className="text-[13px] px-[12px] rounded-[3px] w-full h-[30px] border border-solid border-[#ccc] focus:border-[#66afe9]"
                                        id="phoneInput"
                                    />
                                    <div className="flex justify-end">
                                        <button
                                            type="submit"
                                            className="text-white text-[13px] border border-solid border-transparent rounded-[4px] font-[400] bg-[rgba(53,126,189,1)] px-[12px] py-[7px] text-center mb-[10px]"
                                        >
                                            Kiểm tra
                                        </button>
                                    </div>
                                </form>
                            )}
                            {status == "email" && (
                                <form onSubmit={handleSubmitEmail} className="">
                                    <label className="block text-[13px] mb-[5px]" htmlFor="emailInput">Địa chỉ Email</label>
                                    <FormInput
                                        type="email"
                                        placeholder="email@gmail.com"
                                        name="email"
                                        className="text-[13px] px-[12px] rounded-[3px] w-full h-[30px] border border-solid border-[#ccc] focus:border-[#66afe9]"
                                        id="emailInput"
                                    />
                                    <div className="flex justify-end">
                                        <button
                                            type="submit"
                                            className="text-white text-[13px] border border-solid border-transparent rounded-[4px] font-[400] bg-[rgba(53,126,189,1)] px-[12px] py-[7px] text-center mb-[10px]"
                                        >
                                            Kiểm tra
                                        </button>
                                    </div>
                                </form>
                            )}
                            {status == "both" && (
                                <form onSubmit={handleSubmitPhoneEmail} className="">
                                    <label className="block text-[13px] mb-[5px]" htmlFor="phoneInput">Số điện thoại</label>
                                    <FormInput
                                        placeholder="0909 xxx xxx"
                                        name="phone"
                                        className="text-[13px] px-[12px] rounded-[3px] w-full h-[30px] border border-solid border-[#ccc] focus:border-[#66afe9]"
                                        id="phoneInput"
                                    />
                                    <label className="block text-[13px] mb-[5px]" htmlFor="emailInput">Địa chỉ Email</label>
                                    <FormInput
                                        type="email"
                                        placeholder="email@gmail.com"
                                        name="email"
                                        className="text-[13px] px-[12px] rounded-[3px] w-full h-[30px] border border-solid border-[#ccc] focus:border-[#66afe9]"
                                        id="emailInput"
                                    />
                                    <div className="flex justify-end">
                                        <button
                                            type="submit"
                                            className="text-white text-[13px] border border-solid border-transparent rounded-[4px] font-[400] bg-[rgba(53,126,189,1)] px-[12px] py-[7px] text-center mb-[10px]"
                                        >
                                            Kiểm tra
                                        </button>
                                    </div>
                                </form>
                            )}
                        </div>
                    </div>
                    <div className="flex-1 ml-[40px]">
                        {data.length > 0 && data.map((item: any, index: number) => (
                            <div key={index} className="shadow-search-order flex border border-solid border-[#f0f0f0] rounded-[3px] mb-[15px] ">
                                <div className="w-[59%] px-[10px]">
                                    <div className="font-[600] text-[18px] py-[9px] mb-[20px]">Mã đơn hàng: #{item.orderId}</div>
                                    <div className="text-[13px]">Họ và tên khách hàng: {item.firstName} {item.lastName}</div>
                                    <div className="text-[13px]">Số điện thoại: {item.phoneNumber}</div>
                                    <div className="text-[13px]">Email: {item.email}</div>
                                    <div className="text-[13px]">Ngày mua: {item.orderDate}</div>
                                    <div className="text-[13px]">Địa chỉ giao hàng: {item.address}</div>
                                    {item.orderStatus == "PENDING" && (
                                        <div className="text-[13px] text-[#FF0000] mt-[30px] mb-[10px]">Trạng thái thanh toán: <span className="font-[600]">Đang xử lý</span></div>
                                    )}
                                    {item.orderStatus == "COMPLETED" && (
                                        <div className="text-[13px] text-[#FF0000] mt-[30px] mb-[10px]">Trạng thái thanh toán: <span className="font-[600]">Đã xác nhận</span></div>
                                    )}
                                    {item.orderStatus == "CANCELED" && (
                                        <div className="text-[13px] text-[#FF0000] mt-[30px] mb-[10px]">Trạng thái thanh toán: <span className="font-[600]">Đã hủy</span></div>
                                    )}
                                </div>
                                <div className="flex-1 px-[10px]">
                                    <div className="font-[600] text-[18px] py-[9px] mb-[10px]">Giá trị đơn hàng</div>
                                    <div className="flex items-end">
                                        <div className="text-[28px] text-[#FF0000]">{parseFloat((item.totalPrice.toFixed(0))).toLocaleString("en-US")}</div>
                                        <div className="text-[18px] ml-[7px] mb-[5px]">VNĐ</div>
                                    </div>
                                    <div className="mt-[37px] flex items-center">
                                        <div className="text-[18px] text-[#333]">Số lượng sản phẩm : </div>  
                                        <span className="text-[28px] text-[#FF0000] ml-[5px] mb-[3px]">{item.totalAmount}</span>
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            </div>
        </>
    )
}