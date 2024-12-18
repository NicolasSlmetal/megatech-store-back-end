package com.megatech.store.exceptions;

public enum ErrorType {
    INVALID_REQUEST,
    INVALID_ID,
    CUSTOMER_NOT_FOUND,
    PRODUCT_NOT_FOUND,
    UNKNOWN_EXISTING_DATA_ERROR,
    PRODUCT_ALREADY_EXISTS,
    IMAGE_ALREADY_EXISTS,
    INVALID_ADDRESS_DATA,
    INVALID_ADDRESS_STREET,
    INVALID_ADDRESS_NUMBER,
    INVALID_ADDRESS_CITY,
    INVALID_ADDRESS_STATE,
    INVALID_ADDRESS_ZIP,
    INVALID_CUSTOMER_NAME,
    INVALID_CUSTOMER_CPF,
    INVALID_DATE_PARAMETER,
    INVALID_NULL_ATTRIBUTE,
    INVALID_PRODUCT_IMAGE,
    INVALID_PRODUCT_PRICE,
    INVALID_PRODUCT_MANUFACTURER,
    INVALID_PRODUCT_NAME,
    INVALID_PRODUCT_STOCK_QUANTITY,
    INVALID_PURCHASE_QUANTITY,
    INVALID_PURCHASE_CUSTOMER,
    INVALID_PURCHASE_MAPPING,
    INVALID_USER_EMAIL,
    INVALID_USER_PASSWORD,
    INVALID_LOGIN,
    INVALID_USER_ROLE,
    ACCESS_TOKEN_EXPIRED_OR_DENIED,
}
