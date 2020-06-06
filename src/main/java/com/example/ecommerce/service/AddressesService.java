package com.example.ecommerce.service;

import com.example.ecommerce.shared.dto.AddressDTO;
import com.example.ecommerce.ui.model.response.AddressesRest;

import java.util.List;

public interface AddressesService {

    List<AddressDTO> getAddresses(String userId);
    AddressDTO getAddress(String addressId);
}
