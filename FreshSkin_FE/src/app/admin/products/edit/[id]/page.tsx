"use client";

import dynamic from 'next/dynamic';
const TinyEditor = dynamic(() => import('../../../../../../TinyEditor'), {
    ssr: false
});
import React, { useContext, useEffect, useState } from 'react';
import { Box, Typography, TextField, FormControl, Button, Paper, RadioGroup, FormControlLabel, Radio, FormGroup, Checkbox, InputLabel, Select, MenuItem, Divider } from '@mui/material';
import { MdDeleteForever } from 'react-icons/md';
import UploadImage from '@/app/components/Upload/UploadImage';
import { useParams } from 'next/navigation';
import SubCategory from '@/app/components/Sub-Category/SubCategory';
import { ProfileAdminContext } from '@/app/admin/layout';
import Alert from '@mui/material/Alert';
interface InputField {
    volume: number;
    price: number;
    stock: number;
}

interface InputField {
    price: number;
    volume: number;
    unit: string
}
export default function EditProductAdminPage() {
    const dataProfile = useContext(ProfileAdminContext);
    const permissions = dataProfile?.permissions;
    const { id } = useParams();
    const [alertMessage, setAlertMessage] = useState<string>("");
    const [alertSeverity, setAlertSeverity] = useState<"success" | "error" | "info" | "warning">("info");
    const [loading, setLoading] = useState(false);


    // Quản lý mặc định
    const [listSkinType, setListSkinType] = useState([]);
    const [description, setDescription] = useState('');
    const [ingredients, setIngredients] = useState('');
    const [usageInstructions, setUsageInstructions] = useState('');
    const [listCategory, setListCategory] = useState([]);
    const [brandCurrent, setBrandCurrent] = useState("");
    const [listBrand, setListBrand] = useState([]);
    const [inputCheckedCategory, setInputCheckedCategory] = useState<number[]>([]);
    const [checkedSkinType, setCheckedSkinType] = useState<number[]>([]);
    const [inputs, setInputs] = useState<InputField[]>([]);

    const [productInfo, setProductInfo] = useState({
        title: "",
        categories: [],
        brandId: {},
        description: "",
        variants: [],
        discountPercent: 0,
        origin: "",
        thumbnail: [],
        ingredients: "",
        usageInstructions: "",
        benefits: "",
        skinIssues: "",
        featured: false,
        skinTypes: [{
            id: 0
        }]
    });
    useEffect(() => {
        const fetchCategories = async () => {
            const response = await fetch('https://freshskinweb.onrender.com/admin/products/category/show');
            const data = await response.json();
            setListCategory(data.data);
        };

        const fetchBrands = async () => {
            const response = await fetch('https://freshskinweb.onrender.com/admin/products/brand/show');
            const data = await response.json();
            setListBrand(data.data);
        };

        const fetchSkintypes = async () => {
            const response = await fetch('https://freshskinweb.onrender.com/admin/skintypes/show');
            const data = await response.json();
            setListSkinType(data.data);
        }

        const fetchProduct = async () => {
            const response = await fetch(`https://freshskinweb.onrender.com/admin/products/${id}`);
            const data = await response.json();
            setProductInfo(data.data);
            setDescription(data.data.description);
            setIngredients(data.data.ingredients);
            setUsageInstructions(data.data.usageInstructions);
            setBrandCurrent(data.data.brand.id.toString());
            const selectedCategories = data.data.category.map((cat: any) => cat.id);
            setInputCheckedCategory(selectedCategories);
            const productVariants = data.data.variants.map((variant: any) => ({
                price: variant.price,
                volume: variant.volume,
                unit: variant.unit,
                stock: variant.stock
            }));
            setInputs(productVariants);
            const skinTypeIds = data.data.skinTypes.map((type: any) => type.id);
            setCheckedSkinType(skinTypeIds);
        };

        fetchCategories();
        fetchBrands();
        fetchProduct();
        fetchSkintypes();
    }, []);

    const handleCheckedChange = (checkedIds: number[]) => {
        setInputCheckedCategory(checkedIds);
    };

    // SkinType
    const handleChangeCheckedSkinType = (event: any) => {
        const id = parseInt(event.target.name);
        setCheckedSkinType((prev) => {
            if (prev.includes(id)) {
                return prev.filter((item) => item !== id);
            } else {
                return [...prev, id];
            }
        });
    };

    /// Variants
    const handleAddInput = () => {
        setInputs([...inputs, { price: 0, volume: 0, unit: "ML", stock: 0 }]);
    };
    const handleRemoveInput = (indexRemove: number) => {
        const newInputs = inputs.filter((_, index) => index !== indexRemove);
        setInputs(newInputs);
    };
    const handleInputChange = (index: number, field: 'volume' | 'price' | 'unit' | 'stock', value: string) => {
        const newInputs = [...inputs];
        if (field === "unit") {
            newInputs[index][field] = value ? value : "ML";
        } else {
            const numericValue = parseFloat(value);
            if (field === "price" && numericValue > 100000000) {
                setAlertMessage("Giá không được vượt quá 100.000.000đ");
                setAlertSeverity("warning");
                setTimeout(() => setAlertMessage(""), 5000);
                return;
            }
            newInputs[index][field] = numericValue ? numericValue : 0;
        }
        setInputs(newInputs);
    };

    // Upload ảnh
    const [images, setImages] = useState<(File)[]>([]);

    const handleImageChange = (newImages: (File)[]) => {
        setImages(newImages);
    };

    const handleRemoveDefaultImage = (index: number) => {
        setProductInfo(prevData => ({
            ...prevData,
            thumbnail: prevData.thumbnail.filter((_, i) => i !== index)
        }));
    };

    const handleChangeBrand = (event: any) => {
        setBrandCurrent(event.target.value);
    };

    const handleChangeFeatured = (event: any) => {
        setProductInfo({ ...productInfo, featured: event.target.value === "true" });
    };

    const handleSubmit = async (event: any) => {
        event.preventDefault();
        setLoading(true);

        const title = event.target.title.value;
        const discountPercent = parseInt(event.target.discount.value);
        const origin = event.target.origin.value;
        const benefits = event.target.benefits.value;
        const skinIssues = event.target.skinIssues.value;
        const featured = event.target.featured.value === "true";

        if (!title) {
            setAlertMessage("Tên sản phẩm không được để trống.");
            setAlertSeverity("error");
            setTimeout(() => setAlertMessage(""), 5000);
            setLoading(false);
            return;
        }

        if (title.length <= 10) {
            setAlertMessage("Tên sản phẩm phải trên 10 ký tự.");
            setAlertSeverity("error");
            setTimeout(() => setAlertMessage(""), 5000);
            setLoading(false);
            return;
        }

        if (inputCheckedCategory.length === 0) {
            setAlertMessage("Danh mục sản phẩm không được để trống.");
            setAlertSeverity("error");
            setTimeout(() => setAlertMessage(""), 5000);
            setLoading(false);
            return;
        }

        if (!brandCurrent) {
            setAlertMessage("Thương hiệu không được để trống.");
            setAlertSeverity("error");
            setTimeout(() => setAlertMessage(""), 5000);
            setLoading(false);
            return;
        }

        if ((images.length + productInfo.thumbnail.length) < 5) {
            setAlertMessage("Phải chọn tối thiểu 5 ảnh cho sản phẩm.");
            setAlertSeverity("error");
            setTimeout(() => setAlertMessage(""), 5000);
            setLoading(false);
            return;
        }

        if (!description) {
            setAlertMessage("Mô tả không được để trống");
            setAlertSeverity("error");
            setTimeout(() => setAlertMessage(""), 5000);
            setLoading(false);
            return;
        }

        for (const input of inputs) {
            if (input.volume <= 0) {
                setAlertMessage("Dung tích / Số lượng không được nhỏ hơn hoặc bằng 0ml/g.");
                setAlertSeverity("error");
                setTimeout(() => setAlertMessage(""), 5000);
                setLoading(false);
                return;
            }
            if (input.volume > 0 && input.price <= 0) {
                setAlertMessage("Giá không được để trống.");
                setAlertSeverity("error");
                setTimeout(() => setAlertMessage(""), 5000);
                setLoading(false);
                return;
            }
            if (input.volume > 10000) {
                setAlertMessage("Dung tích không được lớn hơn 10.000ml/g.");
                setAlertSeverity("error");
                setTimeout(() => setAlertMessage(""), 5000);
                setLoading(false);
                return;
            }
        }

        if (discountPercent >= 100) {
            setAlertMessage("Phần trăm giảm giá không được vượt quá 100%.");
            setAlertSeverity("error");
            setTimeout(() => setAlertMessage(""), 5000);
            setLoading(false);
            return;
        }

        if (checkedSkinType.length === 0) {
            setAlertMessage("Sản phẩm dành cho loại da không được để trống");
            setAlertSeverity("error");
            setTimeout(() => setAlertMessage(""), 5000);
            setLoading(false);
            return;
        }

        if (!benefits) {
            setAlertMessage("Lợi ích sản phẩm không được để trống");
            setAlertSeverity("error");
            setTimeout(() => setAlertMessage(""), 5000);
            setLoading(false);
            return;
        }

        if (!ingredients) {
            setAlertMessage("Thành phần sản phẩm không được để trống");
            setAlertSeverity("error");
            setTimeout(() => setAlertMessage(""), 5000);
            setLoading(false);
            return;
        }

        if (!usageInstructions) {
            setAlertMessage("Hướng dẫn sử dụng sản phẩm không được để trống");
            setAlertSeverity("error");
            setTimeout(() => setAlertMessage(""), 5000);
            setLoading(false);
            return;
        }

        if (!origin) {
            setAlertMessage("Xuất sứ sản phẩm không được để trống");
            setAlertSeverity("error");
            setTimeout(() => setAlertMessage(""), 5000);
            setLoading(false);
            return;
        }

        if (!skinIssues) {
            setAlertMessage("Vấn đề da không được để trống");
            setAlertSeverity("error");
            setTimeout(() => setAlertMessage(""), 5000);
            setLoading(false);
            return;
        }

        const formData = new FormData();

        const request = {
            categoryId: inputCheckedCategory,
            brandId: brandCurrent,
            title: title,
            description: description,
            image: productInfo.thumbnail,
            variants: inputs.map(input => ({
                price: Number(input.price),
                volume: input.volume ? Number(input.volume) : 0,
                unit: input.unit,
                stock: input.stock ? Number(input.stock) : 0,
            })),
            skinTypeId: checkedSkinType,
            discountPercent: discountPercent,
            origin: origin,
            ingredients: ingredients,
            usageInstructions: usageInstructions,
            benefits: benefits,
            skinIssues: skinIssues,
            featured: featured,
        };

        formData.append("request", JSON.stringify(request));

        images.forEach((image) => formData.append("newImg", image));

        const response = await fetch(`https://freshskinweb.onrender.com/admin/products/edit/${id}`, {
            method: "PATCH",
            body: formData,
        });

        const dataResponse = await response.json();

        if (dataResponse.code === 200) {
            setAlertMessage(dataResponse.message);
            setAlertSeverity("success");
            setTimeout(() => location.reload(), 2000);
        } else {
            setAlertMessage(dataResponse.message);
            setAlertSeverity("error");
            setLoading(false);
            setTimeout(() => {
                setAlertMessage(""); 
            }, 2000);
        }
    };

    return (
        <>
            {alertMessage && (
                <Alert severity={alertSeverity} sx={{ position: "fixed", width: "600px", height: "60px", right: "5%", top: "5%", fontSize: "16px", zIndex: "999999" }}>
                    {alertMessage}
                </Alert>
            )}
            <Box sx={{ padding: 3, backgroundColor: '#e3f2fd' }}>
                <Typography variant="h4" gutterBottom>
                    Trang chỉnh sửa sản phẩm
                </Typography>

                {permissions?.includes("products_edit") && (
                    <Paper elevation={3} sx={{ padding: 3, marginBottom: 2 }}>
                        <form onSubmit={handleSubmit}>
                            <TextField
                                label="Tên sản phẩm"
                                name='title'
                                variant="outlined"
                                fullWidth
                                sx={{ marginBottom: 3 }}
                                value={productInfo.title}
                                onChange={(e) => setProductInfo({ ...productInfo, title: e.target.value })}
                            />
                            <div className='mb-6 sub-menu'>
                                <Typography className='border border-solid border-[#BFBFBF] rounded-[5px] p-[10px]'>Chọn danh mục sản phẩm</Typography>
                                <SubCategory
                                    items={listCategory}
                                    onCheckedChange={handleCheckedChange}
                                    defaultCheckedIds={inputCheckedCategory}
                                />
                            </div>
                            <FormControl fullWidth variant="outlined" sx={{ marginBottom: 3 }}>
                                <InputLabel shrink={true}>-- Chọn thương hiệu --</InputLabel>
                                <Select
                                    value={brandCurrent}
                                    onChange={handleChangeBrand}
                                    label=" Chọn thương hiệu --"
                                    displayEmpty
                                >
                                    <MenuItem value="">
                                        -- Chọn thương hiệu --
                                    </MenuItem>
                                    {listBrand.map((item: any, index: number) => (
                                        <MenuItem key={index} value={item.id}>{item.title}</MenuItem>
                                    ))}
                                </Select>
                            </FormControl>
                            <FormControl fullWidth sx={{ marginBottom: 3 }}>
                                <RadioGroup
                                    value={productInfo.featured.toString()}
                                    name="featured"
                                    onChange={handleChangeFeatured}
                                    row
                                >
                                    <FormControlLabel value="true" control={<Radio />} label="Nổi bật" />
                                    <FormControlLabel value="false" control={<Radio />} label="Không nổi bật" />
                                </RadioGroup>
                            </FormControl>
                            <h4>Mô tả</h4>
                            <TinyEditor value={description} onEditorChange={(description: string) => setDescription(description)} />
                            <UploadImage
                                label="Chỉnh sửa hình ảnh"
                                id="upload-images"
                                name="images"
                                defaultImages={productInfo.thumbnail}
                                onImageChange={handleImageChange}
                                onRemoveDefaultImage={handleRemoveDefaultImage}
                            />
                            <Box sx={{ marginTop: "20px", marginBottom: "20px" }}>
                                {inputs.map((input, index: number) => (
                                    <Box key={index} sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                                        <TextField
                                            label="Dung tích (ML)"
                                            type='number'
                                            variant="outlined"
                                            size="small"
                                            value={input.volume}
                                            onChange={(e) => handleInputChange(index, 'volume', e.target.value)}
                                            sx={{ mr: 1, width: "150px" }}
                                        />
                                        <Typography variant="h6">:</Typography>
                                        <TextField
                                            label="Giá"
                                            type='number'
                                            variant="outlined"
                                            size="small"
                                            value={input.price}
                                            onChange={(e) => handleInputChange(index, 'price', e.target.value)}
                                            sx={{ ml: 1, width: "150px" }}
                                        />
                                        <TextField
                                            label="Số lượng"
                                            type='number'
                                            variant="outlined"
                                            size="small"
                                            value={input.stock}
                                            onChange={(e) => handleInputChange(index, 'stock', e.target.value)}
                                            sx={{ ml: 1, width: "150px" }}
                                        />
                                        <Select
                                            value={input.unit}
                                            onChange={(e) => handleInputChange(index, "unit", e.target.value)}
                                            size="small"
                                            sx={{ ml: 1, width: "150px" }}
                                        >
                                            <MenuItem value="ML">ML</MenuItem>
                                            <MenuItem value="G">G</MenuItem>
                                        </Select>
                                        <MdDeleteForever onClick={() => handleRemoveInput(index)} className='text-red-400 text-[25px] ml-[10px] cursor-pointer' />
                                    </Box>
                                ))}
                                <Button variant="contained" onClick={handleAddInput}>
                                    Thêm
                                </Button>
                            </Box>
                            <TextField
                                label="% Giảm giá"
                                name='discount'
                                variant="outlined"
                                fullWidth
                                type="number"
                                sx={{ marginBottom: 2 }}
                                value={productInfo.discountPercent}
                                onChange={(e) => setProductInfo({ ...productInfo, discountPercent: parseFloat(e.target.value) })}
                            />
                            <FormGroup row>
                                {listSkinType.map((item: any, index: number) => (
                                    <FormControlLabel
                                        key={index}
                                        control={<Checkbox checked={checkedSkinType.includes(item.id)} onChange={handleChangeCheckedSkinType} name={item.id.toString()} />}
                                        label={item.type}
                                    />
                                ))}
                            </FormGroup>
                            <TextField
                                label="Xuất sứ"
                                name='origin'
                                variant="outlined"
                                fullWidth
                                sx={{ marginBottom: 2, marginTop: 2 }}
                                value={productInfo.origin}
                                onChange={(e) => setProductInfo({ ...productInfo, origin: e.target.value })}
                            />
                            <h4>Thành phần</h4>
                            <TinyEditor value={ingredients} onEditorChange={(ingredients: string) => setIngredients(ingredients)} />
                            <Divider sx={{ marginBottom: 2 }} />
                            <h4>Hướng dẫn sử dụng</h4>
                            <TinyEditor value={usageInstructions} onEditorChange={(usageInstructions: string) => setUsageInstructions(usageInstructions)} />
                            <TextField
                                label="Lợi ích"
                                name='benefits'
                                variant="outlined"
                                fullWidth
                                sx={{ marginBottom: 2, marginTop: 2 }}
                                value={productInfo.benefits}
                                onChange={(e) => setProductInfo({ ...productInfo, benefits: e.target.value })}
                            />
                            <TextField
                                label="Vấn đề da"
                                name='skinIssues'
                                variant="outlined"
                                fullWidth
                                sx={{ marginBottom: 2, marginTop: 2 }}
                                value={productInfo.skinIssues}
                                onChange={(e) => setProductInfo({ ...productInfo, skinIssues: e.target.value })}
                            />
                            <Button type='submit' variant="contained" color="primary" sx={{ width: '100%' }} disabled={loading}>
                                {loading ? "Đang cập nhật sản phẩm..." : "Cập nhật sản phẩm"}
                            </Button>
                        </form>
                    </Paper>
                )}
            </Box>
        </>
    );
}
