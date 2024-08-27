package com.example.movieofficial.api.hall;

import com.example.movieofficial.api.hall.entities.Hall;
import com.example.movieofficial.api.hall.entities.Seat;
import com.example.movieofficial.api.hall.entities.SeatType;
import com.example.movieofficial.api.hall.interfaces.HallRepository;
import com.example.movieofficial.api.hall.interfaces.HallService;
import com.example.movieofficial.api.hall.interfaces.SeatRepository;
import com.example.movieofficial.api.hall.interfaces.SeatTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DefaultHallService implements HallService {

    private final HallRepository hallRepository;

    private final SeatRepository seatRepository;

    private final SeatTypeRepository seatTypeRepository;
}
