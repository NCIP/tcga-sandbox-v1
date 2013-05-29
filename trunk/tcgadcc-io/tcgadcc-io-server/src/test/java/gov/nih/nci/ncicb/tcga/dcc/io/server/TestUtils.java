package gov.nih.nci.ncicb.tcga.dcc.io.server;

import io.netty.util.NetUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javolution.util.FastTable;

public class TestUtils {

    private static final List<Integer> PORTS          = new FastTable<Integer>();
    private static Iterator<Integer>   portIterator;

    private static final int           START_PORT     = 32768;
    private static final int           END_PORT       = 65536;
    private static final int           NUM_CANDIDATES = END_PORT - START_PORT;

    static {
        for (int i = START_PORT; i < END_PORT; i++) {
            PORTS.add(i);
        }

        Collections.shuffle(PORTS);
    }

    public static int getFreePort() {
        for (int i = 0; i < NUM_CANDIDATES; i++) {
            int port = nextCandidatePort();
            ServerSocket serverSocket = null;
            try {
                serverSocket = new ServerSocket();
                serverSocket.setReuseAddress(false);
                serverSocket.bind(new InetSocketAddress(port));
                serverSocket.close();

                serverSocket = new ServerSocket();
                serverSocket.setReuseAddress(false);
                serverSocket.bind(new InetSocketAddress(NetUtil.LOCALHOST, port));
                serverSocket.close();

                return port;
            }
            catch (IOException e) {
            }
        }

        throw new RuntimeException("unable to find a free port");
    }

    private static int nextCandidatePort() {
        if (portIterator == null || !portIterator.hasNext()) {
            portIterator = PORTS.iterator();
        }

        return portIterator.next();
    }

}
