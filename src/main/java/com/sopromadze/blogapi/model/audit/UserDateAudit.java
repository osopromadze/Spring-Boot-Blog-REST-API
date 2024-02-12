package com.sopromadze.blogapi.model.audit;

import jakarta.persistence.Column;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

//@EqualsAndHashCode(callSuper = true)
//@MappedSuperclass
@Data
//@JsonIgnoreProperties(
//        value = {"createdBY", "updatedBy"},
//        allowGetters = true
//)
public abstract class UserDateAudit extends DateAudit {
    private static final long serialVersionUID = 1L;

    @CreatedBy
    @Column(nullable = false, updatable = false)
    private Long createdBy;

    @LastModifiedBy
    @Column(updatable = false)
    private Long updatedBy;
}
