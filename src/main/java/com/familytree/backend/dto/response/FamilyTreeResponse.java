package com.familytree.backend.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class FamilyTreeResponse {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
}
