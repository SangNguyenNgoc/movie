package com.example.movieofficial;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

class Movie {
    String name;
    int priority;
    int originalPriority;
    int lastShowTime; // phút cuối cùng chiếu phim này
    int duration; // thời lượng phim (phút)
    LocalDate releaseDate; // Ngày ra mắt phim

    public Movie(String name, int priority, int duration, LocalDate releaseDate) {
        this.name = name;
        this.priority = priority;
        this.originalPriority = priority;
        this.lastShowTime = -1;
        this.duration = duration;
        this.releaseDate = releaseDate;
    }

    public void resetPriority() {
        this.priority = this.originalPriority;
    }

    public void decreasePriority() {
        this.priority--;
    }

    public boolean wasRecentlyShown(int currentTime) {
        // Check nếu phim được chiếu trong 15 phút trước
        return lastShowTime != -1 && (currentTime - lastShowTime) < 15;
    }

    @Override
    public String toString() {
        return name + " (Priority: " + priority + ", Duration: " + duration + " mins, Release Date: " + releaseDate + ")";
    }
}

class Room {
    int id;
    boolean isAvailable;
    int availableFrom;

    public Room(int id) {
        this.id = id;
        this.isAvailable = true;
        this.availableFrom = 0;
    }

    public boolean isRoomAvailable(int currentTime) {
        return isAvailable || currentTime >= availableFrom;
    }

    public void assignMovie(Movie movie, int currentTime) {
        this.isAvailable = false;
        // Tính toán thời gian phòng sẽ trống: thời lượng phim + 20 phút nghỉ
        this.availableFrom = currentTime + movie.duration + 15;
        movie.lastShowTime = currentTime;
    }

    @Override
    public String toString() {
        return "Room " + id + " (Available from: " + availableFrom + ")";
    }
}

class Scheduler {
    List<Movie> movies;
    List<Room> rooms;
    int currentTime = 0;

    public Scheduler(List<Movie> movies, List<Room> rooms) {
        this.movies = movies;
        this.rooms = rooms;
    }

    public void resetMoviePriorities() {
        for (Movie movie : movies) {
            movie.resetPriority();
        }
    }

    public List<String> scheduleShowings(LocalDateTime startDateTime) {
        // Đổi thời gian từ LocalDateTime sang phút trong ngày
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        String formattedStartTime = startDateTime.format(timeFormatter);
        System.out.println("Scheduling shows starting at: " + formattedStartTime);

        // Xác định giới hạn thời gian chiếu từ 9h sáng đến 10h đêm
        LocalDateTime endOfDay = startDateTime.withHour(23).withMinute(59);
        List<String> showTimeList = new ArrayList<>();

        while (true) {
            boolean allMoviesWithZeroPriority = movies.stream().allMatch(movie -> movie.priority == 0);

            if (allMoviesWithZeroPriority) {
                resetMoviePriorities();
            }

            Room availableRoom = findAvailableRoom();
            if (availableRoom == null) {
                // Nếu không có phòng nào trống, tăng 5 phút
                currentTime += 15;
                continue;
            }

            Movie bestMovie = findBestMovie();
            if (bestMovie == null) {
                currentTime += 15;  // Nếu không có phim phù hợp, cũng tăng 5 phút
                continue;
            }

            // Kiểm tra nếu thời gian chiếu vượt quá 22h thì dừng lại
            LocalDateTime showTime = startDateTime.plusMinutes(currentTime);
            if (showTime.isAfter(endOfDay)) {
                System.out.println("Scheduling ends as the time exceeds 23:00.");
                break;
            }

            // Gán phim vào phòng chiếu
            availableRoom.assignMovie(bestMovie, currentTime);
            bestMovie.decreasePriority();

            showTimeList.add(new String("At " + showTime.format(timeFormatter) + ": " + bestMovie + " is assigned to " + availableRoom));
        }
        return showTimeList;
    }

    public Room findAvailableRoom() {
        for (Room room : rooms) {
            if (room.isRoomAvailable(currentTime)) {
                return room;
            }
        }
        return null;
    }

    public Movie findBestMovie() {
        // Sắp xếp theo độ ưu tiên giảm dần, và nếu bằng nhau thì sắp xếp theo ngày ra mắt mới nhất
        List<Movie> sortedMovies = new ArrayList<>(movies);
        sortedMovies.sort((a, b) -> {
            if (b.priority == a.priority) {
                return b.releaseDate.compareTo(a.releaseDate); // Ngày ra mắt mới nhất sẽ được ưu tiên
            }
            return Integer.compare(b.priority, a.priority); // Sắp xếp theo độ ưu tiên
        });

        for (Movie movie : sortedMovies) {
            if (!movie.wasRecentlyShown(currentTime)) {
                return movie;
            }
        }
        return null;
    }
}

