package com.restaurant.MesondelDesierto.service.interf;

import com.restaurant.MesondelDesierto.dto.AddressDto;
import com.restaurant.MesondelDesierto.dto.Response;

public interface AddressService {
    Response saveAndUpdateAddress(AddressDto addressDto);
}
