package tests;

import manager.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;

class InMemoryTaskManagerTest extends TaskManagerTest {

    @BeforeEach
    public void createClass() {
//        given
        manager = new InMemoryTaskManager();
    }
}