package com.interswitch.bookstore.utils.api;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

@Data
public class PaginationBody<T> {
    private PaginationMeta meta = new PaginationMeta();
    private List<T> data = new ArrayList<>();

    public PaginationBody(){}

    public PaginationBody(Page<T> result, int pageSize){
        this.meta.setPageSize((pageSize == 0) ? 50 : pageSize);

        if (result != null && !result.getContent().isEmpty()) {
            this.meta.setTotal(result.getTotalElements());

            int pageNum = result.getNumber();

            this.meta.setPageNumber(pageNum);
            this.meta.setTotalPages(result.getTotalPages());
            setData(result.getContent());
        }
    }
}
