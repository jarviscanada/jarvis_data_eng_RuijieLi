package ca.jrvs.stockquote.access.httpexternalapi;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ca.jrvs.stockquote.access.database.Quote;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
// import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.Instant;
import java.net.URI;
import okhttp3.OkHttpClient;
import java.util.Map;

import org.apache.log4j.Logger;
// import org.slf4j.LoggerFactory;

import ca.jrvs.stockquote.util.StackTraceUtil;

public class QuoteHttpHelper {
    private String apiKey;
    private OkHttpClient client;
    private static Logger logger;

    public QuoteHttpHelper() {
        String apiKeyPath = "/home/ruijieli/Desktop/jrvs_api_key";
        logger = Logger.getLogger(QuoteHttpHelper.class);
        // BasicConfigurator.configure();
        try {
            byte[] keyBytes = Files.readAllBytes(Paths.get(apiKeyPath));
            this.apiKey = new String(keyBytes, StandardCharsets.UTF_8).strip();
        } catch(IOException e) {
            logger.error("IOException while reading file " + apiKeyPath, e);
        }
        this.client = new OkHttpClient();
        logger.info("Initialized QuoteHttpHelper");
    }

    public QuoteHttpHelper(String apikey, OkHttpClient client) {
        logger = Logger.getLogger(QuoteHttpHelper.class);
        // BasicConfigurator.configure();
        this.apiKey = apikey;
        this.client = client;
        logger.info("Initialized QuoteHttpHelper");
    }

    public Quote fetchQuoteInfo(String symbol) throws IllegalArgumentException {
        final String link = "https://alpha-vantage.p.rapidapi.com/query?function=GLOBAL_QUOTE&symbol="+symbol+"&datatype=json";
        logger.info("Requesting: " + link);
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
            @SuppressWarnings("unchecked")
            Map<String,String> innerMap = (Map<String, String>)objectMapper
                .readValue(responseStr, Map.class)
                .get("Global Quote");
            logger.info("Request returned response with status code " + response.statusCode() + "\n" + response.headers() + "\n" + response.body());

            if(innerMap.get("01. symbol") == null || innerMap.get("01. symbol").equals("null")) {
                logger.info("Quote is null. Returning null.");
                return null;
            }
            String innerJson = objectMapper.writeValueAsString(
                innerMap
            );
            Quote quote = objectMapper.readValue(innerJson, Quote.class);
            logger.info("Quote returned successfully.");
            quote.setTimestamp(new Timestamp(Instant.now().toEpochMilli()));
            return quote;
        } catch (InterruptedException e) {
            logger.error("Process was interrupted while getting quote for " + symbol + " from link " + link + ":\n" + StackTraceUtil.getStackTrace(e));
            throw new RuntimeException(e);
        } catch (JsonMappingException e) {
            logger.error("JSON Mapping error while processing " + responseStr + ":\n" + StackTraceUtil.getStackTrace(e));
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            logger.error("JSON Processing Exception while processing " + responseStr + ":\n" + StackTraceUtil.getStackTrace(e));
            throw new RuntimeException(e);
        } catch (IOException e) {
            logger.error("IO Exception while fetching " + symbol + " from link " + link + ":\n" + StackTraceUtil.getStackTrace(e));
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
