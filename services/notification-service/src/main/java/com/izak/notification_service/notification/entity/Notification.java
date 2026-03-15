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
@Table(name = "notification")
public class Notification {
  String topic;
  String email;
  String message;
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;
  @CreationTimestamp
  private Instant createdAt;
}
