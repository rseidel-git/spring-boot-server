package server.controller;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class ClientInfo {

    private Instant startTimeFrame;
    private int numOfRequestsInCurrentTimeFrame = 0;

    public ClientInfo() {
        this.startTimeFrame = startTimeFrame;
    }

    public Instant getStartTimeFrame() {
        return startTimeFrame;
    }


    public boolean request(Instant requestTime) {
        final Instant now = Instant.now();
        if (numOfRequestsInCurrentTimeFrame == 0) {
            numOfRequestsInCurrentTimeFrame++;
            startTimeFrame = now;
            return true;
        }
        else {
            if (numOfRequestsInCurrentTimeFrame == 5 || requestTime.minus(5, ChronoUnit.SECONDS).isAfter(startTimeFrame)) {
                numOfRequestsInCurrentTimeFrame = 0;
                return false;
            }
            else {
                numOfRequestsInCurrentTimeFrame++;
                return true;
            }
        }

    }

}
