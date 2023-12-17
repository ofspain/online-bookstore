package com.interswitch.bookstore.utils.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PaginationMeta {
    @JsonProperty("total_records")
    private Long total = 0L;

    @JsonProperty("total_pages")
    private int totalPages = 1;

    @JsonProperty("page_size")
    private int pageSize;

    @JsonProperty("page_number")
    private int pageNumber = 1;
}
