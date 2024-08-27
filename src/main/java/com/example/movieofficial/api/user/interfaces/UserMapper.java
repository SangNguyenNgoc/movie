package com.example.movieofficial.api.user.interfaces;

import com.example.movieofficial.api.user.dtos.UserInfo;
import com.example.movieofficial.api.user.dtos.UserProfile;
import com.example.movieofficial.api.user.entities.Role;
import com.example.movieofficial.api.user.entities.User;
import org.mapstruct.*;


@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface UserMapper {

    UserProfile toProfile(User user);

    UserInfo toUserInfo(User user);
}
