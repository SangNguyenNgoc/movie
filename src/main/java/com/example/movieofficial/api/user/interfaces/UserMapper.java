package com.example.movieofficial.api.user.interfaces;

import com.example.movieofficial.api.user.dtos.UserInfo;
import com.example.movieofficial.api.user.dtos.UserProfile;
import com.example.movieofficial.api.user.entities.User;
import com.example.movieofficial.api.user.dtos.UserInfoUpdate;
import org.mapstruct.*;


@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface UserMapper {

    UserProfile toProfile(User user);

    UserInfo toUserInfo(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User partialUpdate(UserInfoUpdate userInfoUpdate, @MappingTarget User user);
}
