package com.example.movieofficial;


import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Log4j2
public class MovieOfficialApplication {
    public static void main(String[] args) {
        SpringApplication.run(MovieOfficialApplication.class, args);
    }


//    @Bean
//    @Transactional
//    CommandLineRunner initSeat(SeatRepository seatRepository, HallRepository hallRepository, SeatTypeRepository seatTypeRepository) {
//        return args -> {
//            var normal = seatTypeRepository.findById(1).get();
//            var empty = seatTypeRepository.findById(3).get();
//            var halls = hallRepository.findAll(Sort.by("id"));
//            System.out.println("Start init seats");
//            halls.forEach(hall -> {
//                List<Seat> seats = new ArrayList<>();
//                for (int i = 1; i <= hall.getNumberOfRows(); i++) {
//                    System.out.println("Start init seats for row: " + mapNumberToChar(i));
//                    char c = mapNumberToChar(i);
//                    var s = 1;
//                    for (int j = 1; j <= hall.getColsPerRow(); j++) {
//                        String name = "" + c + s;
//                        System.out.println("Seat name: " + name);
//                        var seat = Seat.builder()
//                                .name((j != 17 && j != 18) ? name : null)
//                                .currRow(i)
//                                .currCol(j)
//                                .hall(hall) // `hall` phải được quản lý
//                                .type((j != 17 && j != 18) ? normal : empty) // `type` phải được quản lý
//                                .build();
//                        seat.setCreateBy("35000000-0000-0000-0000-000000000000");
//                        seats.add(seat);
//                        if (j != 17 && j != 18) {
//                            s++;
//                        }
//                    }
//                }
//                seatRepository.saveAll(seats); // Hibernate tự xử lý merge/persist
//            });
//            System.out.println("End init seats");
//        };
//    }

//    public int mapCharToNumber(char character) {
//        if (character >= 'A' && character <= 'Z') {
//            return character - 'A';
//        } else {
//            throw new IllegalArgumentException("Character must be between A and Z");
//        }
//    }
//
//    public char mapNumberToChar(int number) {
//        number = number - 1;
//        if (number >= 0 && number <= 25) {
//            return (char) ('A' + number);
//        } else {
//            throw new IllegalArgumentException("Number must be between 0 and 25");
//        }
//    }

}
