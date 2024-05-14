package org.eaetirk.demo.elastic.query.service.model.assembler;

import org.eaetirk.demo.elastic.model.index.impl.TwitterIndexModel;
import org.eaetirk.demo.elastic.query.service.api.ElasticDocumentController;
import org.eaetirk.demo.elastic.query.service.mapper.ElasticToResponseModelMapper;
import org.eaetirk.demo.elastic.query.service.model.ElasticQueryServiceResponseModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@Component
public class ElasticQueryServiceResponseModelAssembler extends RepresentationModelAssemblerSupport<TwitterIndexModel, ElasticQueryServiceResponseModel> {
    private final ElasticToResponseModelMapper elasticToResponseModelMapper;

    public ElasticQueryServiceResponseModelAssembler(ElasticToResponseModelMapper elasticToResponseModelMapper) {
        super(ElasticDocumentController.class, ElasticQueryServiceResponseModel.class);
        this.elasticToResponseModelMapper = elasticToResponseModelMapper;
    }


    @Override
    public ElasticQueryServiceResponseModel toModel(TwitterIndexModel twitterIndexModel) {
        ElasticQueryServiceResponseModel responseModel =
                elasticToResponseModelMapper.getResponseModel(twitterIndexModel);
        responseModel.add(
                linkTo(methodOn(ElasticDocumentController.class).getDocumentById(twitterIndexModel.getId())).withSelfRel());
        responseModel.add(linkTo(ElasticDocumentController.class).withRel("documents"));
        return responseModel;
    }

    public List<ElasticQueryServiceResponseModel> toModels(List<TwitterIndexModel> twitterIndexModelList) {
        return twitterIndexModelList.stream().map(this::toModel).collect(Collectors.toList());
    }
}
