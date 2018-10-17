package com.example.htw.currencyconverter.network.ConnectivityCheckerUtil;

import com.example.htw.currencyconverter.callback.OnlineChecker;

import java.io.IOException;

public class ExperimentalOnlineChecker implements OnlineChecker {

    private final Runtime runtime;

    public ExperimentalOnlineChecker(Runtime runtime) {
        this.runtime = runtime;
    }

    @Override
    public boolean isOnline() {
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        }
        catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }

        return false;
    }
}