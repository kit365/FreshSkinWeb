export const provinceChoosen = (provinceChoosen: boolean) => {
    return {
        type: "PROVINCE_CHOOSEN",
        provinceChoosen: provinceChoosen
    }
}

export const sumShip = (feeShip: number) => {
    return {
        type: "FEE_SHIP",
        feeShip: feeShip,
    }
}
