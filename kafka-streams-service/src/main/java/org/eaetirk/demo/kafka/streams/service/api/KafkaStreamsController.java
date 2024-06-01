package org.eaetirk.demo.kafka.streams.service.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.NotEmpty;
import org.eaetirk.demo.kafka.streams.service.model.KafkaStreamsResponseModel;
import org.eaetirk.demo.kafka.streams.service.runner.StreamsRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/", produces = "application/vndi.api.v1+json")
public class KafkaStreamsController {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaStreamsController.class);
    private final StreamsRunner<String, Long> kafkaStreamsRunner;

    public KafkaStreamsController(StreamsRunner<String, Long> kafkaStreamsRunner) {
        this.kafkaStreamsRunner = kafkaStreamsRunner;
    }

    @PreAuthorize("hasRole('APP_USER_ROLE')")
    @GetMapping("get-word-count-by-word/{word}")
    @Operation(summary = "Get Word Count By Word")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = {
                    @Content(mediaType = "application/vnd.api.v1+json",
                    schema = @Schema(implementation = KafkaStreamsResponseModel.class))
            }),
            @ApiResponse(responseCode = "400", description = "Not Found"),
            @ApiResponse(responseCode = "500", description = "Unexpected Server Error")
    })
    public @ResponseBody
    ResponseEntity<KafkaStreamsResponseModel> getWordCountByWord(
            @PathVariable @NotEmpty String word){
        Long wordCount = kafkaStreamsRunner.getValueByKey(word);
        LOG.info("Word count {} returned for word {}", wordCount, word);
        return ResponseEntity.ok(
                KafkaStreamsResponseModel
                        .builder()
                        .word(word)
                        .wordCount(wordCount)
                        .build());
    }
}
