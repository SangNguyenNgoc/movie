package com.example.movieofficial.api.show.interfaces;

import com.example.movieofficial.api.show.dtos.ShowAutoCreate;
import com.example.movieofficial.api.show.dtos.ShowCreate;
import com.example.movieofficial.api.show.dtos.ShowDetail;
import com.example.movieofficial.api.show.dtos.ShowInfo;

import java.time.LocalDateTime;
import java.util.List;

public interface ShowService {

    ShowInfo create(ShowCreate showCreate);

    List<ShowInfo> scheduleShow(ShowAutoCreate showAutoCreate);

    ShowDetail getShowDetail(String showId);

    void updateShowStatus();

}
