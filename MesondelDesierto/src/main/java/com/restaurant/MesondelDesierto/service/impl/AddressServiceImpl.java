package com.restaurant.MesondelDesierto.service.impl;

import com.restaurant.MesondelDesierto.dto.AddressDto;
import com.restaurant.MesondelDesierto.dto.Response;
import com.restaurant.MesondelDesierto.entity.Address;
import com.restaurant.MesondelDesierto.entity.User;
import com.restaurant.MesondelDesierto.repository.AddressRepo;
import com.restaurant.MesondelDesierto.service.interf.AddressService;
import com.restaurant.MesondelDesierto.service.interf.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {
    private final AddressRepo addressRepo;
    private final UserService userService;


    @Override
    public Response saveAndUpdateAddress(AddressDto addressDto) {
        User user = userService.getLoginUser();
        Address address = user.getAddress();

        if (address == null){
            address = new Address();
            address.setUser(user);
        }
        if (addressDto.getStreet() != null) address.setStreet(addressDto.getStreet());
        if (addressDto.getCity() != null) address.setCity(addressDto.getCity());
        if (addressDto.getState() != null) address.setState(addressDto.getState());
        if (addressDto.getZipCode() != null) address.setZipCode(addressDto.getZipCode());
        if (addressDto.getCountry() != null) address.setCountry(addressDto.getCountry());

        addressRepo.save(address);

        String message = (user.getAddress() == null) ? "Direccion creada correctamente" : "Direccion actualizada correctamente";
        return Response.builder()
                .status(200)
                .message(message)
                .build();
    }
}
