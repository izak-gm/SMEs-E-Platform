package com.izak.auth_service.user.entity;

import com.izak.auth_service.address.entity.Address;
import com.izak.auth_service.user.audit.Auditable;
import com.izak.auth_service.user.enums.Auth;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "_user")
public class User extends Auditable implements UserDetails {
  @Id
  @GeneratedValue
  @Column(columnDefinition = "uuid", updatable = false, nullable = false)
  private UUID id;
  private String firstName;
  private String lastName;
  private String email;
  private String phoneNumber;
  private String gender;
  private LocalDate dob;
  private String password;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "address_id", referencedColumnName = "id")
  private Address address;

  @Enumerated(EnumType.STRING)
  @Column(name = "auth")
  private Auth auth;

  private boolean isEnabled = true;
  private boolean isAccountNonLocked = true;
  private boolean isCredentialsNonExpired = true;
  private boolean isAccountNonExpired = true;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority("ROLE_" + auth.name()));
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public String getPassword() {
    return password;
  }


  @Override
  public boolean isAccountNonExpired() {
    return this.isAccountNonExpired;
  }

  @Override
  public boolean isAccountNonLocked() {
    return this.isAccountNonLocked;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return this.isCredentialsNonExpired;
  }

  @Override
  public boolean isEnabled() {
    return this.isEnabled;
  }

}
