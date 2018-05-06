package server.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("")
public class HomeController {

    private static final String PARAM_NAME_CLIENT_ID = "clientId";
    private Map<String, ClientInfo> clients = new HashMap<>();

    @GetMapping("/")
    public ResponseEntity<Void> home(@RequestParam(PARAM_NAME_CLIENT_ID) int clientId) {

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private class ServerTask implements Runnable {

        private final long clientId;

        private ServerTask(long clientId) {
            this.clientId = clientId;
        }

        @Override
        public void run() {
            final ClientInfo clientInfo = clients.get(clientId);
            clientInfo.getStartTimeFrame();
            clientInfo.getStartTimeFrame();
        }
    }
}
