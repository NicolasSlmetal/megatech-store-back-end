package com.megatech.store.controller;

import com.megatech.store.dtos.address.ViaCepAddressDTO;
import com.megatech.store.service.ApiRequestService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class AddressController {

    private final ApiRequestService apiRequestService;

    public AddressController(ApiRequestService apiRequestService) {
        this.apiRequestService = apiRequestService;
    }

    @GetMapping("/addresses")
    public ViaCepAddressDTO getAddressByZipCode(@RequestParam("cep") String zipCode) {
        return apiRequestService
                .getApiResponse("https://viacep.com.br/ws/" + zipCode + "/json/", ViaCepAddressDTO.class);
    }

}
