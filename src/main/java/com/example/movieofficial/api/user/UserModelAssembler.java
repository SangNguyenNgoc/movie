package com.example.movieofficial.api.user;

import com.example.movieofficial.api.user.dtos.UserInfo;
import com.example.movieofficial.api.user.dtos.UserProfile;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class UserModelAssembler {

    public void linkToGetUserInfo(UserInfo userInfo) {
        userInfo.add(linkTo(methodOn(UserController.class)
                .getById(userInfo.getId()))
                .withRel("detail")
                .withType(HttpMethod.GET.name())
        );
    }

    public void linkToGetAllUser(UserInfo userInfo) {
        userInfo.add(linkTo(methodOn(UserController.class)
                .getById(userInfo.getId()))
                .withSelfRel()
                .withType(HttpMethod.GET.name())
        );

        userInfo.add(linkTo(UserController.class)
                .withRel("all")
                .withType(HttpMethod.GET.name()));
    }

    public void linkToCrudUser(UserProfile userProfile) {
        userProfile.add(linkTo(UserController.class)
                .slash("/profile")
                .withSelfRel()
                .withType(HttpMethod.GET.name()));

        userProfile.add(linkTo(UserController.class)
                .slash("/information")
                .withRel("update_info")
                .withType(HttpMethod.PUT.name()));

        userProfile.add(linkTo(UserController.class)
                .slash("/avatar")
                .withRel("update_avatar")
                .withType(HttpMethod.PUT.name()));
    }
}
