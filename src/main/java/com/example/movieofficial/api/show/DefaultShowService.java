package com.example.movieofficial.api.show;

import com.example.movieofficial.api.hall.entities.Seat;
import com.example.movieofficial.api.hall.dtos.SeatRow;
import com.example.movieofficial.api.hall.interfaces.SeatMapper;
import com.example.movieofficial.api.show.dtos.ShowAutoCreate;
import com.example.movieofficial.api.show.dtos.ShowCreate;
import com.example.movieofficial.api.show.dtos.ShowDetail;
import com.example.movieofficial.api.show.dtos.ShowInfo;
import com.example.movieofficial.api.show.entities.Show;
import com.example.movieofficial.api.show.interfaces.ShowMapper;
import com.example.movieofficial.api.show.interfaces.ShowRepository;
import com.example.movieofficial.api.show.interfaces.ShowService;
import com.example.movieofficial.api.show.usecases.AutoScheduleShowUseCase;
import com.example.movieofficial.api.show.usecases.CreateShowUseCase;
import com.example.movieofficial.api.ticket.entities.Ticket;
import com.example.movieofficial.api.ticket.interfaces.TicketRepository;
import com.example.movieofficial.utils.exceptions.DataNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DefaultShowService implements ShowService {

    ShowRepository showRepository;
    TicketRepository ticketRepository;
    ShowMapper showMapper;
    SeatMapper seatMapper;

    //Use cases
    AutoScheduleShowUseCase autoCreateShow;
    CreateShowUseCase createShow;


    @Override
    public ShowInfo create(ShowCreate showCreate) {
        return createShow.execute(showCreate);
    }


    @Override
    public List<ShowInfo> scheduleShow(ShowAutoCreate showAutoCreate) {
        return autoCreateShow.execute(showAutoCreate);
    }


    @Override
    public ShowDetail getShowDetail(String showId) {

        Show show = showRepository.findWithDetailsByIdAndDateTime(
                showId,
                LocalDate.now(),
                LocalTime.now()
        ).orElseThrow(() -> new DataNotFoundException("Data not found", List.of("Show not found")));

        Map<SeatRow, List<SeatRow.SeatDto>> seatAfterSort = show.getHall().getSeats().stream()
                .sorted(Comparator.comparing(Seat::getCurrCol).reversed())
                .map(seatMapper::toDto)
                .collect(
                        Collectors.groupingBy(seat ->
                                SeatRow.builder()
                                        .row(seat.getCurrRow())
                                        .rowName(mapNumberToChar(seat.getCurrRow()))
                                        .build(),
                                TreeMap::new,
                                Collectors.toList()
                        )
                );
        ShowDetail showDetail = showMapper.toDetail(show);
        showDetail.getHall().setRows(seatAfterSort.entrySet().stream()
                .map(entry -> {
                    var row = entry.getKey();
                    row.setSeats(entry.getValue());
                    return row;
                })
                .toList()
        );

        List<Ticket> tickets = ticketRepository.findByShowIdOrderBySeatRowNameAscSeatRowIndexAsc(
                show.getId(),
                LocalDateTime.now().minusMinutes(2)
        );
        tickets.forEach(ticket -> {
            int row = showDetail.getHall().getNumberOfRows() - ticket.getSeat().getCurrRow();
            int col = showDetail.getHall().getColsPerRow() - ticket.getSeat().getCurrCol();
            showDetail.getHall().getRows().get(row).getSeats().get(col).setIsReserved(true);
        });
        List<Show> sameShow = showRepository.findSameShow(
                showDetail.getStartDate(),
                showDetail.getMovie().getSlug(),
                showDetail.getFormat().getId(),
                LocalTime.now(), LocalDate.now()
        );
        showDetail.setSameShows(sameShow.stream().map(showMapper::toInfo).collect(Collectors.toList()));
        return showDetail;
    }


    public String mapNumberToChar(int number) {
        number = number - 1;
        if (number >= 0 && number <= 25) {
            char result = (char) ('A' + number);
            return String.valueOf(result);
        } else {
            throw new IllegalArgumentException("Number must be between 0 and 25");
        }
    }


    @Override
    @Scheduled(cron = "0 0 3 * * ?", zone = "Asia/Ho_Chi_Minh")
//    @EventListener(ApplicationReadyEvent.class)
    @Async
    public void updateShowStatus() {
        showRepository.updateStatusByStartDateBefore(LocalDate.now());
    }
}
