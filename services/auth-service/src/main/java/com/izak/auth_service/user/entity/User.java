package com.izak.auth_service.user.entity;

import com.izak.auth_service.user.enums.Auth;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "_user")
public class User implements UserDetails {
  @Id
  @SequenceGenerator(
        name = "user_sequence",
        sequenceName = "user_sequence",
        allocationSize = 1
  )
  @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "user_sequence"
  )
  private Integer id;
  private String firstName;
  private String lastName;
  private String email;
  private String phoneNumber;
  private String gender;
  private Date dob;
  private String password;

  @Enumerated(EnumType.STRING)
  private Auth auth;

  private boolean isEnabled=true;
  private boolean isAccountNonLocked=true;
  private boolean isCredentialsNonExpired=true;
  private boolean isAccountNonExpired=true;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities(){
    return List.of(new SimpleGrantedAuthority("ROLE_"+ auth.name()));
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
    return UserDetails.super.isAccountNonExpired();
  }

  @Override
  public boolean isAccountNonLocked() {
    return UserDetails.super.isAccountNonLocked();
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return UserDetails.super.isCredentialsNonExpired();
  }

  @Override
  public boolean isEnabled() {
    return UserDetails.super.isEnabled();
  }
}
