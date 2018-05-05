package server.controller;


import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.market.TickerPrice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("binance")
public class BinanceController {

    private BinanceApiRestClient client;

    public BinanceController() {
        String apiKey = "s7wGM9XCoahDR1y2ffai2wvTmmgft8rEj4TTFhZXnG01ldy8LE79iB9yo4Zf1L1k";
        String secret = "EvnTtjbYFq1gqUKf10mOqWS8zhL4HloNoKzyEWeQZZWltDwGEgO4oH0ufeL0qSPw";
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance(apiKey, secret);
        client = factory.newRestClient();
        client.ping();//todo die if failed
    }

    @GetMapping("/")
    public String price(@RequestParam("symbol") String coin) {
        if (coin == null) {
            return "failed to get price! Bad request";
        }
        if (coin.toLowerCase().equals("all")) {
            return client.getAllPrices().toString();
        }
        else {
            TickerPrice price;
            try {
                price = client.getPrice(coin);
            } catch (Exception e) {
                return "failed to get price!\n\n" + e.getMessage();
            }
            if (price == null) {
                return "no such coin!";
            } else {
                return price.toString();
            }

        }
    }

    @GetMapping("/supported")
    public String supported() {
        List<TickerPrice> allPrices = client.getAllPrices();
        Set<String> allSymbols = new HashSet<>();
        for (TickerPrice price : allPrices) {
            String symbol = price.getSymbol();
            try {
                allSymbols.add(symbol.substring(0, 3));
            } catch (Exception ignored) {}
            try {
                allSymbols.add(symbol.substring(0, 4));
            } catch (Exception ignored) {}
            try {
                allSymbols.add(symbol.substring(symbol.length() - 3));
            } catch (Exception ignored) {}
            try {
                allSymbols.add(symbol.substring(symbol.length() - 4));
            } catch (Exception ignored) {}
        }

        return allSymbols.toString();
    }

}
