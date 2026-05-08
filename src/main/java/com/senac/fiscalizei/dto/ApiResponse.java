package com.senac.fiscalizei.dto;

public record ApiResponse(

        String status,
        String detail
) {

    public static ApiResponse success(String detail) {
        return new ApiResponse("success", detail);
    }

    public static ApiResponse error(String detail) {
        return new ApiResponse("error", detail);
    }
}
