package com.megatech.store.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "tb_address")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddressModel implements Model {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "add_id")
    private Long id;

    @Column(name = "add_street")
    private String street;

    @Column(name = "add_number")
    private Integer number;

    @Column(name = "add_city")
    private String city;

    @Column(name = "add_state")
    private String state;

    @Column(name = "add_zipcode")
    private String zipcode;

    @ManyToOne
    @JoinColumn(name = "add_customer_id", referencedColumnName = "cst_id")
    private CustomerModel customer;

}
