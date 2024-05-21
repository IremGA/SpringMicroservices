package org.eaetirk.demo.elastic.query.web.client.api;

import jakarta.validation.Valid;
import org.eaetirk.demo.elastic.query.web.client.model.ElasticQueryWebClientRequestModel;
import org.eaetirk.demo.elastic.query.web.client.model.ElasticQueryWebClientResponseModel;
import org.eaetirk.demo.elastic.query.web.client.service.ElasticQueryWebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

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
        model.addAttribute("elasticQueryWebClientRequestModel", ElasticQueryWebClientRequestModel.builder().build());
        return "home";
    }

    @GetMapping("/query-by-text")
    public String queryByText(@Valid ElasticQueryWebClientRequestModel elasticQueryWebClientRequestModel,
                              Model model){
        LOG.info("Querying with text {} ", elasticQueryWebClientRequestModel.getText());
        List<ElasticQueryWebClientResponseModel> responseModels = webClient.getDataByText(elasticQueryWebClientRequestModel);
        model.addAttribute("elasticQueryWebClientResponseModels", responseModels);
        model.addAttribute("searchText", elasticQueryWebClientRequestModel.getText());
        model.addAttribute("elasticQueryWebClientRequestModel", ElasticQueryWebClientRequestModel.builder().build());
        return "home";
    }



}
