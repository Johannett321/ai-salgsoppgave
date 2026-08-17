export const formatAsNOK = (amount?: number) => {
    if (!amount) {
        return amount
    }
    return amount
        .toString()
        .replace(/\B(?=(\d{3})+(?!\d))/g, ' ') + ' kr';
}
