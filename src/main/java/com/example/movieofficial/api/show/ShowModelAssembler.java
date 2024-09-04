package com.example.movieofficial.api.show;

import com.example.movieofficial.api.bill.BillController;
import com.example.movieofficial.api.cinema.dtos.CinemaDetail;
import com.example.movieofficial.api.movie.MovieController;
import com.example.movieofficial.api.movie.dtos.MovieDetail;
import com.example.movieofficial.api.show.dtos.ShowDetail;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class ShowModelAssembler {

    public void linkToCreateBill(ShowDetail showDetail) {
        showDetail.add(linkTo(BillController.class)
                .withRel("create bill")
                .withType(HttpMethod.POST.name()));
    }
}
