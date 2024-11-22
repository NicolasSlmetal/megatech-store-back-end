package com.megatech.store.model;

import com.megatech.store.domain.Customer;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.util.List;

@Entity(name = "customer")
@Table(name ="tb_customer")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomerModel implements Model {

    @Id
    @Column(name = "cst_id")
    private Long id;

    @Column(name = "cst_name")
    private String name;

    @Column(name = "cst_cpf")
    private String cpf;

    @CreationTimestamp
    @Column(name = "cst_registration_date", updatable = false)
    private LocalDate registrationDate;

    @MapsId
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "cst_id", referencedColumnName = "user_id", updatable = false)
    private UserModel user;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<AddressModel> addresses;

}
