package ca.jrvs.exercise.json;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;

// import java.net.http.HttpRequest;
// import com.google.api.client.http.HttpRequest;

public class AlphaVantageAPI {
    AlphaVantageAPI() {}
    public static void main(String[] args) {
        final String symbol = "MSFT";
        final String API_KEY_PATH = "/home/ruijieli/Desktop/jrvs_api_key";
        File file = new File(API_KEY_PATH);
        AlphaVantageAPI a = new AlphaVantageAPI();
        try {
            String apiKey = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
            apiKey = apiKey.strip();
            a.testHTTP(apiKey, symbol);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    public void testHTTP(String apiKey, String symbol) {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://alpha-vantage.p.rapidapi.com/query?function=GLOBAL_QUOTE&symbol="+symbol+"&datatype=json"))
            .header("X-RapidAPI-Key", apiKey)
            .header("X-RapidAPI-Host", "alpha-vantage.p.rapidapi.com")
            .method("GET", HttpRequest.BodyPublishers.noBody())
            .build();
        try {
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            // System.out.println("============= resp. body =============");
            // System.out.println(response.body());
            ObjectMapper objectMapper = new ObjectMapper();
            String responseStr = response.body();
            System.out.println(responseStr);
            String innerJson = objectMapper.writeValueAsString(
                objectMapper
                    .readValue(responseStr, Map.class)
                    .get("Global Quote")
            );
            // System.out.println(objectMapper.readValue(responseStr, Map.class).get("Global Quote").toString());
            Quote quote = objectMapper.readValue(innerJson, Quote.class);
            System.out.println(quote);
            // System.out.println("============= resp. body =============");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } 
    }
}
