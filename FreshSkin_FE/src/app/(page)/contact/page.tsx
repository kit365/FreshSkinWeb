"use client"

import FormInput from "@/app/components/Form/FormInput";
import { useContext } from "react";
import { SettingProfileContext } from "../layout";
import Link from "next/link";
import { MdNavigateNext } from "react-icons/md";

export default function ContactPage() {
    const settingProfile = useContext(SettingProfileContext);

    if (!settingProfile) {
        return null;
    }

    const { setting } = settingProfile;
    
    return (
        <>
            <ul className="flex items-center container mx-auto px-3">
                <li>
                    <Link href="/" className="flex items-center">
                        <span className="text-[#333] text-[15px] font-[400] hover:text-secondary">Trang chủ</span>
                        <span><MdNavigateNext className="ml-[10px] text-[18px] mr-[10px]" /></span>
                    </Link>
                </li>
                <li className="text-secondary text-[15px] font-[400]">
                    Liên hệ
                </li>
            </ul>
            <div className="container mx-auto flex items-start mt-[25px]">

                <div className="w-[50%] px-[10px]">
                    <div className="mb-[15px]">
                        <h4 className="uppercase text-[15px] font-[600] mb-[13px]">Nơi giải đáp toàn bộ mọi thắc mắc của bạn?</h4>
                        <div className="text-[#00090f] text-[14px] mb-[10px]">Không phải những người đẹp là những người hạnh phúc, mà những người hạnh phúc mới là những người đẹp.</div>
                        <div className="text-[#00090f] text-[14px] mb-[10px]"><b>Địa chỉ:</b> {setting?.address || ""}</div>
                        <div className="text-[#00090f] text-[14px] mb-[5px]">
                            <b>Hotline: </b>
                            <span className="font-[600] cursor-pointer text-secondary hover:text-primary">{setting?.phone || ""}</span>
                        </div>
                        <div className="text-[#00090f] text-[14px] mb-[5px]">
                            <b>Email: </b>
                            <span className="font-[600] cursor-pointer text-secondary hover:text-primary">{setting?.email || ""}</span>
                        </div>
                    </div>
                    <div className="">
                        <h4 className="uppercase text-[15px] font-[600] mb-[13px]">Liên hệ với chúng tôi</h4>
                        <form action="" className="">
                            <div className="">
                                <FormInput
                                    name="fullname"
                                    placeholder="Họ và tên"
                                    className="w-[304px] mr-[15px]"
                                />
                                <FormInput
                                    type="email"
                                    name="email"
                                    placeholder="Email"
                                    className="w-[304px]"
                                />
                            </div>
                            <FormInput
                                name="phone"
                                placeholder="Điện thoại"
                                className="w-full"
                            />
                            <textarea name="content" placeholder="Nội dung" rows={6} className="mb-[15px] py-[10px] outline-none placeholder-[#757575] px-[20px] rounded-[4px] border border-solid border-[#e1e1e1] w-full" />
                            <button type="submit" className="bg-primary hover:bg-[#fcaf17] rounded-[5px] text-white px-[20px] h-[35px] text-[14px]">Gửi thông tin</button>
                        </form>
                    </div>
                </div>
                <div className="w-[50%] px-[10px]">
                    <iframe src="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3918.6100105370224!2d106.8073080748058!3d10.84112758931163!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x31752731176b07b1%3A0xb752b24b379bae5e!2zVHLGsOG7nW5nIMSQ4bqhaSBo4buNYyBGUFQgVFAuIEhDTQ!5e0!3m2!1svi!2s!4v1738056836546!5m2!1svi!2s" width="100%" height="570"></iframe>
                </div>
            </div>
        </>
    )
}