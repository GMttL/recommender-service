package com.gmitit01.recommenderservice.entity.DTO;


import lombok.Data;

import java.util.List;

@Data
public class PagedResponseDTO<T> {

    private List<T> content;
    private int totalPages;
    private long totalElements;
    private int pageNumber;
    private int pageSize;

}
