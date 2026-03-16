package com.izak.notification_service.notification.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "notifications") // plural to avoid reserved keyword issues
public class Notification {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false)
  private String topic;

  @Column(nullable = false)
  private String email;

  @Column(nullable = false, columnDefinition = "TEXT") // in case messages are long
  private String message;

  @CreationTimestamp
  @Column(nullable = false, updatable = false)
  private Instant createdAt;
}
