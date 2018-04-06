package uk.gov.hmcts.reform.sendletter.logging;

import com.microsoft.applicationinsights.TelemetryClient;
import com.microsoft.applicationinsights.telemetry.Duration;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.logging.appinsights.AbstractAppInsights;
import uk.gov.hmcts.reform.sendletter.entity.Letter;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Component
public class AppInsights extends AbstractAppInsights {

    static final String LETTER_NOT_PRINTED = "LetterNotPrinted";

    static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

    public AppInsights(TelemetryClient telemetry) {
        super(telemetry);
    }

    // dependencies

    public void trackServiceAuthentication(java.time.Duration duration, boolean success) {
        telemetry.trackDependency(
            AppDependency.AUTH_SERVICE,
            AppDependencyCommand.AUTH_SERVICE_HEADER,
            new Duration(duration.toMillis()),
            success
        );
    }

    public void trackFtpUpload(java.time.Duration duration, boolean success) {
        telemetry.trackDependency(
            AppDependency.FTP_CLIENT,
            AppDependencyCommand.FTP_FILE_UPLOADED,
            new Duration(duration.toMillis()),
            success
        );
    }

    public void trackFtpReportsDownload(java.time.Duration duration, boolean success) {
        telemetry.trackDependency(
            AppDependency.FTP_CLIENT,
            AppDependencyCommand.FTP_DOWNLOAD_REPORTS,
            new Duration(duration.toMillis()),
            success
        );
    }

    public void trackFtpReportDelete(java.time.Duration duration, boolean success) {
        telemetry.trackDependency(
            AppDependency.FTP_CLIENT,
            AppDependencyCommand.FTP_REPORT_DELETE,
            new Duration(duration.toMillis()),
            success
        );
    }

    // events

    public void trackStaleLetter(Letter staleLetter) {
        LocalDateTime sentToPrint = LocalDateTime.ofInstant(staleLetter.getSentToPrintAt().toInstant(), ZoneOffset.UTC);
        Map<String, String> properties = new HashMap<>();

        properties.put("letterId", staleLetter.getId().toString());
        properties.put("messageId", staleLetter.getMessageId());
        properties.put("service", staleLetter.getService());
        properties.put("type", staleLetter.getType());
        properties.put("sentToPrintDayOfWeek", sentToPrint.getDayOfWeek().name());
        properties.put("sentToPrintAt", sentToPrint.format(TIME_FORMAT));

        telemetry.trackEvent(LETTER_NOT_PRINTED, properties, null);
    }

    public void trackException(Exception exception) {
        telemetry.trackException(exception);
    }
}
