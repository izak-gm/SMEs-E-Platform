package com.izak.auth_service.address.entity;

import com.izak.auth_service.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "address")
public class Address {
  @Id
  @SequenceGenerator(
        name = "address_sequence",
        sequenceName = "address_sequence",
        allocationSize = 1
  )
  @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "address_sequence"
  )
  private Integer id;

  @OneToOne(mappedBy = "address", cascade = CascadeType.ALL)
  private User user;
  private String town;
  private String city;
  private String county;
  private String postalCode;
}
