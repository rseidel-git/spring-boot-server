package server.controller;

import java.time.Instant;

public class ClientInfo {

    private Instant startTimeFrame;
    private int numOfRequestsInCurrentTimeFrame = 0;

    public Instant getStartTimeFrame() {
        return startTimeFrame;
    }

    public void setStartTimeFrame(Instant startTimeFrame) {
        this.startTimeFrame = startTimeFrame;
    }

    
}
