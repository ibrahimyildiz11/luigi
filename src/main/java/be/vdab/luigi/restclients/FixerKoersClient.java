package be.vdab.luigi.restclients;

import be.vdab.luigi.exceptions.KoersClientException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;

@Component
@Qualifier("Fixer")
class FixerKoersClient implements KoersClient {
    private static final Pattern PATTERN = Pattern.compile("^.*\"USD\": *(\\d+\\.?\\d*).*$");
    private final URL url;
    FixerKoersClient() {
        try {
            url = new URL (
                    "http://data.fixer.io/api/latest?symbols=USD&access_key=213bd583ad7b26793da0a08de8c60672");
        } catch (MalformedURLException ex) {
            throw new KoersClientException("Fixer URL is verkeerd.");
        }
    }

    @Override public BigDecimal getDollarKoers() {
        try(var stream = url.openStream()) {
            var matcher = PATTERN.matcher(new String(stream.readAllBytes()));
            if(!matcher.matches()) {
                throw new KoersClientException("Fixer data ongeldig");
            }
            return new BigDecimal(matcher.group(1));
        }catch (IOException ex) {
            throw new KoersClientException("Kan koers niet lezen via Fixer.", ex);
        }
    }
}
