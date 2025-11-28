"use client"

import { TbPlayerTrackNextFilled, TbPlayerTrackPrevFilled } from "react-icons/tb";

export default function Pagination(props: {
    totalPages: number,
    currentPage: number
}) {
    const { totalPages = 1, currentPage = 1 } = props;
    const pageNumbers = Array.from({ length: totalPages }, (_, index) => index + 1);

    // Phân trang
    const handlePagination = (page: number) => {
        const url = new URL(location.href);

        if (page) {
            url.searchParams.set("page", page.toString());
        }
        else {
            url.searchParams.delete("page");
        }

        location.href = url.href;
    }
    // Hết phân trang

    return (
        <>
            <div className="text-center mt-[50px] mb-[-25px]">
                <ul className="flex items-center justify-center cursor-pointer">
                    {currentPage > 1 && (
                        <li 
                            className="mx-[3px] rounded-[5px] text-[10px] font-[400] text-primary bg-white w-[30px] border border-solid border-primary h-[30px] flex items-center justify-center hover:text-white hover:bg-primary"
                            onClick={() => handlePagination(currentPage - 1)}
                        >
                            <TbPlayerTrackPrevFilled />
                        </li>
                    )}
                    {pageNumbers.map((page) => (
                        <li
                            key={page}
                            onClick={() => handlePagination(page)}
                            className={`mx-[3px] rounded-[5px] text-[14px] font-[400] w-[30px] h-[30px] flex items-center justify-center ${page === currentPage
                                ? 'text-white bg-primary'
                                : 'text-primary bg-white border border-solid border-primary hover:text-white hover:bg-primary'
                                }`}
                        >
                            {page}
                        </li>
                    ))}
                    {currentPage < totalPages && (
                        <li
                            className="mx-[3px] rounded-[5px] text-[10px] font-[400] text-primary bg-white w-[30px] border border-solid border-primary h-[30px] flex items-center justify-center hover:text-white hover:bg-primary"
                            onClick={() => handlePagination(currentPage + 1)}
                        >
                            <TbPlayerTrackNextFilled />
                        </li>
                    )}
                </ul>
            </div>
        </>
    )
}