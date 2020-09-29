package uk.gov.hmcts.reform.sendletter;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.sendletter.model.out.StaleLetter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.stream.IntStream;

import static com.google.common.io.Resources.getResource;
import static org.assertj.core.api.Assertions.assertThat;

public class StaleLetterJsonParserToCsv {

    public static final String NEW_LINE = "\n";

    @Test
    void pareStaleData() throws IOException, JSONException {
        String response = Resources.toString(getResource("stale-letters.json"), Charsets.UTF_8);

        JSONObject jsonObject = new JSONObject(response);
        String total = jsonObject.getString("count");

        JSONArray staleLetters = jsonObject.getJSONArray("stale_letters");

        StringBuilder output = new StringBuilder("Letter count :");
        output.append(total);
        output.append(NEW_LINE);
        output.append(String.join(",","id","status","service","created","sentToPrint"));
        output.append(NEW_LINE);

        StaleLetter staleLetter = getStaleLetter(staleLetters.getJSONObject(0));
        assertThat(staleLetter).isNotNull();

        IntStream.range(0, staleLetters.length())
                .mapToObj(counter -> getJsonObject(staleLetters, counter))
                .sorted(this::reverseCompare)
                .map(this::getStaleRecord)
                .forEach(output::append);
        String file = String.join("", "stale-letters-", LocalDateTime.now().toString(), ".csv");
        Files.write(Paths.get(file), output.toString().getBytes());
        Path destination = Path.of(file);
        assertThat(destination).isNotNull();
        assertThat(destination.toFile().exists()).isEqualTo(true);
        //Comment below line to check the CSV file
        assertThat(destination.toFile().delete()).isEqualTo(true);
    }


    private JSONObject getJsonObject(JSONArray staleLetters, int counter) {
        JSONObject staleLetter = null;
        try {
            staleLetter = staleLetters.getJSONObject(counter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return staleLetter;
    }

    private StaleLetter getStaleLetter(JSONObject jsonObject) throws JSONException {
        return new StaleLetter(UUID.fromString(jsonObject.getString("id")),
                jsonObject.getString("status"),
                jsonObject.getString("service"),
                LocalDateTime.parse(jsonObject.getString("created_at"), DateTimeFormatter.ISO_DATE_TIME),
                LocalDateTime.parse(jsonObject.getString("sent_to_print_at"), DateTimeFormatter.ISO_DATE_TIME));

    }

    private int reverseCompare(JSONObject first, JSONObject second) {
        try {
            return getLocalDateTime(second.getString("created_at"))
                    .compareTo(getLocalDateTime(first.getString("created_at")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private LocalDateTime getLocalDateTime(String createdAt) {
        return LocalDateTime.parse(createdAt, DateTimeFormatter.ISO_DATE_TIME);
    }

    private String getStaleRecord(JSONObject jsonObject) {
        StringBuilder data = new StringBuilder();
        try {
            data.append(String.join(",",
                jsonObject.getString("id"),
                jsonObject.getString("status"),
                jsonObject.getString("service"),
                jsonObject.getString("created_at"),
                jsonObject.getString("sent_to_print_at")));
            data.append(NEW_LINE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data.toString();
    }
}
