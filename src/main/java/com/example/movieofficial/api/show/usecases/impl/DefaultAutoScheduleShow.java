package com.example.movieofficial.api.show.usecases.impl;

import com.example.movieofficial.api.hall.entities.Hall;
import com.example.movieofficial.api.hall.repositories.HallRepository;
import com.example.movieofficial.api.movie.entities.Format;
import com.example.movieofficial.api.movie.entities.Movie;
import com.example.movieofficial.api.movie.repositories.FormatRepository;
import com.example.movieofficial.api.movie.repositories.MovieRepository;
import com.example.movieofficial.api.show.dtos.ShowAutoCreate;
import com.example.movieofficial.api.show.dtos.ShowInfo;
import com.example.movieofficial.api.show.entities.Show;
import com.example.movieofficial.api.show.interfaces.ShowMapper;
import com.example.movieofficial.api.show.interfaces.ShowRepository;
import com.example.movieofficial.api.show.usecases.AutoScheduleShowUseCase;
import com.example.movieofficial.api.show.usecases.configs.ScheduleShowsConfig;
import com.example.movieofficial.utils.exceptions.DataNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DefaultAutoScheduleShow implements AutoScheduleShowUseCase {
    ShowRepository showRepository;
    MovieRepository movieRepository;
    HallRepository hallRepository;
    FormatRepository formatRepository;
    ShowMapper showMapper;
    ScheduleShowsConfig useCaseConfig;


    @Override
    public List<ShowInfo> execute(ShowAutoCreate showAutoCreate) {
        var createDate = showAutoCreate.getDate();
        filterMoviesInput(showAutoCreate.getMovies(), createDate);
        var halls = hallRepository.findHallsWithoutShows(createDate, showAutoCreate.getCinemaId());
        if (halls.isEmpty()) {
            throw new DataNotFoundException("Halls not found", List.of("All of halls are unavailable."));
        }
        var shows = scheduleShow(showAutoCreate.getMovies(), halls, createDate);
        var showsCreated = showRepository.saveAll(shows);
        return showsCreated.stream().map(showMapper::toInfo).collect(Collectors.toList());
    }


    @Override
    public void filterMoviesInput(
            List<ShowAutoCreate.MovieDto> moviesInput, LocalDate date
    ) {
        var movies = movieRepository.findByDate(date);
        moviesInput.forEach(movieDto -> {
            var foundMovie = movies.stream()
                    .filter(movie -> movieDto.getId().equals(movie.getId()))
                    .findFirst().orElseThrow(
                            () -> new DataNotFoundException("Data not found", List.of("One of the movies not found"))
                    );
            var format = foundMovie.getFormats().stream()
                    .filter(f -> f.getId().equals(movieDto.getFormatId()))
                    .findFirst()
                    .orElseThrow(
                            () -> new DataNotFoundException("Data not found",
                                    List.of("Format not found with ID: " + movieDto.getFormatId()))
                    );
            movieDto.setRunningTime(foundMovie.getRunningTime());
            movieDto.setReleaseDate(foundMovie.getReleaseDate());
            movieDto.setOriginalPriority(movieDto.getPriority());
        });
    }


    @Override
    public List<Show> scheduleShow(
            List<ShowAutoCreate.MovieDto> movies,
            List<Hall> halls,
            LocalDate date
    ) {
        int currentTime = 0;
        List<Show> shows = new ArrayList<>();
        LocalDateTime startDate = LocalDateTime.of(date, LocalTime.parse(useCaseConfig.START_IN_DAY));
        LocalDateTime endDate = LocalDateTime.of(date, LocalTime.parse(useCaseConfig.END_IN_DAY));

        List<Movie> allMovies = movieRepository.findAll();
        List<Format> allFormats = formatRepository.findAll();

        while (true) {
            boolean allMoviesWithZeroPriority = movies.stream().allMatch(movie -> movie.getPriority() == 0);
            if (allMoviesWithZeroPriority) {
                movies.forEach(ShowAutoCreate.MovieDto::resetPriority);
            }

            Hall hall = findAvailableHall(currentTime, halls, shows);
            if (hall == null) {
                currentTime += 15;
                continue;
            }

            ShowAutoCreate.MovieDto bestMovie = findBestMovie(shows, movies, currentTime);
            if (bestMovie == null) {
                currentTime += 15;
                continue;
            }

            LocalDateTime showTime = startDate.plusMinutes(currentTime);
            if (showTime.isAfter(endDate)) break;

            Optional<Movie> movieOpt = allMovies.stream()
                    .filter(movie -> movie.getId().equals(bestMovie.getId()))
                    .findFirst();

            Optional<Format> formatOpt = allFormats.stream()
                    .filter(format -> format.getId().equals(bestMovie.getFormatId()))
                    .findFirst();

            if (movieOpt.isEmpty() || formatOpt.isEmpty()) {
                throw new DataNotFoundException("Data not found",List.of("Movie or Format not found"));
            }

            Show newShow = new Show();
            newShow.setMovie(movieOpt.get());
            newShow.setFormat(formatOpt.get());
            newShow.setHall(hall);
            newShow.setRunningTime(bestMovie.getRunningTime());
            newShow.setStartDate(startDate.toLocalDate());
            newShow.setStartTime(startDate.toLocalTime().plusMinutes(currentTime));
            newShow.setStatus(true);

            shows.add(newShow);
        }

        return shows;
    }


    @Override
    public Hall findAvailableHall(int currentTime, List<Hall> halls, List<Show> shows) {
        return halls.stream().filter(hall -> isRoomAvailable(currentTime, hall, shows)).findFirst().orElse(null);
    }


    @Override
    public ShowAutoCreate.MovieDto findBestMovie(
            List<Show> shows, List<ShowAutoCreate.MovieDto> movies, int currentTime)
    {
        // Tạo danh sách mới để sắp xếp
        List<ShowAutoCreate.MovieDto> sortedMovies = new ArrayList<>(movies);

        // Sắp xếp danh sách theo độ ưu tiên giảm dần, nếu bằng nhau thì sắp xếp theo ngày ra mắt mới nhất
        sortedMovies.sort((a, b) -> {
            if (!b.getPriority().equals(a.getPriority())) {
                return b.getPriority().compareTo(a.getPriority()); // Sắp xếp theo độ ưu tiên
            }
            return b.getReleaseDate().compareTo(a.getReleaseDate()); // Ngày ra mắt mới hơn đặt trước
        });

        // Tìm movie không chiếu gần đây
        for (ShowAutoCreate.MovieDto movie : sortedMovies) {
            if (!wasRecentlyShow(currentTime, shows, movie)) {
                movie.decreasePriority();
                return movie;
            }
        }

        return null; // Nếu không có movie nào phù hợp
    }


    @Override
    public boolean wasRecentlyShow(int currentTime, List<Show> shows, ShowAutoCreate.MovieDto movie) {
        // Tìm show cuối cùng của movie
        Optional<Show> lastShow = shows.stream()
                .filter(show -> show.getMovie().getId().equals(movie.getId()))
                .max(Comparator.comparing(Show::getStartTime));

        // Nếu không có show nào, trả về false
        if (lastShow.isEmpty()) {
            return false;
        }

        // Tính thời gian bắt đầu của show cuối cùng
        Show show = lastShow.get();
        int startTimeToIntMinute = (show.getStartTime().getHour() - 9) * 60 + show.getStartTime().getMinute();

        // So sánh thời gian hiện tại với thời gian bắt đầu của show cuối cùng
        return currentTime - startTimeToIntMinute < Integer.parseInt(useCaseConfig.INTERVAL_TIME);
    }


    @Override
    public boolean isRoomAvailable(int currentTime, Hall hall, List<Show> shows) {
        // Tìm show cuối cùng diễn ra trong hall cụ thể
        Optional<Show> lastShow = shows.stream()
                .filter(show -> show.getHall().getId().equals(hall.getId()))
                .max(Comparator.comparing(Show::getStartTime));

        // Nếu không có show nào, phòng có sẵn
        if (lastShow.isEmpty()) {
            return true;
        }

        // Tính thời gian có thể sử dụng phòng sau khi show cuối kết thúc
        Show show = lastShow.get();
        int availableTimeInHall = (show.getStartTime().getHour() - 9) * 60
                + show.getStartTime().getMinute()
                + show.getMovie().getRunningTime()
                + Integer.parseInt(useCaseConfig.CLEANING_TIME); // Thay `_cleaningTime` bằng biến hoặc giá trị thực tế

        return availableTimeInHall <= currentTime;
    }
}
