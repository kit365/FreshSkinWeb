"use client";

import Cookies from "js-cookie";

interface CartItem {
    image: string;
    title: string;
    price: number;
    link: string;
    variantId: number;
    volume: number;
    unit: string;
    quantity: number;
    stock: number;
}

let dataInit: any = undefined;

const tokenUser = Cookies.get("tokenUser");
if (!tokenUser) {
    const getCartFromCookie = () => {
        if (typeof window === "undefined") return { products: [], totalPriceInit: 0, totalQuantityInit: 0, priceVoucher: 0, voucherTitle: "" };

        const cartCookie = Cookies.get("cart");
        return cartCookie ? JSON.parse(cartCookie) : { products: [], totalPriceInit: 0, totalQuantityInit: 0, priceVoucher: 0, voucherTitle: "" };
    };

    dataInit = getCartFromCookie();
}
else {
    const productsInit: CartItem[] = [];
    const totalPriceInit: number = productsInit.reduce((sum: number, item: CartItem) => {
        return sum + item.price * item.quantity
    }, 0);
    const totalQuantityInit: number = productsInit.reduce((sum: number, item: CartItem) => {
        return sum + item.quantity
    }, 0);

    dataInit = {
        products: productsInit,
        totalPriceInit: totalPriceInit,
        totalQuantityInit: totalQuantityInit,
        priceVoucher: 0,
        voucherTitle: ""
    }
}

const saveCartToCookie = (cart: { products: CartItem[]; totalPriceInit: number; totalQuantityInit: number }) => {
    Cookies.set("cart", JSON.stringify(cart), { expires: 100, path: "/" });
};


export const cartReducer = (state = dataInit, action: any) => {
    let newState;

    switch (action.type) {
        case "CART_CHANGE_QUANTITY": {
            const updatedDataChangeQuantity = [...state.products];
            const newQuantity = parseInt(action.event.target.value);

            if (newQuantity >= 0) {
                updatedDataChangeQuantity[action.index].quantity = newQuantity;

                const priceTotalUpdatedChangeQuantity = updatedDataChangeQuantity.reduce(
                    (sum: number, item: CartItem) => sum + item.price * item.quantity,
                    0
                );

                const quantityTotalUpdatedChangeQuantity = updatedDataChangeQuantity.reduce(
                    (sum: number, item: CartItem) => sum + item.quantity,
                    0
                );

                newState = {
                    ...state,
                    products: updatedDataChangeQuantity,
                    totalPriceInit: priceTotalUpdatedChangeQuantity,
                    totalQuantityInit: quantityTotalUpdatedChangeQuantity,
                };
            }
            break;
        }

        case "CART_INCREASE_QUANTITY": {
            action.event.preventDefault();

            const updatedDataIncreaseQuantity = [...state.products];
            updatedDataIncreaseQuantity[action.index].quantity += 1;

            const priceTotalUpdatedIncreaseQuantity =
                state.totalPriceInit + updatedDataIncreaseQuantity[action.index].price;
            const quantityTotalUpdatedIncreaseQuantity = state.totalQuantityInit + 1;

            newState = {
                ...state,
                products: updatedDataIncreaseQuantity,
                totalPriceInit: priceTotalUpdatedIncreaseQuantity,
                totalQuantityInit: quantityTotalUpdatedIncreaseQuantity,
            };
            break;
        }

        case "CART_DECREASE_QUANTITY": {
            action.event.preventDefault();

            const updatedDataDecreaseQuantity = [...state.products];
            if (updatedDataDecreaseQuantity[action.index].quantity - 1 >= 0) {
                updatedDataDecreaseQuantity[action.index].quantity -= 1;

                const priceTotalUpdatedDecreaseQuantity =
                    state.totalPriceInit - updatedDataDecreaseQuantity[action.index].price;
                const quantityTotalUpdatedDecreaseQuantity = state.totalQuantityInit - 1;

                newState = {
                    ...state,
                    products: updatedDataDecreaseQuantity,
                    totalPriceInit: priceTotalUpdatedDecreaseQuantity,
                    totalQuantityInit: quantityTotalUpdatedDecreaseQuantity,
                };
            }
            break;
        }

        case "CART_DELETE": {
            const updatedDataDelete = state.products.filter((_: CartItem, index: number) => index !== action.index);

            const priceTotalUpdatedDelete = updatedDataDelete.reduce(
                (sum: number, item: CartItem) => sum + item.price * item.quantity,
                0
            );

            const quantityTotalUpdatedDelete = updatedDataDelete.reduce(
                (sum: number, item: CartItem) => sum + item.quantity,
                0
            );

            newState = {
                ...state,
                products: updatedDataDelete,
                totalPriceInit: priceTotalUpdatedDelete,
                totalQuantityInit: quantityTotalUpdatedDelete,
            };
            break;
        }

        case "CART_ADD_NEW_PRODUCT": {
            const updatedDataNewProduct = action.products;

            const priceTotalUpdatedAddNewProduct = updatedDataNewProduct.reduce(
                (sum: number, item: CartItem) => sum + item.price * item.quantity,
                0
            );

            const quantityTotalUpdatedAddNewProduct = updatedDataNewProduct.reduce(
                (sum: number, item: CartItem) => sum + item.quantity,
                0
            );

            newState = {
                ...state,
                products: updatedDataNewProduct,
                totalPriceInit: priceTotalUpdatedAddNewProduct,
                totalQuantityInit: quantityTotalUpdatedAddNewProduct,
            };
            break;
        }

        case "CART_TOTAL_PRICE_VOUCHER": {
            if (action.totalPriceVoucher >= 0) { 
                newState = {
                    ...state,
                    priceVoucher: action.totalPriceVoucher,
                    voucherTitle: action.voucherTitle
                };
            }
            break;
        }


        case "CART_RESET":
            newState = {
                products: [],
                totalPriceInit: 0,
                totalQuantityInit: 0,
            };
            break;

        default:
            return state;
    }

    if (newState) {
        saveCartToCookie(newState);
        return newState;
    }

    return state;
};
