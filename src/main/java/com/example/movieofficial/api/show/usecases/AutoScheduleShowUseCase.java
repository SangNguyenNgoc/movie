package com.example.movieofficial.api.show.usecases;

import com.example.movieofficial.api.hall.entities.Hall;
import com.example.movieofficial.api.show.dtos.ShowAutoCreate;
import com.example.movieofficial.api.show.dtos.ShowInfo;
import com.example.movieofficial.api.show.entities.Show;

import java.time.LocalDate;
import java.util.List;

public interface AutoScheduleShowUseCase {

    //Hàm thực hiện chính của use case
    List<ShowInfo> execute(ShowAutoCreate showAutoCreate);

    // Lọc danh sách phim đầu vào, bảo đảm tất cả phim đều đúng ngày chiếu và format
    void filterMoviesInput(List<ShowAutoCreate.MovieDto> moviesInput, LocalDate date);

    // Sắp xếp các suất chiếu
    List<Show> scheduleShow(
            List<ShowAutoCreate.MovieDto> movies,
            List<Hall> halls,
            LocalDate date
    );

    // Tìm ra 1 phòng trống trong thời gian truyền vào
    Hall findAvailableHall(int currentTime, List<Hall> halls, List<Show> shows);

    // Tìm ra phim có độ ưu tiên cao nhất trong danh sách
    ShowAutoCreate.MovieDto findBestMovie(
            List<Show> shows, List<ShowAutoCreate.MovieDto> movies, int currentTime
    );

    // Tìm xem phim đó đã được chiếu gần đây chưa
    boolean wasRecentlyShow(int currentTime, List<Show> shows, ShowAutoCreate.MovieDto movie);

    // Tính toán xem phòng đó có thể chiếu được hay không
    boolean isRoomAvailable(int currentTime, Hall hall, List<Show> shows);
}
