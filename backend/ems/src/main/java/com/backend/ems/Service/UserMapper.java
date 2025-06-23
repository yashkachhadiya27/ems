package com.backend.ems.Service;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.backend.ems.DTO.UserDetailDTO;
import com.backend.ems.Entity.Register;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDetailDTO toUserDetailDTO(Register register);
}
