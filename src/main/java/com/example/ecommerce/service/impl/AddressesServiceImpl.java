package com.example.ecommerce.service.impl;

import com.example.ecommerce.io.entity.AddressEntity;
import com.example.ecommerce.io.entity.UserEntity;
import com.example.ecommerce.io.repositories.AddressRepository;
import com.example.ecommerce.io.repositories.UserRepository;
import com.example.ecommerce.service.AddressesService;
import com.example.ecommerce.shared.dto.AddressDTO;
import com.example.ecommerce.ui.model.response.AddressesRest;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Service
public class AddressesServiceImpl implements AddressesService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    AddressRepository addressRepository;

    @Override
    public List<AddressDTO> getAddresses(String userId) {

        List<AddressDTO> returnValue=null;

        UserEntity userEntity=userRepository.findByUserId(userId);

        List<AddressEntity> addresses=addressRepository.findAllByUserDetails(userEntity);

        ModelMapper modelMapper=new ModelMapper();
        Type listType = new TypeToken<List<AddressDTO>>() {}.getType();
        returnValue = modelMapper.map(addresses, listType);

        return returnValue;
    }

    @Override
    public AddressDTO getAddress(String addressId) {

        AddressDTO returnValue=null;
        AddressEntity addressEntity=addressRepository.findByAddressId(addressId);

        ModelMapper modelMapper=new ModelMapper();
        returnValue=modelMapper.map(addressEntity,AddressDTO.class);

        return returnValue;

    }
}
