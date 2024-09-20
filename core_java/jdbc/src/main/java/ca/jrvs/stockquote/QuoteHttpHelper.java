package ca.jrvs.stockquote;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
// import java.nio.file.Path;
import java.nio.file.Paths;
import java.net.URI;
import okhttp3.OkHttpClient;
import java.util.Map;

import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuoteHttpHelper {
    private String apiKey;
    private OkHttpClient client;
    Logger logger;

    QuoteHttpHelper() {
        String apiKeyPath = "/home/ruijieli/Desktop/jrvs_api_key";
        logger = LoggerFactory.getLogger(QuoteHttpHelper.class);
        BasicConfigurator.configure();
        try {
            byte[] keyBytes = Files.readAllBytes(Paths.get(apiKeyPath));
            this.apiKey = new String(keyBytes, StandardCharsets.UTF_8).strip();
        } catch(IOException e) {
            logger.error("IOException while reading file " + apiKeyPath, e);
        }
        this.client = new OkHttpClient();
    }

	public Quote fetchQuoteInfo(String symbol) throws IllegalArgumentException {
        final String link = "https://alpha-vantage.p.rapidapi.com/query?function=GLOBAL_QUOTE&symbol="+symbol+"&datatype=json";
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(link))
            .header("X-RapidAPI-Key", this.apiKey)
            .header("X-RapidAPI-Host", "alpha-vantage.p.rapidapi.com")
            .method("GET", HttpRequest.BodyPublishers.noBody())
            .build();
        String responseStr = null;
        try {
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            ObjectMapper objectMapper = new ObjectMapper();
            responseStr = response.body();
            String innerJson = objectMapper.writeValueAsString(
                objectMapper
                    .readValue(responseStr, Map.class)
                    .get("Global Quote")
            );
            return objectMapper.readValue(innerJson, Quote.class);
        } catch (InterruptedException e) {
            logger.error("Process was interrupted while getting quote for " + symbol + " from link " + link, e);
            throw new RuntimeException(e);
        } catch (JsonMappingException e) {
            logger.error("JSON Mapping error while processing " + responseStr, e);
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            logger.error("JSON Processing Exception while processing " + responseStr, e);
            throw new RuntimeException(e);
        } catch (IOException e) {
            logger.error("IO Exception while fetching " + symbol + " from link " + link, e);
            throw new RuntimeException(e);
        }
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public OkHttpClient getClient() {
        return client;
    }

    public void setClient(OkHttpClient client) {
        this.client = client;
    }

}
