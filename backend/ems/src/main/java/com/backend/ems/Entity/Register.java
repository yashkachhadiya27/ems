package com.backend.ems.Entity;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.backend.ems.Enums.UserStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Table(name = "register")
@Data
public class Register implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "first_name")
    @NotEmpty(message = "First name Should not be empty and Should not be null.")
    @Size(min = 3, max = 30, message = "Invalid First Name: Must be of 3 - 30 characters.")
    private String fname;

    @Column(name = "middle_name")
    @NotEmpty(message = "Middle name Should not be empty and Should not be null")
    @Size(min = 3, max = 30, message = "Invalid Middle Name: Must be of 3 - 30 characters.")
    private String mname;

    @Column(name = "last_name")
    @NotEmpty(message = "Last name Should not be empty and Should not be null.")
    @Size(min = 3, max = 30, message = "Invalid Last Name: Must be of 3 - 30 characters.")
    private String lname;

    @Email(message = "Invalid Email.")
    private String email;

    @NotNull(message = "Gender should not be null.")
    private String gender;

    @NotEmpty(message = "Department Should not be empty and Should not be null.")
    private String department;

    @NotNull(message = "Joining date should not be null.")
    @PastOrPresent(message = "Joining date should not contain future date.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateOfJoining;

    @NotNull(message = "Birth date should not be null.")
    @Past(message = "Birth date value should not contain future date.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateOfBirth;

    @NotEmpty(message = "Password Should not be empty and Should not be null.")
    private String password;

    @NotEmpty(message = "Phone number Should not be empty and Should not be null.")
    @Pattern(regexp = "^\\d{10}$", message = "Invalid phone number")
    private String phone;

    @Column(columnDefinition = "TEXT[]")
    private String[] skills;

    @NotNull(message = "image name should not be empty.")
    private String image;

    @NotNull(message = "Role field should not be empty.")
    private String role;

    // Uni Directional One to One Mapping between Register and Address
    // By default fetch type is Eager in one to one relatiobship
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "address_id")
    private Address address;

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<Message> messagesSent;

    @Enumerated(EnumType.STRING)
    private UserStatus status = UserStatus.OFFLINE;

    // @OneToMany(mappedBy="register", cascade=CascadeType.ALL,
    // fetch=FetchType.EAGER)
    // private List<Attendance> attendance;

    // Uni Directional One to Many Mapping between Register and Experience
    // By default fetch type is lazy in one to many relationship
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Experience> experience;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "user_projects", joinColumns = @JoinColumn(name = "register_id"), inverseJoinColumns = @JoinColumn(name = "project_id"))
    @JsonIgnore
    private List<Project> projects;

    @JsonIgnore
    @OneToOne(mappedBy = "register", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private RefreshToken refreshToken;

    @OneToMany(mappedBy = "register", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Leave> leaveRequests;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getUsername() {
        return email;
    }

    public Register(int senderId) {
        this.id = senderId;
    }

    public Register() {
    }
}
