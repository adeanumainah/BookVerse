package com.perpustakaan.dean.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponUmum<T> {
    private boolean berhasil;
    private String pesan;
    private T data;
    
}
