"use client";

import { useState } from "react";

export default function UploadImage(props: {
    label: string;
    id: string;
    name: string;
    onImageChange: (images: File[]) => void;
    defaultImages?: string[];
    onRemoveDefaultImage?: (index: number) => void;
}) {
    const { label = "", id = "", name = "", onImageChange, defaultImages = [], onRemoveDefaultImage } = props;

    const [imagePreviews, setImagePreviews] = useState<File[]>([]);

    const handleChange = (event: any) => {
        const newFiles = Array.from(event.target.files) as File[];
        const updatedFiles = [...imagePreviews, ...newFiles];
        setImagePreviews(updatedFiles);
        onImageChange(updatedFiles);
    };

    const handleClickDeleteFile = (index: number) => {
        if (onRemoveDefaultImage) {
            onRemoveDefaultImage(index);
        }
    };

    const handleClickDelete = (index: number) => {
        const updatedFiles = imagePreviews.filter((_, i) => i !== index);
        setImagePreviews(updatedFiles);
        onImageChange(updatedFiles);
    };

    return (
        <div className="mb-[10px]">
            {label && (
                <label htmlFor={id} className="text-[16px] text-[#333] block mb-[5px]">
                    {label}
                </label>
            )}
            <label
                htmlFor={id}
                className="flex items-center justify-center h-[60px] border border-dashed border-[#aaaaaa] rounded-[5px] text-[#aaaaaa] text-[40px] cursor-pointer hover:text-blue-400 hover:border-blue-400"
            >
                +
            </label>
            <input
                type="file"
                name={name}
                id={id}
                accept="image/*"
                className="hidden"
                multiple
                onChange={handleChange}
            />

            {(defaultImages.length > 0 || imagePreviews.length > 0) && (
                <div className="mt-[10px] flex gap-[10px] flex-wrap">
                    {defaultImages.map((image, index) => (
                        <div key={`default-${image}-${index}`} className="relative">
                            <img
                                src={image}
                                className="w-[200px] h-[200px] object-cover border border-solid border-[#aaaaaa] rounded-[5px] p-[10px]"
                            />
                            <button
                                className="absolute right-2 top-1 text-[20px] bg-red-500 text-white rounded-full px-2"
                                onClick={() => handleClickDeleteFile(index)}
                            >
                                X
                            </button>
                        </div>
                    ))}

                    {imagePreviews.map((image, index) => (
                        <div key={`preview-${index}`} className="relative">
                            <img
                                src={URL.createObjectURL(image)}
                                className="w-[200px] h-[200px] object-cover border border-solid border-[#aaaaaa] rounded-[5px] p-[10px]"
                            />
                            <button
                                className="absolute right-2 top-1 text-[20px] bg-red-500 text-white rounded-full px-2"
                                onClick={() => handleClickDelete(index)}
                            >
                                X
                            </button>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
}