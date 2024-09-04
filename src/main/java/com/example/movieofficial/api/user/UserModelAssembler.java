package com.example.movieofficial.api.user;

import com.example.movieofficial.api.user.dtos.UserInfo;
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
}
