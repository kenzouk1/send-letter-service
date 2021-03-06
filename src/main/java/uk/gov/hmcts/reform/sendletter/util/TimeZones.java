package uk.gov.hmcts.reform.sendletter.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public final class TimeZones {

    public static final String EUROPE_LONDON = "Europe/London";
    public static final String UTC = "UTC";

    private TimeZones() {
        // utility class construct
    }

    public static LocalDateTime localDateTimeWithUtc(LocalDate date, LocalTime time) {
        ZonedDateTime zonedDateTime = ZonedDateTime.of(date, time, ZoneId.from(ZoneOffset.UTC));
        return zonedDateTime.toLocalDateTime();
    }

    public static Instant getCurrentEuropeLondonInstant() {
        return LocalDateTime
            .now()
            .atZone(ZoneId.of(EUROPE_LONDON))
            .toInstant();
    }
}
