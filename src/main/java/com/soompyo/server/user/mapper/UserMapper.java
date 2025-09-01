package com.soompyo.server.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.soompyo.server.user.domain.User;
import com.soompyo.server.user.dto.response.UserDetailResponseDto;
import com.soompyo.server.user.dto.response.UserLoginResponseDto;
import com.soompyo.server.user.dto.response.UserSignUpResponseDto;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR, nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
public interface UserMapper {

    @Mapping(target = "email", source = "email")
    @Mapping(target = "createdAt", source = "createdAt")
    UserSignUpResponseDto toRegisteredUserDto(User user);

    @Mapping(target = "email", source = "email")
    @Mapping(target = "role", source = "role")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    @Mapping(target = "lastLoginAt", source = "lastLoginAt")
    UserDetailResponseDto toUserDetailDto(User user);

    @Mapping(target = "id", source = "user.id")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "role", source = "user.role")
    @Mapping(target = "accessToken", source = "token")
    UserLoginResponseDto toLoginResponseDto(User user, String token);

}
