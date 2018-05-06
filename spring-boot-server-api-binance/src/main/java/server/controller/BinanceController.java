package server.controller;


import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.market.TickerPrice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private static final String WARNING_NOT_INITIATED_FALLING_BACK_TO_DEFAULT = "WARNING: Not initiated. Falling back to default.";
    private static final String MESSAGE_FAILED_TO_INIT = "Failed to init";
    private static final String MESSAGE_INIT_SUCCESS = "Initialized successfully";

    private BinanceApiRestClient client;
    private List<String> warnings = new ArrayList<>();
    private boolean isInit = false;

    private static String getCurrentMethodName() {
        return Thread.currentThread().getStackTrace()[2].getMethodName();
    }

    @GetMapping("/init")
    public ResponseEntity<String> init(@RequestParam(PARAM_NAME_API_KEY) String apiKey, @RequestParam(PARAM_NAME_SECRET_KEY) String secretKey) {
        if (!doInit(apiKey, secretKey)) {
            createResponse(MESSAGE_FAILED_TO_INIT, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return createResponse(MESSAGE_INIT_SUCCESS, HttpStatus.OK);
    }

    @GetMapping("/getAllPrices")
    public ResponseEntity<String> getAllPrices() {
        if (!isInit) {
            warnings.add(WARNING_NOT_INITIATED_FALLING_BACK_TO_DEFAULT);
            if (!doInit(DEFAULT_API_KEY, DEFAULT_SECRET_KEY)) {
                createResponse(MESSAGE_FAILED_TO_INIT, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        String result;
        HttpStatus status;
        try {
            result = client.getAllPrices().toString();
            status = HttpStatus.OK;
        } catch (Exception e) {
           result =  "failed to " + getCurrentMethodName() + ".\n\n\n" + e.getMessage();
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return createResponse(result, status);
    }

    @GetMapping("/getAllUserPrices")
    public ResponseEntity<String> getAllUserPrices(@RequestParam(PARAM_NAME_API_KEY) String apiKey, @RequestParam(PARAM_NAME_SECRET_KEY) String secretKey) {
        if (!doInit(apiKey, secretKey)) {
            createResponse(MESSAGE_FAILED_TO_INIT, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        String result;
        HttpStatus status;
        try {
            result = client.getAllPrices().toString();
            status = HttpStatus.OK;
        } catch (Exception e) {
           result =  "failed to " + getCurrentMethodName() + ".\n\n\n" + e.getMessage();
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        isInit = false;
        return createResponse(result, status);
    }

    @GetMapping("/getTradesSymbol")
    public ResponseEntity<String> getTradesSymbol(@RequestParam("symbol") String symbol) {
        if (!isInit) {
            warnings.add(WARNING_NOT_INITIATED_FALLING_BACK_TO_DEFAULT);
            if (!doInit(DEFAULT_API_KEY, DEFAULT_SECRET_KEY)) {
                createResponse(MESSAGE_FAILED_TO_INIT, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        String result;
        HttpStatus status;
        try {
            result = client.getMyTrades(symbol).toString();
            status = HttpStatus.OK;
        } catch (Exception e) {
           result =  "failed to " + getCurrentMethodName() + ".\n\n\n" + e.getMessage();
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return createResponse(result, status);
    }

    @GetMapping("/getUserTradesSymbol")
    public ResponseEntity<String> getUserTradesSymbol(@RequestParam("symbol") String symbol, @RequestParam(PARAM_NAME_API_KEY) String apiKey, @RequestParam(PARAM_NAME_SECRET_KEY) String secretKey) {
        if (!doInit(apiKey, secretKey)) {
            createResponse(MESSAGE_FAILED_TO_INIT, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        String result;
        HttpStatus status;
        try {
            result = client.getMyTrades(symbol).toString();
            status = HttpStatus.OK;
            isInit = false;
        }
        catch (Exception e) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            result =  "failed to " + getCurrentMethodName() + ".\n\n\n" + e.getMessage();
        }
        return createResponse(result, status);
    }

    @GetMapping("/getPrice")
    public ResponseEntity<String> getPrice(@RequestParam("symbol") String symbol) {
        if (!isInit) {
            warnings.add(WARNING_NOT_INITIATED_FALLING_BACK_TO_DEFAULT);
            if (!doInit(DEFAULT_API_KEY, DEFAULT_SECRET_KEY)) {
                createResponse(MESSAGE_FAILED_TO_INIT, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        String result;
        HttpStatus status;
        try {
            if (symbol.toLowerCase().equals("all")) {
                result = client.getAllPrices().toString();
            } else {
                TickerPrice price;

                price = client.getPrice(symbol);
                if (price == null) {
                    result = "no such symbol!";
                } else {
                    result = price.toString();
                }
            }
            status = HttpStatus.OK;
        } catch (Exception e) {
           result =  "failed to " + getCurrentMethodName() + ".\n\n\n" + e.getMessage();
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }


        return createResponse(result, status);
    }

    @GetMapping("/supported")
    public ResponseEntity<String> supported() {
        if (!isInit) {
            warnings.add(WARNING_NOT_INITIATED_FALLING_BACK_TO_DEFAULT);
            if (!doInit(DEFAULT_API_KEY, DEFAULT_SECRET_KEY)) {
                createResponse(MESSAGE_FAILED_TO_INIT, HttpStatus.OK);
            }
        }
        List<TickerPrice> allPrices;
        String result;
        HttpStatus status;
        try {
            allPrices = client.getAllPrices();
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
            result = allSymbols.toString();
            status = HttpStatus.OK;
        } catch (Exception e) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            result =  "failed to " + getCurrentMethodName() + ".\n\n\n" + e.getMessage();
        }


        return createResponse(result, status);
    }

    private boolean doInit(String apiKey, String secretKey) {
        BinanceApiClientFactory binanceApiClientFactory = BinanceApiClientFactory.newInstance(apiKey, secretKey);
        client = binanceApiClientFactory.newRestClient();
        client.ping();//todo die if failed
        isInit = true;

        return true;
    }

    private ResponseEntity<String> createResponse(String result, HttpStatus httpStatus) {
        final String message = warnings.isEmpty() ? result : warnings.toString() + "\n\n" + result;
        warnings.clear();
        return ResponseEntity.status(httpStatus).body(message);
    }

}
