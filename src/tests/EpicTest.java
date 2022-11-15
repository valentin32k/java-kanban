package tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;

class EpicTest {
    private final Epic testEpic = new Epic("Эпик 1", "Описание эпика 1", 1);

    @Test
    void epicStatusIsNew_WhenSubtaskListIsEmpty() {
        Assertions.assertEquals(Status.NEW, testEpic.getStatus());
    }

    @Test
    void epicStatusIsNew_WhenSubtasksStatusIsNew() {
//        given
        HashMap<Integer, Subtask> subtasksById = new HashMap<>();
        Subtask subtask1 = new Subtask("Подзадача 1.1", "Описание подзадачи 1.1", 1, 2, Status.NEW, LocalDateTime.now(), Duration.ofMinutes(601L));
        Subtask subtask2 = new Subtask("Подзадача 1.2", "Описание подзадачи 1.2", 1, 3, Status.NEW, LocalDateTime.now(), Duration.ofMinutes(601L));
        Subtask subtask3 = new Subtask("Подзадача 1.3", "Описание подзадачи 1.3", 1, 4, Status.NEW, LocalDateTime.now(), Duration.ofMinutes(601L));
        testEpic.addSubtaskId(subtask1.getId());
        testEpic.addSubtaskId(subtask2.getId());
        testEpic.addSubtaskId(subtask3.getId());
        subtasksById.put(subtask1.getId(), subtask1);
        subtasksById.put(subtask2.getId(), subtask2);
        subtasksById.put(subtask3.getId(), subtask3);
//        when
        testEpic.updateEpicStatus(subtasksById);
//        then
        Assertions.assertEquals(Status.NEW, testEpic.getStatus());
    }

    @Test
    void epicStatusIsDone_WhenSubtasksStatusIsDone() {
//        given
        HashMap<Integer, Subtask> subtasksById = new HashMap<>();
        Subtask subtask1 = new Subtask("Подзадача 1.1", "Описание подзадачи 1.1", 1, 2, Status.DONE, LocalDateTime.now(), Duration.ofMinutes(601L));
        Subtask subtask2 = new Subtask("Подзадача 1.2", "Описание подзадачи 1.2", 1, 3, Status.DONE, LocalDateTime.now(), Duration.ofMinutes(601L));
        Subtask subtask3 = new Subtask("Подзадача 1.3", "Описание подзадачи 1.3", 1, 4, Status.DONE, LocalDateTime.now(), Duration.ofMinutes(601L));
        testEpic.addSubtaskId(subtask1.getId());
        testEpic.addSubtaskId(subtask2.getId());
        testEpic.addSubtaskId(subtask3.getId());
        subtasksById.put(subtask1.getId(), subtask1);
        subtasksById.put(subtask2.getId(), subtask2);
        subtasksById.put(subtask3.getId(), subtask3);
//        when
        testEpic.updateEpicStatus(subtasksById);
//        then
        Assertions.assertEquals(Status.DONE, testEpic.getStatus());
    }

    @Test
    void epicStatusIsInProgress_WhenSubtasksStatusesIsNewAndDone() {
//        given
        HashMap<Integer, Subtask> subtasksById = new HashMap<>();
        Subtask subtask1 = new Subtask("Подзадача 1.1", "Описание подзадачи 1.1", 1, 2, Status.DONE, LocalDateTime.now(), Duration.ofMinutes(601L));
        Subtask subtask2 = new Subtask("Подзадача 1.2", "Описание подзадачи 1.2", 1, 3, Status.NEW, LocalDateTime.now(), Duration.ofMinutes(601L));
        Subtask subtask3 = new Subtask("Подзадача 1.3", "Описание подзадачи 1.3", 1, 4, Status.DONE, LocalDateTime.now(), Duration.ofMinutes(601L));
        testEpic.addSubtaskId(subtask1.getId());
        testEpic.addSubtaskId(subtask2.getId());
        testEpic.addSubtaskId(subtask3.getId());
        subtasksById.put(subtask1.getId(), subtask1);
        subtasksById.put(subtask2.getId(), subtask2);
        subtasksById.put(subtask3.getId(), subtask3);
//        when
        testEpic.updateEpicStatus(subtasksById);
//        then
        Assertions.assertEquals(Status.IN_PROGRESS, testEpic.getStatus());
    }

    @Test
    void epicStatusIsInProgress_WhenSubtasksStatusIsInProgress() {
//        given
        HashMap<Integer, Subtask> subtasksById = new HashMap<>();
        Subtask subtask1 = new Subtask("Подзадача 1.1", "Описание подзадачи 1.1", 1, 2, Status.IN_PROGRESS, LocalDateTime.now(), Duration.ofMinutes(601L));
        Subtask subtask2 = new Subtask("Подзадача 1.2", "Описание подзадачи 1.2", 1, 3, Status.IN_PROGRESS, LocalDateTime.now(), Duration.ofMinutes(601L));
        Subtask subtask3 = new Subtask("Подзадача 1.3", "Описание подзадачи 1.3", 1, 4, Status.IN_PROGRESS, LocalDateTime.now(), Duration.ofMinutes(601L));
        testEpic.addSubtaskId(subtask1.getId());
        testEpic.addSubtaskId(subtask2.getId());
        testEpic.addSubtaskId(subtask3.getId());
        subtasksById.put(subtask1.getId(), subtask1);
        subtasksById.put(subtask2.getId(), subtask2);
        subtasksById.put(subtask3.getId(), subtask3);
//        when
        testEpic.updateEpicStatus(subtasksById);
//        then
        Assertions.assertEquals(Status.IN_PROGRESS, testEpic.getStatus());
    }
}