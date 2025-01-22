package com.example.movieofficial.api.hall.services.impl;

import com.example.movieofficial.api.cinema.interfaces.CinemaRepository;
import com.example.movieofficial.api.hall.dtos.HallCreateRequest;
import com.example.movieofficial.api.hall.dtos.HallDetail;
import com.example.movieofficial.api.hall.dtos.HallResponse;
import com.example.movieofficial.api.hall.dtos.SeatRow;
import com.example.movieofficial.api.hall.entities.Hall;
import com.example.movieofficial.api.hall.entities.Seat;
import com.example.movieofficial.api.hall.entities.SeatType;
import com.example.movieofficial.api.hall.mappers.HallMapper;
import com.example.movieofficial.api.hall.mappers.SeatMapper;
import com.example.movieofficial.api.hall.repositories.HallRepository;
import com.example.movieofficial.api.hall.repositories.HallStatusRepository;
import com.example.movieofficial.api.hall.repositories.SeatTypeRepository;
import com.example.movieofficial.api.hall.services.HallService;
import com.example.movieofficial.utils.exceptions.DataNotFoundException;
import com.example.movieofficial.utils.exceptions.InputInvalidException;
import com.example.movieofficial.utils.exceptions.ServerInternalException;
import com.example.movieofficial.utils.services.ObjectsValidator;
import com.example.movieofficial.utils.services.UtilsService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DefaultHallService implements HallService {

    HallRepository hallRepository;
    HallMapper hallMapper;
    SeatMapper seatMapper;
    UtilsService utilsService;
    CinemaRepository cinemaRepository;
    SeatTypeRepository seatTypeRepository;
    HallStatusRepository hallStatusRepository;
    ObjectsValidator<HallCreateRequest> hallCreateValidator;
    ObjectsValidator<HallCreateRequest.SeatRow> rowValidator;
    ObjectsValidator<HallCreateRequest.SeatDto> seatValidator;

    @Override
    public List<HallResponse> getHallByCinema(String cinemaId) {
        return hallRepository.findByCinemaIdOrderByIdAsc(cinemaId)
                .stream()
                .map(hallMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public HallDetail getHallById(long hallId) {
        var hall = hallRepository.findById(hallId).orElseThrow(
                () -> new DataNotFoundException("Data not found", List.of("Hall not found"))
        );
        var hallDetail = hallMapper.toDetail(hall);
        Map<SeatRow, List<SeatRow.SeatDto>> seatAfterSort = hall.getSeats().stream()
                .sorted(Comparator.comparing(Seat::getCurrCol).reversed())
                .map(seatMapper::toDto)
                .collect(
                        Collectors.groupingBy(seat ->
                                        SeatRow.builder()
                                                .row(seat.getCurrRow())
                                                .rowName(utilsService.mapNumberToChar(seat.getCurrRow()))
                                                .build(),
                                TreeMap::new, //TreeMap sort object thank to Comparable<SeatRow>
                                Collectors.toList()
                        )
                );
        hallDetail.setSeats(seatAfterSort.entrySet().stream()
                .map(entry -> {
                    var row = entry.getKey();
                    row.setSeats(entry.getValue());
                    return row;
                })
                .toList()
        );
        return hallDetail;
    }

    @Override
    @Transactional
    public HallResponse create(HallCreateRequest request) {
        hallCreateValidator.validate(request);
        var cinema = cinemaRepository.findById(request.getCinemaId()).orElseThrow(
                () -> new DataNotFoundException("Data not found", List.of("Cinema not found"))
        );
        var hallStatus = hallStatusRepository.findById(2L).orElseThrow(
                () -> new ServerInternalException("Server error", List.of("Error in setting status"))
        );
        var cinemaName = cinema.getName().substring(cinema.getName().indexOf(' ') + 1);
        var seatTypes = seatTypeRepository.findAll();
        var hall = Hall.builder()
                .name(request.getName() + "-" + cinemaName)
                .cinema(cinema)
                .status(hallStatus)
                .totalSeats(request.getTotalSeats())
                .availableSeats(request.getTotalSeats())
                .numberOfRows(request.getNumberOfRows())
                .colsPerRow(request.getColsPerRow())
                .build();
        var seatRows = request.getRows().stream()
                .sorted(Comparator.comparing(HallCreateRequest.SeatRow::getRowName)).toList();
        hall.setSeats(createSeat(seatRows, hall, seatTypes));
        hallRepository.save(hall);
        return hallMapper.toDto(hall);
    }

    private Set<Seat> createSeat(List<HallCreateRequest.SeatRow> seatRows, Hall hall, List<SeatType> seatTypes) {
        List<Seat> seats = new ArrayList<>();
        seatRows.forEach(seatRow -> {
            if (seatRow == null) {
                throw new InputInvalidException("Input invalid", List.of("One of row is null"));
            }
            rowValidator.validate(seatRow);
            int seatNumber = 1;
            for(var i= seatRow.getSeats().size() - 1; i>=0 ; i--) {
                var seatInput = seatRow.getSeats().get(i);
                if (seatInput != null) {
                    seatValidator.validate(seatInput);
                    var seat = Seat.builder()
                            .hall(hall)
                            .name(null)
                            .currCol(seatInput.getCurrCol())
                            .currRow(utilsService.mapCharToNumber(seatRow.getRowName()))
                            .type(seatTypes.stream().filter(type -> type.getId().equals(seatInput.getTypeId())).findFirst().orElse(null))
                            .build();
                    if (seatInput.getTypeId() != 3) {
                        seat.setName(seatRow.getRowName() + seatNumber);
                        seatNumber++;
                    }
                    seats.add(seat);
                } else {
                    throw new InputInvalidException("Input invalid", List.of("One of seats is null"));
                }
            }
        });
        return seats.stream().collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public HallResponse updateHallStatus(long hallId, long statusId) {
        var hallStatus = hallStatusRepository.findById(2L).orElseThrow(
                () -> new DataNotFoundException("Data not found", List.of("Hall status not found"))
        );
        var hall = hallRepository.findById(hallId).orElseThrow(
                () -> new DataNotFoundException("Data not found", List.of("Hall not found"))
        );
        hall.setStatus(hallStatus);
        return hallMapper.toDto(hall);
    }
}
