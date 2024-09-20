package ca.jrvs.stockquote;

public class Main {
    public static void main(String[] args) {
        QuoteHttpHelper helper = new QuoteHttpHelper();
        System.out.println(
            helper.fetchQuoteInfo("TSLA")
        );
    }
}
