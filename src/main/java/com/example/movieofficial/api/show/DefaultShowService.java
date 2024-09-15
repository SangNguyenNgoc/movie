package com.example.movieofficial.api.show;

import com.example.movieofficial.api.hall.entities.Hall;
import com.example.movieofficial.api.hall.interfaces.HallRepository;
import com.example.movieofficial.api.movie.entities.Format;
import com.example.movieofficial.api.movie.entities.Movie;
import com.example.movieofficial.api.movie.interfaces.FormatRepository;
import com.example.movieofficial.api.movie.interfaces.MovieRepository;
import com.example.movieofficial.api.show.dtos.ShowCreate;
import com.example.movieofficial.api.show.dtos.ShowDetail;
import com.example.movieofficial.api.show.entities.Show;
import com.example.movieofficial.api.show.interfaces.ShowMapper;
import com.example.movieofficial.api.show.interfaces.ShowRepository;
import com.example.movieofficial.api.show.interfaces.ShowService;
import com.example.movieofficial.api.ticket.entities.Ticket;
import com.example.movieofficial.api.ticket.interfaces.TicketRepository;
import com.example.movieofficial.utils.exceptions.DataNotFoundException;
import com.example.movieofficial.utils.exceptions.InputInvalidException;
import com.example.movieofficial.utils.services.ObjectsValidator;
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
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DefaultShowService implements ShowService {

    ShowRepository showRepository;
    MovieRepository movieRepository;
    FormatRepository formatRepository;
    HallRepository hallRepository;
    TicketRepository ticketRepository;
    ShowMapper showMapper;
    ObjectsValidator<ShowCreate> showValidator;

    @Override
    public Movie checkMovieInput(String movieId, LocalDateTime dateTime) {
        Movie movie = movieRepository.findById(movieId).orElseThrow(
                () -> new DataNotFoundException("Data not found", List.of("Movie not found"))
        );
        LocalDate date = dateTime.toLocalDate();
        if (!(date.isAfter(movie.getReleaseDate()) && date.isBefore(movie.getEndDate()))) {
            throw new InputInvalidException(
                    "Input invalid",
                    List.of("The movie is not allowed to be shown at this time.")
            );
        }
        return movie;
    }

    @Override
    public Format checkFormatInput(String movieId, Long formatId) {
        Format format = formatRepository.findById(formatId).orElseThrow(
                () -> new DataNotFoundException("Data not found", List.of("Movie format not found"))
        );
        if (!formatRepository.existsByIdAndMoviesId(formatId, movieId)) {
            throw new DataNotFoundException(
                    "Data not found",
                    List.of("The movie is not shown in the format you requested")
            );
        }
        return format;
    }

    @Override
    public Hall checkHallInput(Long hallId, LocalDateTime dateTime, Movie movie) {
        Hall hall = hallRepository.findByIdAndStatusIdAndCinemaStatusId(hallId).orElseThrow(
                () -> new InputInvalidException(
                        "Input invalid",
                        List.of("The screening room does not exist or is under maintenance.")
                ));
        List<Show> shows = showRepository.findByStartDateAndHallId(dateTime.toLocalDate(), hallId);
        LocalTime startTimeTesting = dateTime.toLocalTime();
        LocalTime endTimeTesting = startTimeTesting.plusMinutes(movie.getRunningTime() + 30);
        for (Show show : shows) {
            LocalTime startTimeInData = show.getStartTime();
            LocalTime endTimeInData = startTimeInData.plusMinutes(show.getRunningTime());
            if ((startTimeTesting.isAfter(startTimeInData) && startTimeTesting.isBefore(endTimeInData)) ||
                    (startTimeInData.isAfter(startTimeTesting) && startTimeInData.isBefore(endTimeTesting)) ||
                    (startTimeTesting.equals(startTimeInData))
            ) {
                throw new InputInvalidException(
                        "Input invalid",
                        List.of("The screening room is not available at this time.")
                );
            }
        }
        return hall;
    }

    @Override
    public String create(ShowCreate showCreate) {
        showValidator.validate(showCreate);
        Movie movie = checkMovieInput(showCreate.getMovieId(), showCreate.getStartTime());
        Format format = checkFormatInput(showCreate.getMovieId(), showCreate.getFormatId());
        Hall hall = checkHallInput(showCreate.getHallId(), showCreate.getStartTime(), movie);
        Show show = Show.builder()
                .startDate(showCreate.getStartTime().toLocalDate())
                .startTime(showCreate.getStartTime().toLocalTime())
                .format(format)
                .movie(movie)
                .runningTime(movie.getRunningTime() + 30)
                .hall(hall)
                .status(true)
                .build();
        showRepository.save(show);
        return "Success";
    }

    @Override
    public ShowDetail getShowDetail(String showId) {
        Show show = showRepository.findWithDetailsByIdAndDateTime(
                showId,
                LocalDate.now(),
                LocalTime.now()
        ).orElseThrow(() -> new DataNotFoundException("Data not found", List.of("Show not found")));
        ShowDetail showDetail = showMapper.toDetail(show);
        List<ShowDetail.HallDto.SeatDto> seatDtoAfterSort = showDetail.getHall().getSeats().stream()
                .sorted(Comparator.comparing(ShowDetail.HallDto.SeatDto::getRowName)
                        .thenComparing(ShowDetail.HallDto.SeatDto::getRowIndex))
                .toList();
        showDetail.getHall().setSeats(seatDtoAfterSort);
        List<Ticket> tickets = ticketRepository.findByShowIdOrderBySeatRowNameAscSeatRowIndexAsc(
                show.getId(),
                LocalDateTime.now().minusMinutes(2)
        );
        tickets.forEach(ticket -> {
            char rowName = ticket.getSeat().getRowName().charAt(0);
            int rowIndex = ticket.getSeat().getRowIndex();
            int seatIndex = mapCharToNumber(rowName) * 10 + rowIndex - 1;
            showDetail.getHall().getSeats().get(seatIndex).setIsReserved(true);
        });
        return showDetail;
    }

    public int mapCharToNumber(char character) {
        if (character >= 'A' && character <= 'Z') {
            return character - 'A';
        } else {
            throw new IllegalArgumentException("Character must be between A and Z");
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
