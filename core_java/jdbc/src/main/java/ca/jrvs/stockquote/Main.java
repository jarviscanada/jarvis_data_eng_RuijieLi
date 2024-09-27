package ca.jrvs.stockquote;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import ca.jrvs.stockquote.access.database.PositionDao;
import ca.jrvs.stockquote.access.database.QuoteDao;
import ca.jrvs.stockquote.access.httpexternalapi.QuoteHttpHelper;
import ca.jrvs.stockquote.controller.StockQuoteController;
import ca.jrvs.stockquote.service.PositionService;
import ca.jrvs.stockquote.service.QuoteService;
import okhttp3.OkHttpClient;

public class Main {
	public static void main(String[] args) {		
		Map<String, String> properties = new HashMap<>();
		try (BufferedReader br = new BufferedReader(new FileReader("src/main/resources/properties.txt"))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] tokens = line.split(":");
				properties.put(tokens[0], tokens[1]);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			Class.forName(properties.get("db-class"));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		OkHttpClient client = new OkHttpClient();
		String url = "jdbc:postgresql://"+properties.get("server")+":"+properties.get("port")+"/"+properties.get("database");
		try (Connection c = DriverManager.getConnection(url, properties.get("username"), properties.get("password"))) {
			QuoteDao qRepo = new QuoteDao(c);
			PositionDao pRepo = new PositionDao(c);
			QuoteHttpHelper rcon = new QuoteHttpHelper(properties.get("api-key-path"), client);
			QuoteService sQuote = new QuoteService(qRepo, rcon);
			PositionService sPos = new PositionService(pRepo, qRepo, sQuote);
			StockQuoteController con = new StockQuoteController(sQuote, sPos);
			con.initClient();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
