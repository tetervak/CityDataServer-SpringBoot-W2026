package ca.tetervak.citydata.errors;

public record ApiError(
        int status,
        String error,
        String message
){}
