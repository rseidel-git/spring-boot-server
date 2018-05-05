package server.controller;


import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.market.TickerPrice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("binance")
public class BinanceController {

    private static final String PARAM_NAME_API_KEY = "apiKey";
    private static final String PARAM_NAME_SECRET_KEY = "secretKey";

    private static final String DEFAULT_API_KEY = "s7wGM9XCoahDR1y2ffai2wvTmmgft8rEj4TTFhZXnG01ldy8LE79iB9yo4Zf1L1k";
    private static final String DEFAULT_SECRET_KEY = "EvnTtjbYFq1gqUKf10mOqWS8zhL4HloNoKzyEWeQZZWltDwGEgO4oH0ufeL0qSPw";
    public static final String WARNING_NOT_INITIATED_FALLING_BACK_TO_DEFAULT = "WARNING: Not initiated. Falling back to default.";

    private BinanceApiRestClient client;
    private List<String> warnings = new ArrayList<>();
    private boolean isInit = false;

    @GetMapping("/init")
    public String init(@RequestParam(PARAM_NAME_API_KEY) String apiKey, @RequestParam(PARAM_NAME_SECRET_KEY) String secretKey) {
        if (!doInit(apiKey, secretKey)) {
            createResponse("Failed to init :-(");
        }

        final String result = "Initialized successfully!";
        return createResponse(result);
    }

    @GetMapping("/getAllPrices")
    public String getAllPrices() {
        if (!isInit) {
            warnings.add(WARNING_NOT_INITIATED_FALLING_BACK_TO_DEFAULT);
            if (!doInit(DEFAULT_API_KEY, DEFAULT_SECRET_KEY)) {
                createResponse("Failed to init");
            }
        }

        final String result = client.getAllPrices().toString();
        return createResponse(result);
    }

    @GetMapping("/getAllPricesUser")
    public String getAllPricesUser(@RequestParam(PARAM_NAME_API_KEY) String apiKey, @RequestParam(PARAM_NAME_SECRET_KEY) String secretKey) {
        if (!doInit(apiKey, secretKey)) {
            createResponse("Failed to init");
        }

        final String result = client.getAllPrices().toString();
        isInit = false;
        return createResponse(result);
    }

    @GetMapping("/getTradesSymbol")
    public String getTradesSymbol(@RequestParam("symbol") String symbol) {
        if (!isInit) {
            warnings.add(WARNING_NOT_INITIATED_FALLING_BACK_TO_DEFAULT);
            if (!doInit(DEFAULT_API_KEY, DEFAULT_SECRET_KEY)) {
                createResponse("Failed to init");
            }
        }

        final String result = client.getMyTrades(symbol).toString();
        return createResponse(result);
    }

    @GetMapping("/getUserTradesSymbol")
    public String getUserTradesSymbol(@RequestParam("symbol") String symbol, @RequestParam(PARAM_NAME_API_KEY) String apiKey, @RequestParam(PARAM_NAME_SECRET_KEY) String secretKey) {
        if (!doInit(apiKey, secretKey)) {
            createResponse("Failed to init");
        }

        final String result = client.getMyTrades(symbol).toString();
        isInit = false;
        return createResponse(result);
    }

    @GetMapping("/getPrice")
    public String getPrice(@RequestParam("symbol") String symbol) {
        if (!isInit) {
            warnings.add(WARNING_NOT_INITIATED_FALLING_BACK_TO_DEFAULT);
            if (!doInit(DEFAULT_API_KEY, DEFAULT_SECRET_KEY)) {
                createResponse("Failed to init");
            }
        }

        String result;
        if (symbol.toLowerCase().equals("all")) {
            result = client.getAllPrices().toString();
        } else {
            TickerPrice price;
            try {
                price = client.getPrice(symbol);
            } catch (Exception e) {
                return "failed to get price!\n\n" + e.getMessage();
            }
            if (price == null) {
                result = "no such symbol!";
            } else {
                result = price.toString();
            }
        }

        return createResponse(result);
    }

    @GetMapping("/supported")
    public String supported() {
        if (!isInit) {
            warnings.add(WARNING_NOT_INITIATED_FALLING_BACK_TO_DEFAULT);
            if (!doInit(DEFAULT_API_KEY, DEFAULT_SECRET_KEY)) {
                createResponse("Failed to init");
            }
        }
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

    private boolean doInit(String apiKey, String secretKey) {
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance(apiKey, secretKey);
        client = factory.newRestClient();
        client.ping();//todo die if failed
        isInit = true;

        return true;
    }

    private String createResponse(String result) {
        final String message = warnings.isEmpty() ? result : warnings.toString() + "\n\n" + result;
        warnings.clear();
        return message;
    }

}
