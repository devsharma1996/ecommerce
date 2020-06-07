package com.example.ecommerce.service.impl;

import com.example.ecommerce.exceptions.UserServiceException;
import com.example.ecommerce.io.repositories.UserRepository;
import com.example.ecommerce.io.entity.UserEntity;
import com.example.ecommerce.service.UserService;
import com.example.ecommerce.shared.EmailSender;
import com.example.ecommerce.shared.Utils;
import com.example.ecommerce.shared.dto.AddressDTO;
import com.example.ecommerce.shared.dto.UserDto;
import com.example.ecommerce.ui.model.response.ErrorMessages;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    EmailSender emailSender;

    @Autowired
    Utils utils;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDto createUser(UserDto user) {

        if(userRepository.findByEmail(user.getEmail())!=null) throw new RuntimeException("Record already exists");

        for(int i=0; i < user.getAddresses().size(); i++){
            AddressDTO address=user.getAddresses().get(i);
            address.setUserDetails(user);
            address.setAddressId(utils.generateAddressId(30));
            user.getAddresses().set(i,address);
        }

        //BeanUtils.copyProperties(user,userEntity);
        ModelMapper modelMapper=new ModelMapper();
        UserEntity userEntity=modelMapper.map(user,UserEntity.class);


        String publicUserId=utils.generateUserId(30);
        userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userEntity.setUserId(publicUserId);
        userEntity.setEmailVerificationToken(utils.generateEmailVerificationToken(publicUserId));
        userEntity.setEmailVerificationStatus(Boolean.FALSE);

        UserEntity storedUserDetails= userRepository.save(userEntity);

        //BeanUtils.copyProperties(storedUserDetails,returnValue);
        UserDto returnValue=modelMapper.map(storedUserDetails,UserDto.class);

        // Send an email to user to verify email address
        emailSender.verifyEmail(returnValue);


        return returnValue;


    }

    @Override
    public UserDto getUser(String email) {
        UserEntity userEntity=userRepository.findByEmail(email);

        if(userEntity==null) throw new UsernameNotFoundException(email);

        UserDto returnValue=new UserDto();
        BeanUtils.copyProperties(userEntity,returnValue);
        return returnValue;
    }

    @Override
    public UserDto getUserByUserId(String userId) {

        UserEntity userEntity=userRepository.findByUserId(userId);

        if(userEntity==null) throw new UsernameNotFoundException("User with ID : "+userId+" not found");

        ModelMapper modelMapper=new ModelMapper();
        UserDto returnValue=modelMapper.map(userEntity,UserDto.class);
        return returnValue;
    }

    @Override
    public UserDto updateUser(String userId, UserDto user) {

        UserEntity userEntity=userRepository.findByUserId(userId);

        if(userEntity==null) throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());


        userEntity.setFirstName(user.getFirstName());
        userEntity.setLastName(user.getLastName());

        UserEntity updatedUserDetails=userRepository.save(userEntity);
        ModelMapper modelMapper=new ModelMapper();
        UserDto returnValue=modelMapper.map(updatedUserDetails,UserDto.class);

        return returnValue;
    }

    @Override
    public void deleteUser(String userId) {

        UserEntity userEntity=userRepository.findByUserId(userId);

        if(userEntity==null) throw new UsernameNotFoundException(userId);

        userRepository.delete(userEntity);
    }

    @Override
    public List<UserDto> getUsers(int page, int limit) {
        List<UserDto> returnValue=new ArrayList<>();

        if(page > 0) page=page-1;
        Pageable pageableRequest=  PageRequest.of(page, limit);
        Page<UserEntity> usersPage=userRepository.findAll(pageableRequest);
        List<UserEntity> users=usersPage.getContent();

        ModelMapper modelMapper=new ModelMapper();
        for(UserEntity userEntity:users){
            UserDto userDto=modelMapper.map(userEntity,UserDto.class);
            returnValue.add(userDto);
        }
        return returnValue;
    }

    @Override
    public boolean verifyEmailToken(String token) {
        boolean returnValue=false;

        UserEntity userEntity=userRepository.findByEmailVerificationToken(token);

        if(userEntity!=null){
            boolean hasTokenExpired=utils.hasTokenExpired(token);
            if(!hasTokenExpired){
                userEntity.setEmailVerificationToken(null);
                userEntity.setEmailVerificationStatus(Boolean.TRUE);
                userRepository.save(userEntity);
                returnValue=true;
            }
        }

        return returnValue;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity=userRepository.findByEmail(email);
        if(userEntity==null) throw new UsernameNotFoundException(email);

        return new User(userEntity.getEmail(),userEntity.getEncryptedPassword(),userEntity.getEmailVerificationStatus(),
                true,true,true,new ArrayList<>());

       // return new User(userEntity.getEmail(),userEntity.getEncryptedPassword(),new ArrayList<>());
    }


}
