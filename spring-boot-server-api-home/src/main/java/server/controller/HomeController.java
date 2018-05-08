package server.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sun.misc.Contended;

import javax.annotation.PreDestroy;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.*;

@RestController
@RequestMapping("")
public class HomeController {

    private ExecutorService executorService = Executors.newFixedThreadPool(5);
    private static final String PARAM_NAME_CLIENT_ID = "clientId";

    @Contended
    private Map<Long, ClientInfo> clients = new ConcurrentHashMap<>();

    @GetMapping("/")
    public ResponseEntity<Void> home(@RequestParam(PARAM_NAME_CLIENT_ID) long clientId) {

        final Future<Boolean> future = executorService.submit(new ServerTask(clientId));
        try {
            if (!future.get(10, TimeUnit.SECONDS)) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreDestroy
    private void destroy() throws InterruptedException {
        executorService.shutdown();
        executorService.awaitTermination(5, TimeUnit.SECONDS);
        executorService.shutdownNow();
    }

    private class ServerTask implements Callable<Boolean> {

        private final long clientId;

        private ServerTask(long clientId) {
            this.clientId = clientId;
        }
        @Override
        public Boolean call() {
            final Instant now = Instant.now();
            clients.putIfAbsent(clientId, new ClientInfo());
            final ClientInfo clientInfo = clients.get(clientId);
            return clientInfo.request(now);
        }
    }
}
