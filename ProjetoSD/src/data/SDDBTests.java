package data;

import models.Operations;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

/**
 * Created by luiz on 26/06/17.
 */
public final class SDDBTests {
    private SDDBTests() {}

    public static void main(String[] args) {
        try (TTransport transport = new TSocket("localhost",SDDBServer.BASE_PORT)) {
            transport.open();

            Operations.Client client = new Operations.Client(new TBinaryProtocol(transport));

            try {
                assert client.criarVertice(0, 0, "A", 0,"") : "Vértice A";   // S1
                assert client.criarVertice(1, 0, "C", 0,"") : "Vértice C";   // S2
                assert client.criarVertice(2, 0, "E", 0,"") : "Vértice E";   // S3
                assert client.criarVertice(3, 0, "B", 0,"") : "Vértice B";   // S1
                assert client.criarVertice(4, 0, "F", 0,"") : "Vértice F";   // S3
                assert client.criarVertice(5, 0, "G", 0,"") : "Vértice G";   // S1
                assert client.criarVertice(8, 0, "D", 0,"") : "Vértice D";   // S2

                assert client.criarAresta(0, 3, 1, true, "A - B") : "Aresta A - B";
                assert client.criarAresta(0, 1, 2, true, "A - C") : "Aresta A - C";
                assert client.criarAresta(3, 8, 2, true, "B - D") : "Aresta B - D";
                assert client.criarAresta(1, 8, 1, true, "C - D") : "Aresta C - D";
                assert client.criarAresta(2, 3, 1, true, "E - B") : "Aresta E - B";
                assert client.criarAresta(1, 2, 1, true, "C - E") : "Aresta C - E";
                assert client.criarAresta(2, 4, 2, true, "E - F") : "Aresta E - F";
                assert client.criarAresta(4, 8, 1, true, "F - D") : "Aresta F - D";
                assert client.criarAresta(1, 5, 2, true, "C - G") : "Aresta C - G";
                assert client.criarAresta(2, 5, 2, true, "E - G") : "Aresta E - G";
            }

            catch (AssertionError e) { System.err.println(e); }
        }

        catch (TException e) { e.printStackTrace(System.err); }
    }
}
