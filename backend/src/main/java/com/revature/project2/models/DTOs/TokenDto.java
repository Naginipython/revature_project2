package com.revature.project2.models.DTOs;

public record TokenDto(String token){
    @Override
    public String token() {
        return token;
    }
}
