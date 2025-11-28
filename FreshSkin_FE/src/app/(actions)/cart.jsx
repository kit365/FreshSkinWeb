export const cartDelete = (index) => {
    return {
        type: "CART_DELETE",
        index: index
    }
}

export const cartChangeQuantity = (event, index) => {
    return {
        type: "CART_CHANGE_QUANTITY",
        event: event,
        index: index
    }
}

export const cartIncreaseQuantity = (event, index) => {
    return {
        type: "CART_INCREASE_QUANTITY",
        event: event,
        index: index
    }
}

export const cartDecreaseQuantity = (event, index) => {
    return {
        type: "CART_DECREASE_QUANTITY",
        event: event,
        index: index
    }
}

export const cartAddNewProduct = (products) => {
    return {
        type: "CART_ADD_NEW_PRODUCT",
        products: products    
    }
}

export const cartTotalPriceVoucher = (totalPriceVoucher, voucherTitle) => {
    return {
        type: "CART_TOTAL_PRICE_VOUCHER",
        totalPriceVoucher: totalPriceVoucher,
        voucherTitle: voucherTitle    
    }
}

export const cartReset = () => {
    return {
        type: "CART_RESET"
    }
}