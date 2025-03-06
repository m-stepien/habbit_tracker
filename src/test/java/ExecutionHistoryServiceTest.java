import com.habit.tracker.entity.ExecutionHistoryEntity;
import com.habit.tracker.entity.HabitEntity;
import com.habit.tracker.enums.ExecutionState;
import com.habit.tracker.enums.HabitStatus;
import com.habit.tracker.repository.ExecutionHistoryRepository;
import com.habit.tracker.service.ExecutionHistoryService;
import com.habit.tracker.service.HabitService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDate;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExecutionHistoryServiceTest {
    @Mock
    HabitService habitService;
    @Mock
    ExecutionHistoryRepository executionHistoryRepository;
    @InjectMocks
    ExecutionHistoryService executionHistoryService;

    @Test
    void testMarkHabitInDaySuccess(){
        String userId = "test123";
        Long habitId = 2L;
        ExecutionState state = ExecutionState.DONE;
        LocalDate date = LocalDate.of(2024,9,12);
        HabitEntity habit = new HabitEntity("habit123", 10, LocalDate.of(2024,12,12), 100);
        habit.setId(habitId);
        habit.setUserId(userId);
        habit.setStatus(HabitStatus.ACTIVE);
        when(habitService.getHabitById(habitId)).thenReturn(habit);
        this.executionHistoryService.markHabitInDay(userId, habitId, state, date);
        ArgumentCaptor<ExecutionHistoryEntity> argumentCaptor = ArgumentCaptor.forClass(ExecutionHistoryEntity.class);
        verify(this.executionHistoryRepository).save(argumentCaptor.capture());
        ExecutionHistoryEntity results = argumentCaptor.getValue();
        assertNotNull(results);
        assertEquals(habitId, results.getHabit().getId());
        assertEquals(state, results.getExecutionState());
        assertEquals(date, results.getDate());
    }

    @Test
    void testMarkHabitInDaySuccessDateNull(){
        String userId = "test123";
        Long habitId = 2L;
        ExecutionState state = ExecutionState.DONE;
        LocalDate date = null;
        HabitEntity habit = new HabitEntity("habit123", 10, LocalDate.of(2024,12,12), 100);
        habit.setId(habitId);
        habit.setUserId(userId);
        habit.setStatus(HabitStatus.ACTIVE);
        when(habitService.getHabitById(habitId)).thenReturn(habit);
        this.executionHistoryService.markHabitInDay(userId, habitId, state, date);
        ArgumentCaptor<ExecutionHistoryEntity> argumentCaptor = ArgumentCaptor.forClass(ExecutionHistoryEntity.class);
        verify(this.executionHistoryRepository).save(argumentCaptor.capture());
        ExecutionHistoryEntity results = argumentCaptor.getValue();
        assertNotNull(results);
        assertEquals(habitId, results.getHabit().getId());
        assertEquals(state, results.getExecutionState());
        assertEquals(LocalDate.now(), results.getDate());
    }


    @Test
    void testMarkHabitInDayHabitNotActive(){
        String userId = "test123";
        Long habitId = 2L;
        ExecutionState state = ExecutionState.DONE;
        LocalDate date = null;
        HabitEntity habit = new HabitEntity("habit123", 10, LocalDate.of(2024,12,12), 100);
        habit.setId(habitId);
        habit.setUserId(userId);
        habit.setStatus(HabitStatus.INACTIVE);
        when(habitService.getHabitById(habitId)).thenReturn(habit);
        assertThrows(AccessDeniedException.class, ()->this.executionHistoryService.markHabitInDay(userId, habitId, state, date));
        verify(executionHistoryRepository, times(0)).save(any(ExecutionHistoryEntity.class));
    }

    @Test
    void testMarkHabitInDayHabitUserNotOwnHabit(){
        String userId = "test123";
        String otherUserId ="other321";
        Long habitId = 2L;
        ExecutionState state = ExecutionState.DONE;
        LocalDate date = null;
        HabitEntity habit = new HabitEntity("habit123", 10, LocalDate.of(2024,12,12), 100);
        habit.setId(habitId);
        habit.setUserId(userId);
        habit.setStatus(HabitStatus.ACTIVE);
        when(habitService.getHabitById(habitId)).thenReturn(habit);
        assertThrows(AccessDeniedException.class, ()->this.executionHistoryService.markHabitInDay(otherUserId, habitId, state, date));
        verify(executionHistoryRepository, times(0)).save(any(ExecutionHistoryEntity.class));
    }
}
