"use client"

import { Url } from "next/dist/shared/lib/router/router";
import Link from "next/link";
import { ReactNode, useContext } from "react";
import { FaFacebookF, FaInstagram, FaTwitter, FaYoutube } from "react-icons/fa6";
import TitleFooter from "../title/TitleFooter";
import ContentFooter from "./ContentFooter";
import { SettingProfileContext } from "@/app/(page)/layout";

export default function Footer() {
    const settingProfile = useContext(SettingProfileContext);

    if (!settingProfile) {
        return null; 
      }

    const { setting } = settingProfile;

    interface buttonIcon {
        icon: ReactNode,
        link: Url
    }

    interface PolicySupport {
        content: string,
        link: string
    }

    const menuButton: buttonIcon[] = [
        {
            icon: <FaFacebookF />,
            link: setting?.facebook || "#"
        },
        {
            icon: <FaTwitter />,
            link: setting?.twitter || "#"
        },
        {
            icon: <FaYoutube />,
            link: setting?.youtube || "#"
        },
        {
            icon: <FaInstagram />,
            link: setting?.instagram || "#"
        }
    ]

    const contentPolicy: PolicySupport[] = [
        {
            content: "Chính sách và quy định chung",
            link: "/chinh-sach-va-quy-dinh-chung"
        },
        {
            content: "Chính sách thanh toán",
            link: "/chinh-sach-thanh-toan"
        },
        {
            content: "Chính sách giao nhận",
            link: "/chinh-sach-giao-nhan"
        },
        {
            content: "Chính sách đổi trả sản phẩm",
            link: "/chinh-sach-doi-tra-san-pham"
        },
        {
            content: "Chính sách bảo mật thông tin cá nhân",
            link: "/chinh-sach-bao-mat-thong-tin-ca-nhan"
        },
        {
            content: "Điều khoản sử dụng",
            link: "/dieu-khoan-su-dung"
        }
    ]

    const contentSupport: PolicySupport[] = [
        {
            content: "Quyền lợi Fresh-er",
            link: "/quyen-loi-fresher"
        },
        {
            content: "Thông tin thành viên",
            link: "/thong-tin-thanh-vien"
        },
        {
            content: "Tích điểm đổi quà",
            link: "/tich-diem-doi-qua"
        },
        {
            content: "Hỗ trợ kỹ thuật",
            link: "/ho-tro-ki-thuat"
        },
        {
            content: "Câu hỏi thường gặp",
            link: "/cau-hoi-thuong-gap"
        },
        {
            content: "Liên hệ",
            link: "/contact"
        }
    ]

    return (
        <>
            <div className="bg-[#B2D18F] container mx-auto flex items-center px-[20px] rounded-[10px] mt-[60px] justify-between">
                <div className="">
                    <div className="text-[22px] font-[600] uppercase text-white">
                        Đăng ký nhận tin từ Fresh Skin
                    </div>
                    <div className="text-[14px] font-[400] text-white">
                        Nhận thông tin sản phẩm mới nhất và các chương trình khuyến mãi.
                    </div>
                </div>
                <div className="w-[166px] h-[114px]">
                    <img src="https://res.cloudinary.com/dr53sfboy/image/upload/v1742359278/product-brand/dsadas_20250319-044118_4.webp" className="w-full h-full object-cover" />
                </div>
                <form action="" className="relative">
                    <input type="email" placeholder="Nhập địa chỉ email" content="email" className="w-[450px] py-[15px] pl-[20px] pr-[125px] rounded-[50px] text-[14px]" />
                    <button type="submit" className="text-[14px] text-white bg-primary rounded-tr-[48px] rounded-br-[48px] py-[15px] px-[30px] absolute right-[0px] hover:bg-[#4E7661]">Đăng ký</button>
                </form>
            </div>
            <div className="container mx-auto mt-[30px] px-[10px] flex pb-[50px]">
                <div className="w-[304px] mr-[30px]">
                    <div className="w-[150px] h-[60px]">
                        <img src={setting?.logo || "https://res.cloudinary.com/dr53sfboy/image/upload/v1742010540/product-brand/test_20250315-034900_4.png"} alt="logo" className="w-full h-full object-cover" />
                    </div>
                    <div className="text-[14px] my-[10px]">Không phải những người đẹp là những người hạnh phúc, mà những người hạnh phúc mới là những người đẹp.</div>
                    <div className="flex items-center">
                        {menuButton.map((item: buttonIcon, index: number) => (
                            <Link href={item.link} key={index}>
                                <div className="w-[35px] h-[35px] mr-[10px] bg-[#4E7661] text-white flex items-center justify-center rounded-[5px] hover:bg-[#719181]">
                                    {item.icon}
                                </div>
                            </Link>
                        ))}
                    </div>
                </div>
                <div className="w-[420px] pr-[50px]">
                    <TitleFooter title="Thông tin liên hệ" />
                    <div className="text-[14px] mb-[5px]"><b>Địa chỉ: </b>{setting?.address || ""}</div>
                    <div className="text-[14px] mb-[5px]">
                        <b>Điện thoại: </b>
                        <span className="text-primary font-[600] hover:text-secondary cursor-pointer">{setting?.phone || ""}</span>
                    </div>
                    <div className="text-[14px] mb-[5px]">
                        <b>Email: </b>
                        <span className="text-primary font-[600] hover:text-secondary cursor-pointer">{setting?.email || ""}</span>
                    </div>
                </div>
                <div className="w-[304px]">
                    <TitleFooter title="Chính sách" />
                    {contentPolicy.map((item: any, index: number) => (
                        <ContentFooter title={item.content} link={item.link} key={index} />
                    ))}
                </div>
                <div className="flex-1">
                    <TitleFooter title="Hỗ trợ" />
                    {contentSupport.map((item: any, index: number) => (
                        <ContentFooter title={item.content} link={item.link} key={index} />
                    ))}
                </div>
            </div>
            <div className="text-[14px] text-secondary mb-[5px] container mx-auto py-[20px] border-t border-solid border-black">
                <span className="font-[600 cursor-pointer">{setting?.copyright || ""}</span>
            </div>
        </>
    )
}