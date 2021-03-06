package data;

import io.atomix.catalyst.transport.Address;
import io.atomix.catalyst.transport.Transport;
import io.atomix.catalyst.transport.netty.NettyTransport;
import io.atomix.copycat.server.CopycatServer;
import io.atomix.copycat.server.cluster.Member;
import io.atomix.copycat.server.storage.Storage;
import io.atomix.copycat.server.storage.StorageLevel;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

/**
 * Created by gustavovm on 7/30/17.
 */
public class DataServer implements AutoCloseable {
    private Transport transport;
    private Address address;
    private CopycatServer.Builder builder;
    private CopycatServer server;
    private CompletableFuture<CopycatServer> future;
    private Collection<Address> cluster;

    public DataServer(String ip, int port){
        address = new Address(ip,port);
    }

    public boolean initDServer(int ThreadNum, String fileDir){
        this.transport = NettyTransport.builder().withThreads(ThreadNum).build();
        this.builder = CopycatServer.builder(address);
        this.builder.withStateMachine(SDDBStateMachine::new).withTransport(this.transport);
        this.builder.withStorage(Storage.builder().withDirectory(new File(fileDir)).withStorageLevel(StorageLevel.DISK).build());

        if(this.address.port() == SDDBServer.BASE_DATA_PORT) {
            cluster = Arrays.asList(
                    new Address("localhost", this.address.port() + 10),
                    new Address("localhost", this.address.port() + 20),
                    new Address("localhost", this.address.port() + 30));
        }
        //its possible to add a new cluster, just pass a new list to server.join(newClusterList).join();
        try{
            if(this.address.port() == SDDBServer.BASE_DATA_PORT) {
                this.server = this.builder.build();
                this.future = server.bootstrap(cluster);
                server.join(cluster).join();
                future.join();
                return true;
            }else{
                this.server = this.builder.build();
                this.future = server.bootstrap();
                future.join();
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void close() throws Exception {
        this.server.shutdown().join();
        this.transport.close();
    }
    public void killNode(){
        System.out.println("Lider");
        Member lider = server.cluster().leader();
        System.out.println(lider.address());
        server.cluster().members().forEach(member ->{
            System.out.println("Cluster "+member.address().host()+", "+member.address().port());
            //System.out.println(member.type());
        });
/*        Member membro = server.cluster().member();
        System.out.println(membro.type().toString());
        membro.remove().whenComplete((result,error)->{
            if(error == null){
                System.out.println("Cluster removido");
            }else{
                System.out.println("Erro ao remover cluster");
            }
        });
        server.cluster().onLeave(member ->{
            System.out.println(member.address()+ " left the cluster");
        });*//**//*
        server.cluster().member(new Address("localhost",this.address.port()+10)).remove();
        server.onStateChange(state -> {
           if(state == CopycatServer.State.LEADER){
               System.out.println("Leader changed");
           }else{
               System.out.println("Something changed");
           }
        });*/
    }
}
