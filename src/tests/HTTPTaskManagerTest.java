package tests;

import manager.Managers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import server.KVServer;

import java.io.IOException;
import java.net.URI;

class HTTPTaskManagerTest extends FileBackedTaskManagerTest {
    private KVServer server;

    @Override
    @BeforeEach
    public void createClass() throws IOException {
        server = new KVServer();
        server.start();
        manager = Managers.getDefault(URI.create("http://localhost:8078"));
    }

    @AfterEach
    public void closeServer() {
        server.stop();
    }
}