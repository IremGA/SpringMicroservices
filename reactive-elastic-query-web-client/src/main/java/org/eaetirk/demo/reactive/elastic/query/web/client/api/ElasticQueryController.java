package org.eaetirk.demo.reactive.elastic.query.web.client.api;

import jakarta.validation.Valid;
import org.eaetirk.demo.reactive.elastic.query.web.client.model.ElasticQueryWebClientRequestModel;
import org.eaetirk.demo.reactive.elastic.query.web.client.model.ElasticQueryWebClientResponseModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import org.eaetirk.demo.reactive.elastic.query.web.client.service.ElasticQueryWebClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.thymeleaf.spring5.context.webflux.IReactiveDataDriverContextVariable;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Flux;

@Controller
public class ElasticQueryController {
    private  static final Logger LOG = LoggerFactory.getLogger(ElasticQueryController.class);
    private final ElasticQueryWebClient webClient;

    public ElasticQueryController(ElasticQueryWebClient webClient) {
        this.webClient = webClient;
    }

    @GetMapping("")
    public String index(){
        return "index";
    }

    @GetMapping("/error")
    public String error(){
        return "error";
    }

    @GetMapping("/home")
    public String home(Model model){
        model.addAttribute("elasticQueryClientRequestModel", ElasticQueryWebClientRequestModel.builder().build());
        return "home";
    }

    @PostMapping("/query-by-text")
    public String queryByText(@Valid ElasticQueryWebClientRequestModel elasticQueryClientRequestModel,
                              Model model){
        LOG.info("Querying with text {} ", elasticQueryClientRequestModel.getText());
        Flux<ElasticQueryWebClientResponseModel> responseModels = webClient.getDataByText(elasticQueryClientRequestModel);
        IReactiveDataDriverContextVariable reactiveVar =
                new ReactiveDataDriverContextVariable(responseModels, 1);
        model.addAttribute("elasticQueryClientResponseModels", reactiveVar);
        model.addAttribute("searchText", elasticQueryClientRequestModel.getText());
        model.addAttribute("elasticQueryClientRequestModel", ElasticQueryWebClientRequestModel.builder().build());
        LOG.info("Returning from reactive client controller for text {} ! ", elasticQueryClientRequestModel.getText());
        return "home";
    }

}
