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
public class CustomerModel {

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

    public CustomerModel(Customer customer) {
        setName(customer.getName());
        setCpf(customer.getCpf());
        setAddresses(customer.getAddress().stream().map(AddressModel::new).toList());
        getAddresses().forEach(addressModel -> addressModel.setCustomer(this));
        if (customer.getRegistrationDate() != null) {
            setRegistrationDate(customer.getRegistrationDate());
        }
        setUser(new UserModel(customer.getUser()));
        setId(getUser().getId());
    }

}
