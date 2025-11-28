"use client"

import { Checkbox, Typography } from "@mui/material";
import { useEffect, useState } from "react";

export default function SubCategory(props: any) {
    const { items, onCheckedChange, defaultCheckedIds } = props; 
    const [inputChecked, setInputChecked] = useState<number[]>([]);

    useEffect(() => {
        setInputChecked(defaultCheckedIds || []);
    }, [defaultCheckedIds]);

    const handleInputChecked = (event: any, id: number) => {
        const newChecked = event.target.checked
            ? [...inputChecked, id] 
            : inputChecked.filter(checkedId => checkedId !== id); 

        setInputChecked(newChecked); 
        onCheckedChange(newChecked); 
    };

    return (
        <>
            {items.length > 0 && (
                <ul style={{ padding: 0 }}>
                    {items.map((item: any, index: number) => (
                        <li key={index} className="py-[10px] text-[18px]">
                            <Typography variant="body1" style={{ cursor: 'pointer' }}>
                                {item.title} 
                                {item.child.length <= 0 && (
                                    <Checkbox 
                                        checked={inputChecked?.includes(item.id)} 
                                        onChange={(event) => handleInputChecked(event, item.id)} 
                                    />
                                )}
                            </Typography>
                            {item.child && item.child.length > 0 && (
                                <SubCategory 
                                    items={item.child} 
                                    onCheckedChange={onCheckedChange} 
                                    defaultCheckedIds={inputChecked} 
                                />
                            )}
                        </li>
                    ))}
                </ul>
            )}
        </>
    );
}