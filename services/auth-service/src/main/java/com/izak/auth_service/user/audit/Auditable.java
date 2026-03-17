package com.izak.auth_service.user.audit;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Auditable {
  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  protected Date createdAt;

  @LastModifiedDate
  @Column(name = "updated_at")
  protected Date updatedAt;
}
