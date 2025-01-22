package com.example.movieofficial.api.hall.services;

import com.example.movieofficial.api.hall.dtos.HallCreateRequest;
import com.example.movieofficial.api.hall.dtos.HallDetail;
import com.example.movieofficial.api.hall.dtos.HallResponse;
import com.example.movieofficial.api.hall.entities.HallStatus;

import java.util.List;

public interface HallService {

    List<HallResponse> getHallByCinema(String cinemaId);

    HallDetail getHallById(long hallId);

    HallResponse create(HallCreateRequest request);

    HallResponse updateHallStatus(long hallId, long statusId);
}
