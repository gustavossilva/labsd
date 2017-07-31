package data;

import models.Operations;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportException;

import java.io.File;
import java.io.IOException;

/**
 * Created by gustavovm on 5/21/17.
 */
public class SDDBServer {
    final static int BASE_PORT = 9080;
    final static int BASE_DATA_PORT = 25000;
    public static void main(String [] args){
        DataServer data;
        final int N_SERVERS = (args.length > 0) ? Integer.parseInt(args[0]) : 3;

        if (args.length > 1) {
            final int ID = Integer.parseInt(args[1]);

            data = new DataServer("localhost",BASE_DATA_PORT + ID);
            data.initDServer(1,"logs");

            try (SDDBHandler handler = new SDDBHandler(ID, N_SERVERS)) {
                Operations.Processor processor = new Operations.Processor(handler);
                TServerTransport transport = new TServerSocket(BASE_PORT + ID);
                new TThreadPoolServer(new TThreadPoolServer.Args(transport).processor(processor)).serve();
            }

            catch (TTransportException e) {
                e.printStackTrace();
            }
        }

        else
            for (int i = 0; i < N_SERVERS; i++) {
                try {
                    ProcessBuilder b = new ProcessBuilder("java", "-jar", "Server.jar",
                                                          Integer.toString(N_SERVERS), Integer.toString(i));
                    b.redirectOutput(new File( String.format("server%d.log", i + 1) )).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
    }
}
