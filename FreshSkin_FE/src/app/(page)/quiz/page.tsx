"use client"

import Link from "next/link";
import { IoMdInformationCircleOutline } from "react-icons/io";
import { SiTicktick } from "react-icons/si";

export default function QuizPage() {
    return (
        <>
             <div className=" bg-gradient-to-br from-green-100 to-white via-white py-[100px] flex justify-center">
                <div className="w-[770px] flex mb-[48px]">
                    <div className="w-[60%]">
                        <h1 className="text-[40px] text-[#9acba4] font-[700] mb-[24px]">Kiểm tra sức khỏe da của bạn ngay!</h1>
                        <SiTicktick className="text-[#818282] mb-[10px]" />
                        <p className="text-[11px] text-[#ABACAD] mb-[25px]">Tham vấn y khoa: <Link href="https://vnexpress.net/suc-khoe/cac-benh/da-lieu/chuyen-gia/79140" className="cursor-pointer underline">Thạc sĩ - Bác sĩ Vũ Thị trang </Link> ngày 27 tháng 9, 2024</p>
                        <button className="uppercase text-white bg-[#9acba4] hover:bg-[#aed8d3] border border-solid border-transparent px-[22px] rounded-[8px] text-[16px] font-[600] h-[48px]">
                            <Link href="/quiz/test">
                                Kiểm tra ngay thôi
                            </Link>
                        </button>
                    </div>
                    <div className="flex-1 ml-[30px]">
                        <div className="w-[270px] h-[255px]">
                            <img src="https://res.cloudinary.com/dr53sfboy/image/upload/v1742357776/product-brand/dsa_20250319-041615_16.webp" className="w-full h-full object-cover" />
                        </div>
                    </div>
                </div>
            </div>
            <div className="flex justify-center mx-auto text-[16px] mb-[64px] pb-[40px] border-b-8 border-solid border-[#F7F9FC] w-[568px]">Chào mừng bạn đến với bài trắc nghiệm chăm sóc da của chúng tôi! Hiểu rõ làn da của mình là bước đầu tiên để có được một làn da khỏe mạnh và rạng rỡ. Bài quiz này sẽ giúp bạn xác định loại da, các vấn đề da mà bạn đang gặp phải, và cung cấp các gợi ý chăm sóc da phù hợp nhất. Chỉ mất vài phút để hoàn thành, bạn sẽ nhận được những lời khuyên giúp làn da của bạn trở nên đẹp hơn mỗi ngày.</div>
            <h2 className="flex justify-center mr-[397px] mb-[10px] text-[14px] text-[#8C8C8C] font-[900] items-center">
                <IoMdInformationCircleOutline className="text-[#478AF4] mr-[10px]" />
                Miễn trừ trách nhiệm
            </h2>
            <div className="flex justify-center mx-auto pl-[25px] mb-[32px] text-[16px] border-[#F7F9FC] w-[570px]">Công cụ này không cung cấp lời khuyên y tế mà chỉ dùng để cung cấp thông tin. Nó không thay thế cho lời khuyên, chẩn đoán hoặc điều trị y tế chuyên nghiệp từ bác sĩ. Nếu bạn đang tìm phương pháp điều trị hay đang điều trị, đừng bỏ qua lời khuyên y tế từ bác sĩ chỉ vì một số thông tin bạn đã đọc trên website của chúng tôi. Nếu bạn nghĩ mình đang trong tình trạng khẩn cấp, hãy liên hệ với bác sĩ ngay lập tức.</div>
        </>
    )
}