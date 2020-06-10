package com.example.ecommerce.ui.controller;

import com.example.ecommerce.service.UserService;
import com.example.ecommerce.service.impl.AddressesServiceImpl;
import com.example.ecommerce.service.impl.UserServiceImpl;
import com.example.ecommerce.shared.dto.AddressDTO;
import com.example.ecommerce.ui.model.request.PasswordResetModel;
import com.example.ecommerce.ui.model.request.PasswordResetRequestModel;
import com.example.ecommerce.ui.model.request.UserDetailsRequestModel;
import com.example.ecommerce.ui.model.response.AddressesRest;
import com.example.ecommerce.ui.model.response.OperationStatusModel;
import com.example.ecommerce.ui.model.response.RequestOperationStatus;
import com.example.ecommerce.ui.model.response.UserRest;
import com.example.ecommerce.shared.dto.UserDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/users")  // http:localhost:8080/users
public class UserController {


    @Autowired
    UserServiceImpl userService;

    @Autowired
    AddressesServiceImpl addressesService;

    @GetMapping(path = "/{id}",
            produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public UserRest getUser(@PathVariable String id){

        UserDto userDto=userService.getUserByUserId(id);
        ModelMapper modelMapper=new ModelMapper();
        UserRest returnValue=modelMapper.map(userDto,UserRest.class);

        return returnValue ;
    }

    @PostMapping(
            consumes = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
            produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}
    )
    public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails){

        UserRest returnValue=new UserRest();

//        UserDto userDto=new UserDto();
//        BeanUtils.copyProperties(userDetails,userDto);

        ModelMapper modelMapper=new ModelMapper();
        UserDto userDto=modelMapper.map(userDetails,UserDto.class);

        UserDto createUser=userService.createUser(userDto);
        returnValue=modelMapper.map(createUser,UserRest.class);
        return returnValue;

    }


    @PutMapping(path = "/{id}",
            consumes = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
            produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}
    )
    public UserRest updateUser(@RequestBody UserDetailsRequestModel userDetails, @PathVariable String id){


        ModelMapper modelMapper=new ModelMapper();
        UserDto userDto=modelMapper.map(userDetails,UserDto.class);

        UserDto createUser=userService.updateUser(id,userDto);
        UserRest returnValue=modelMapper.map(createUser,UserRest.class);
        return returnValue;
    }

    @DeleteMapping(path = "/{id}",
            produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}
            )
    public OperationStatusModel deleteUser(@PathVariable String id){
        OperationStatusModel returnValue=new OperationStatusModel();
        returnValue.setOperationName(RequestOperationName.DELETE.name());

        userService.deleteUser(id);

        returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());

        return returnValue;
    }

    @GetMapping(
            produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}
    )
    public List<UserRest> getUsers(@RequestParam(value = "page",defaultValue = "0") int page,@RequestParam(value = "limit",defaultValue = "2") int limit){
        List<UserRest> returnValue=new ArrayList<>();

        List<UserDto> users=userService.getUsers(page,limit);
        ModelMapper modelMapper=new ModelMapper();

        for(UserDto userDto:users){
            UserRest userModel=modelMapper.map(userDto,UserRest.class);
            returnValue.add(userModel);
        }
        return returnValue;
    }

    @GetMapping(path = "/{userId}/addresses",
            produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE, "application/hal+json"})
    public CollectionModel<AddressesRest> getUserAddresses(@PathVariable String userId){

        List<AddressesRest> returnValue=new ArrayList<>();

        List<AddressDTO> addressess=addressesService.getAddresses(userId);
        ModelMapper modelMapper = new ModelMapper();

        if(addressess!=null && !addressess.isEmpty()) {
            Type listType = new TypeToken<List<AddressesRest>>() {}.getType();
            returnValue = modelMapper.map(addressess, listType);

            for(AddressesRest addressesRest:returnValue){
                Link addressLink= linkTo(methodOn(UserController.class).getUserAddress(userId,addressesRest.getAddressId())).withSelfRel();
                addressesRest.add(addressLink);
                Link userLink= linkTo(methodOn(UserController.class).getUser(userId)).withRel("user");
                addressesRest.add(userLink);

            }
        }

        return new CollectionModel<>(returnValue);
    }

    @GetMapping(path = "/{userId}/addresses/{addressId}",
            produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE, "application/hal+json"})
    public AddressesRest getUserAddress(@PathVariable String userId,@PathVariable String addressId){

        AddressDTO addressDTO=addressesService.getAddress(addressId);
        ModelMapper modelMapper=new ModelMapper();
        Link addressLink= linkTo(methodOn(UserController.class).getUserAddress(userId,addressId)).withSelfRel();
        Link userLink= linkTo(methodOn(UserController.class).getUser(userId)).withRel("user");
        Link addressesLink =linkTo(methodOn(UserController.class).getUserAddresses(userId)).withRel("addresses");

        AddressesRest returnValue= modelMapper.map(addressDTO,AddressesRest.class);
        returnValue.add(addressLink);
        returnValue.add(userLink);
        returnValue.add(addressesLink);
        return returnValue;
    }

    /**
     * http://localhost:8080/ecommerce/users/email-verification?token=sdfsf
     */
    @GetMapping(path = "/email-verification",
            produces = { MediaType.APPLICATION_JSON_VALUE,})
    public OperationStatusModel verifyEmailToken(@RequestParam( value = "token") String token){

        OperationStatusModel returnValue=new OperationStatusModel();
        boolean isVerified=userService.verifyEmailToken(token);

        if(isVerified){
            returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
        }else{
            returnValue.setOperationResult(RequestOperationStatus.ERROR.name());
        }

        returnValue.setOperationName(RequestOperationName.VERIFY_EMAIL.name());
        return returnValue;

    }

    @PostMapping(path = "/password-reset-request",
            consumes = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
            produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}
    )
    public OperationStatusModel passwordRequestReset(@RequestBody PasswordResetRequestModel passwordResetRequestModel){

        OperationStatusModel returnValue=new OperationStatusModel();

        boolean operationResult=userService.requestPasswordReset(passwordResetRequestModel.getEmail());

        returnValue.setOperationName(RequestOperationName.REQUEST_PASSWORD_RESET.name());
        returnValue.setOperationResult(RequestOperationStatus.ERROR.name());

        if(operationResult){
            returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
        }

        return returnValue;

    }

    @PostMapping(path = "/password-reset",
            consumes = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
            produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}
    )
    public OperationStatusModel passwordReset(@RequestBody PasswordResetModel passwordResetModel){

        OperationStatusModel returnValue=new OperationStatusModel();

        boolean operationResult=userService.resetPassword(passwordResetModel.getToken(),passwordResetModel.getPassword());

        returnValue.setOperationName(RequestOperationName.PASSWORD_RESET.name());
        returnValue.setOperationResult(RequestOperationStatus.ERROR.name());

        if(operationResult){
            returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
        }

        return returnValue;

    }


}
