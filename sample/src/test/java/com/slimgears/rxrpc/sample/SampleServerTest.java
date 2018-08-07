package com.slimgears.rxrpc.sample;

import ch.qos.logback.classic.Level;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.slimgears.rxrpc.client.RxClient;
import com.slimgears.rxrpc.server.EndpointDispatchers;
import com.slimgears.rxrpc.server.EndpointResolver;
import com.slimgears.rxrpc.server.RxServer;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class SampleServerTest {
    private final static int port = 8000;
    private final static URI uri = URI.create("ws://localhost:" + port + "/socket/");

    @BeforeClass
    public static void init() {
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);
    }

    @Test
    public void testClientServer() throws ExecutionException, InterruptedException {
        SampleServer server = new SampleServer(port);
        SampleClient client = new SampleClient();

        RxServer rxServer = RxServer.forConfig(RxServer.Config
                .builder()
                .server(server)
                .resolver(new EndpointResolver() {
                    @Override
                    public <T> T resolve(Class<T> cls) {
                        try {
                            return cls.newInstance();
                        } catch (InstantiationException | IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    }
                })
                .dispatcherFactory(EndpointDispatchers
                        .compositeBuilder()
                        .add("sampleEndpoint", resolver -> EndpointDispatchers
                                .builder(SampleEndpoint::new)
                                .method("echoMethod", SampleEndpoint.echoMethod)
                                .build())
                        .buildFactory())
                .objectMapper(new ObjectMapper())
                .build());

        rxServer.start();

        RxClient rxClient = RxClient.forConfig(RxClient.Config
                .builder()
                .objectMapper(new ObjectMapper())
                .endpointFactory(new RxClient.EndpointFactory() {
                    @Override
                    public <T> T create(Class<T> clientClass, Future<RxClient.Session> session) {
                        try {
                            return clientClass.getConstructor(Future.class).newInstance(session);
                        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                            throw new RuntimeException(e);
                        }
                    }
                })
                .client(client)
                .build());

        SampleEndpointClient sampleEndpointClient = rxClient.connect(uri).resolve(SampleEndpointClient.class);
        String msgFromServer = sampleEndpointClient.echoMethod("Test").get();

        Assert.assertEquals("Server received from client: Test", msgFromServer);

        rxServer.stop();
    }
}