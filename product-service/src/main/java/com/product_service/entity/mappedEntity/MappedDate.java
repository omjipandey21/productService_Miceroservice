package com.product_service.entity.mappedEntity;

import java.time.Instant;

public interface MappedDate {

    Instant getCreatedAt();
    Instant getUpdatedAt();

    void setCreatedAt(Instant createdAt);
    void setUpdatedAt(Instant updatedAt);

    default void initializeTimestamp(){
        Instant now = Instant.now();
        setCreatedAt(now);
        setUpdatedAt(now);
    }

    default void updateTimestamp(){
        setUpdatedAt(Instant.now());
    }

}
