package com.product_service.controller.dto.request;

import com.product_service.entity.enumm.Status;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Set;

@Data
public class BulkCategoryStatusUpdateRequest {

    @NotEmpty
    private Set<Long> categoryIds;

    @NotNull
    private Status status;

}