public class Test {
    public static void main(String[] args) {
        // Nhập thời gian bắt đầu ngày chiếu phim (ngày trong tương lai)
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the start date and time for scheduling (yyyy-MM-dd HH:mm):");
        String input = scanner.nextLine();

        // Parse chuỗi nhập vào thành LocalDateTime
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime startDateTime = LocalDateTime.parse(input, formatter);

        // Tạo danh sách phim với ngày ra mắt khác nhau
        List<Movie> movies = new ArrayList<>();
        movies.add(new Movie("Movie A", 4, 110, LocalDate.of(2024, 8, 10))); // Phim A ra mắt 10/08/2024
        movies.add(new Movie("Movie B", 2, 90, LocalDate.of(2024, 9, 1)));  // Phim B ra mắt 01/09/2024
        movies.add(new Movie("Movie C", 2, 96, LocalDate.of(2024, 7, 15))); // Phim C ra mắt 15/07/2024
        movies.add(new Movie("Movie D", 1, 114, LocalDate.of(2024, 7, 14))); //
        movies.add(new Movie("Movie E", 1, 105, LocalDate.of(2024, 7, 13))); //
//        movies.add(new Movie("Movie F", 4, 110, LocalDate.of(2024, 8, 10))); // Phim A ra mắt 10/08/2024
//        movies.add(new Movie("Movie G", 2, 90, LocalDate.of(2024, 9, 1)));  // Phim B ra mắt 01/09/2024
//        movies.add(new Movie("Movie H", 2, 96, LocalDate.of(2024, 7, 15))); // Phim C ra mắt 15/07/2024
//        movies.add(new Movie("Movie J", 1, 114, LocalDate.of(2024, 7, 14))); //
//        movies.add(new Movie("Movie K", 1, 105, LocalDate.of(2024, 7, 13))); //
//        movies.add(new Movie("Movie L", 4, 110, LocalDate.of(2024, 8, 10))); // Phim A ra mắt 10/08/2024
//        movies.add(new Movie("Movie M", 2, 90, LocalDate.of(2024, 9, 1)));  // Phim B ra mắt 01/09/2024
//        movies.add(new Movie("Movie N", 2, 96, LocalDate.of(2024, 7, 15))); // Phim C ra mắt 15/07/2024
//        movies.add(new Movie("Movie P", 1, 114, LocalDate.of(2024, 7, 14))); //
//        movies.add(new Movie("Movie Q", 1, 105, LocalDate.of(2024, 7, 13))); //

        // Tạo danh sách phòng chiếu
        List<Room> rooms = new ArrayList<>();
        rooms.add(new Room(1));
        rooms.add(new Room(2));
        rooms.add(new Room(3));
        rooms.add(new Room(4));
        rooms.add(new Room(5));

        // Tạo đối tượng Scheduler và bắt đầu sắp xếp từ 9h sáng
        Scheduler scheduler = new Scheduler(movies, rooms);
        LocalDateTime startOfDay = startDateTime.withHour(startDateTime.getHour()).withMinute(0);
        List<String> result = scheduler.scheduleShowings(startOfDay);
        var count = 0;
        for (String s : result) {
            if (s.contains("Movie A")) {
                System.out.println(s);
                count++;
            }
        }
        System.out.println("Slot for movie A:" + count);
        count = 0;

        for (String s : result) {
            if (s.contains("Movie B")) {
                System.out.println(s);
                count++;
            }
        }
        System.out.println("Slot for movie B:" + count);
        count = 0;

        for (String s : result) {
            if (s.contains("Movie C")) {
                System.out.println(s);
                count++;
            }
        }
        System.out.println("Slot for movie C:" + count);
        count = 0;

        for (String s : result) {
            if (s.contains("Movie D")) {
                System.out.println(s);
                count++;
            }
        }
        System.out.println("Slot for movie D:" + count);
        count = 0;

        for (String s : result) {
            if (s.contains("Movie E")) {
                System.out.println(s);
                count++;
            }
        }
        System.out.println("Slot for movie E:" + count);
        count = 0;

        result.forEach(System.out::println);
    }

}
