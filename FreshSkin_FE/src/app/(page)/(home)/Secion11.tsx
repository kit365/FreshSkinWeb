"use client";

import BlogItem from "@/app/components/Blog/BlogItem";
import ButtonSeeAll from "@/app/components/Button/ButtonSeeAll";
import Title from "@/app/components/title/Title";
import { useState } from "react";

export default function Section11(props: any) {
    const { dataInit = [] } = props;
    const [dataBlogsCurrent, setDataBlogsCurrent] = useState(dataInit[0].blogs.slice(0, 4));

    const [currentButton, setCurrentButton] = useState(dataInit[0].slug);

    const dataDuongChatChoLanDa = dataInit[3]?.blogs.slice(0, 4);
    const dataGocReview = dataInit[2]?.blogs.slice(0, 4);
    const dataCachChamSocDa = dataInit[1]?.blogs.slice(0, 4);
    const dataTinTuc = dataInit[0]?.blogs.slice(0, 4);

    const dataButton = [
        {
            data: dataTinTuc,
            currentStatus: dataInit[0].slug,
            content: dataInit[0].title
        },
        {
            data: dataCachChamSocDa,
            currentStatus: dataInit[1].slug,
            content: dataInit[1].title
        },
        {
            data: dataGocReview,
            currentStatus: dataInit[2].slug,
            content: dataInit[2].title
        },
        {
            data: dataDuongChatChoLanDa,
            currentStatus: dataInit[3].slug,
            content: dataInit[3].title
        }
    ];

    const handleClick = (data: any, currentButton: string) => {
        setDataBlogsCurrent(data);
        setCurrentButton(currentButton);
    };

    return (
        <>
            <div className="container mx-auto">
                <Title title="Blog làm đẹp" link="/blogs/tin-tuc" />

                <div className="text-center mb-[20px]">
                    {dataButton.map((item, index) => (
                        <button
                            key={index}
                            onClick={() => handleClick(item.data, item.currentStatus)}
                            className={`text-[16px] hover:text-primary font-[500] px-[25px] py-[2px] ${currentButton === item.currentStatus ? "text-primary" : "text-[#333]"
                                }`}
                        >
                            {item.content}
                        </button>
                    ))}
                </div>

                <div className="container mx-auto grid grid-cols-4 gap-[20px]">
                    {dataBlogsCurrent.length > 0 && dataBlogsCurrent.map((item: any, index: number) => (
                        <BlogItem
                            key={index}
                            image={item.thumbnail[0]}
                            day={item.createdAt}
                            title={item.title}
                            description={item.content}
                            link={`/blogs/detail/${item.slug}`}
                        />
                    ))}
                </div>
                <ButtonSeeAll link="/blogs/tin-tuc" />
            </div>
        </>
    );
}
