"use client"

import { Typography } from "@mui/material";
import Link from "next/link";
import { GrNext } from "react-icons/gr";

export default function SubCategoryHeader(props: any) {
    const { items } = props;

    return (
        <>
            {items.length > 0 && (
                <ul style={{ padding: 0 }}>
                    {items.map((item: any, index: number) => (
                        <li key={index} className="py-[10px] text-[18px]">
                            <Typography className="relative" variant="body1" style={{ cursor: 'pointer' }}>
                                <Link href={`/product-category/${item.slug}`}>
                                    {item.title}
                                </Link>
                                {item.child && item.child.length > 0 && (
                                    <GrNext className="absolute top-[20%] right-0" />
                                )}
                            </Typography>
                            {item.child && item.child.length > 0 && (
                                <SubCategoryHeader items={item.child} />
                            )}
                        </li>
                    ))}
                </ul>
            )}
        </>
    );
}