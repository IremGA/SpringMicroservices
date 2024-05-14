package org.eaetirk.demo.elastic.model.index.util;

import org.springframework.data.elasticsearch.core.mapping.PropertyValueConverter;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class CustomZonedDateTimeConverter implements PropertyValueConverter {

    private final DateTimeFormatter formatterWithZone = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss.SSSX");
    private final DateTimeFormatter formatterWithoutZone = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss.SSS");

    private final DateTimeFormatter formatterWithoutTime = DateTimeFormatter.ofPattern("uuuu-MM-dd");

    @Override
    public Object write(Object value) {
        if (value instanceof ZonedDateTime zonedDateTime) {
            return formatterWithZone.format(zonedDateTime);
        } else {
            return value;
        }
    }

    @Override
    public Object read(Object value) {
        if (value instanceof String s) {
            try {
                return formatterWithZone.parse(s, ZonedDateTime::from);
            }catch (DateTimeParseException e){
                return formatterWithoutTime.parse(s, LocalDate::from).atStartOfDay(ZoneId.of("UTC"));
            }
        } else {
            return value;
        }
    }
}
