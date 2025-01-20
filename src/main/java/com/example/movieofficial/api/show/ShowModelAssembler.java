package com.example.movieofficial.api.show;

import com.example.movieofficial.api.bill.BillController;
import com.example.movieofficial.api.show.dtos.ShowDetail;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Service
public class ShowModelAssembler {

    public void linkToCreateBill(ShowDetail showDetail) {
        showDetail.add(linkTo(BillController.class)
                .withRel("create bill")
                .withType(HttpMethod.POST.name()));
    }
}
