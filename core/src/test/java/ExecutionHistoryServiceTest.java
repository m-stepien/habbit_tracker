import com.habit.tracker.core.dto.ExecutionHistoryDayDto;
import com.habit.tracker.core.dto.HabitDto;
import com.habit.tracker.core.entity.ExecutionHistoryEntity;
import com.habit.tracker.core.entity.HabitEntity;
import com.habit.tracker.core.enums.ExecutionState;
import com.habit.tracker.core.enums.HabitStatus;
import com.habit.tracker.core.exceptions.IncorrectDateException;
import com.habit.tracker.core.mapper.HabitMapper;
import com.habit.tracker.core.repository.ExecutionHistoryRepository;
import com.habit.tracker.core.service.ExecutionHistoryService;
import com.habit.tracker.core.service.HabitService;
import com.habit.tracker.core.service.PointsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@ExtendWith(MockitoExtension.class)
public class ExecutionHistoryServiceTest {

    @Mock
    HabitService habitService;

    @Mock
    ExecutionHistoryRepository executionHistoryRepository;
    @Mock
    HabitMapper habitMapper;
    @Mock
    PointsService pointsService;
    @InjectMocks
    ExecutionHistoryService executionHistoryService;


    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        setField(executionHistoryService, "numberOfEditableDay", 7);
    }

    private Object getPrivateField(Object object, String fieldName) throws Exception {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(object);
    }

    @Test
    void testMarkHabitInDaySuccess() throws IncorrectDateException, Exception {
        String userId = "test123";
        Long habitId = 2L;
        ExecutionState state = ExecutionState.DONE;
        LocalDate date = LocalDate.now();
        HabitEntity habit = new HabitEntity("habit123", 10, LocalDate.of(2024, 12, 12), 100);
        habit.setId(habitId);
        habit.setUserId(userId);
        habit.setStatus(HabitStatus.ACTIVE);
        habit.setDaysToMaster(30);
        doReturn(habit).when(habitService).getHabitById(anyLong());
        this.executionHistoryService.markHabitInDay(userId, habitId, state, date);
        verify(habitService, times(1)).getHabitById(habitId);
        ArgumentCaptor<ExecutionHistoryEntity> argumentCaptor = ArgumentCaptor.forClass(ExecutionHistoryEntity.class);
        verify(this.executionHistoryRepository).save(argumentCaptor.capture());
        ExecutionHistoryEntity results = argumentCaptor.getValue();
        assertNotNull(results);
        assertEquals(habitId, results.getHabit().getId());
        assertEquals(state, results.getExecutionState());
        assertEquals(date, results.getDate());
    }

    @Test
    void testMarkHabitInDayFailureDateNull() {
        String userId = "test123";
        Long habitId = 2L;
        ExecutionState state = ExecutionState.DONE;
        LocalDate date = null;
        HabitEntity habit = new HabitEntity("habit123", 10, LocalDate.of(2024, 12, 12), 100);
        habit.setId(habitId);
        habit.setUserId(userId);
        habit.setStatus(HabitStatus.ACTIVE);
        when(habitService.getHabitById(habitId)).thenReturn(habit);
        assertThrows(IncorrectDateException.class, () -> this.executionHistoryService.markHabitInDay(userId, habitId, state, date));
    }


    @Test
    void testMarkHabitInDayHabitNotActive() {
        String userId = "test123";
        Long habitId = 2L;
        ExecutionState state = ExecutionState.DONE;
        LocalDate date = LocalDate.now();
        HabitEntity habit = new HabitEntity("habit123", 10, LocalDate.of(2024, 12, 12), 100);
        habit.setId(habitId);
        habit.setUserId(userId);
        habit.setStatus(HabitStatus.INACTIVE);
        when(habitService.getHabitById(habitId)).thenReturn(habit);
        assertThrows(AccessDeniedException.class, () -> this.executionHistoryService.markHabitInDay(userId, habitId, state, date));
        verify(executionHistoryRepository, times(0)).save(any(ExecutionHistoryEntity.class));
    }

    @Test
    void testMarkHabitInDayHabitUserNotOwnHabit() {
        String userId = "test123";
        String otherUserId = "other321";
        Long habitId = 2L;
        ExecutionState state = ExecutionState.DONE;
        LocalDate date = LocalDate.now();
        HabitEntity habit = new HabitEntity("habit123", 10, LocalDate.of(2024, 12, 12), 100);
        habit.setId(habitId);
        habit.setUserId(userId);
        habit.setStatus(HabitStatus.ACTIVE);
        habit.setDaysToMaster(30);
        when(habitService.getHabitById(habitId)).thenReturn(habit);
        assertThrows(AccessDeniedException.class, () -> this.executionHistoryService.markHabitInDay(otherUserId, habitId, state, date));
        verify(executionHistoryRepository, times(0)).save(any(ExecutionHistoryEntity.class));
    }

    @Test
    void testMarkHabitInDayHabitExecutionAlreadyExistOnDay() throws IncorrectDateException {
        String userId = "test123";
        Long habitId = 2L;
        ExecutionState state = ExecutionState.DONE;
        LocalDate date = LocalDate.now().minusDays(2);
        HabitEntity habit = new HabitEntity("habit123", 10, LocalDate.of(2024, 12, 12), 100);
        habit.setId(habitId);
        habit.setUserId(userId);
        habit.setStatus(HabitStatus.ACTIVE);
        ExecutionHistoryEntity executionInDatabase = new ExecutionHistoryEntity();
        executionInDatabase.setHabit(habit);
        executionInDatabase.setExecutionState(ExecutionState.NOTDONEUNCHECKED);
        executionInDatabase.setDate(date);
        executionInDatabase.setId(3L);
        when(habitService.getHabitById(habitId)).thenReturn(habit);
        when(executionHistoryRepository.findByHabitIdAndDate(habitId, date)).thenReturn(Optional.of(executionInDatabase));
        this.executionHistoryService.markHabitInDay(userId, habitId, state, date);
        ArgumentCaptor<ExecutionHistoryEntity> argumentCaptor = ArgumentCaptor.forClass(ExecutionHistoryEntity.class);
        verify(executionHistoryRepository).save(argumentCaptor.capture());
        ExecutionHistoryEntity savedResults = argumentCaptor.getValue();
        executionInDatabase.setExecutionState(ExecutionState.DONE);
        assertEquals(executionInDatabase, savedResults);
    }


    @Test
    void markHabitBeforeAllowedDate() {
        HabitEntity habit = new HabitEntity();
        habit.setId(3L);
        habit.setPoints(10);
        habit.setUnlockCost(20);
        habit.setUserId("test123");
        habit.setPurchaseDate(LocalDate.of(2024, 12, 20));
        habit.setCreationDate(LocalDate.of(2024, 12, 20));
        habit.setName("Make bed");
        habit.setStatus(HabitStatus.ACTIVE);
        when(this.habitService.getHabitById(3L)).thenReturn(habit);
        assertThrows(IncorrectDateException.class, () -> this.executionHistoryService.markHabitInDay("test123", 3L, ExecutionState.DONE, LocalDate.now().minusDays(12)));

    }


    @Test
    void testEditDateOfExecutionInvalidNewDate() throws IncorrectDateException {
        HabitEntity habit = new HabitEntity();
        habit.setId(3L);
        habit.setPoints(10);
        habit.setUnlockCost(20);
        habit.setUserId("test123");
        habit.setPurchaseDate(LocalDate.of(2024, 12, 20));
        habit.setCreationDate(LocalDate.of(2024, 12, 20));
        habit.setName("Make bed");
        habit.setStatus(HabitStatus.ACTIVE);
        ExecutionHistoryEntity executionHistoryEntity = new ExecutionHistoryEntity();
        executionHistoryEntity.setExecutionState(ExecutionState.DONE);
        executionHistoryEntity.setId(3L);
        executionHistoryEntity.setDate(LocalDate.now());
        executionHistoryEntity.setHabit(habit);

        when(this.executionHistoryRepository.findById(3L)).thenReturn(Optional.of(executionHistoryEntity));
        this.executionHistoryService.editDateOfExecution("test123", 3L, LocalDate.now().minusDays(3));
        ArgumentCaptor<ExecutionHistoryEntity> argumentCaptor = ArgumentCaptor.forClass(ExecutionHistoryEntity.class);
        verify(executionHistoryRepository).save(argumentCaptor.capture());
        ExecutionHistoryEntity savedResults = argumentCaptor.getValue();
        executionHistoryEntity.setDate(LocalDate.now().minusDays(3));
        assertEquals(executionHistoryEntity, savedResults);
    }


    @Test
    void testEditDateOfExecutionToOldOldDate() {
        HabitEntity habit = new HabitEntity();
        habit.setId(3L);
        habit.setPoints(10);
        habit.setUnlockCost(20);
        habit.setUserId("test123");
        habit.setPurchaseDate(LocalDate.of(2024, 12, 20));
        habit.setCreationDate(LocalDate.of(2024, 12, 20));
        habit.setName("Make bed");
        habit.setStatus(HabitStatus.ACTIVE);
        ExecutionHistoryEntity executionHistoryEntity = new ExecutionHistoryEntity();
        executionHistoryEntity.setExecutionState(ExecutionState.DONE);
        executionHistoryEntity.setId(3L);
        executionHistoryEntity.setDate(LocalDate.now().minusDays(20));
        executionHistoryEntity.setHabit(habit);

        when(this.executionHistoryRepository.findById(3L)).thenReturn(Optional.of(executionHistoryEntity));
        assertThrows(IncorrectDateException.class, () -> this.executionHistoryService
                .editDateOfExecution("test123", 3L, LocalDate.now()));

    }


    @Test
    void testEditDateOfExecutionSuccess() {
        HabitEntity habit = new HabitEntity();
        habit.setId(3L);
        habit.setPoints(10);
        habit.setUnlockCost(20);
        habit.setUserId("test123");
        habit.setPurchaseDate(LocalDate.of(2024, 12, 20));
        habit.setCreationDate(LocalDate.of(2024, 12, 20));
        habit.setName("Make bed");
        habit.setStatus(HabitStatus.ACTIVE);
        ExecutionHistoryEntity executionHistoryEntity = new ExecutionHistoryEntity();
        executionHistoryEntity.setExecutionState(ExecutionState.DONE);
        executionHistoryEntity.setId(3L);
        executionHistoryEntity.setDate(LocalDate.now());
        executionHistoryEntity.setHabit(habit);

        when(this.executionHistoryRepository.findById(3L)).thenReturn(Optional.of(executionHistoryEntity));
        assertThrows(IncorrectDateException.class, () -> this.executionHistoryService
                .editDateOfExecution("test123", 3L, LocalDate.now().minusDays(20)));
    }


    @Test
    void getExecutionInDay() {
        String userId = "test123";
        LocalDate date = LocalDate.now();
        ExecutionHistoryEntity executionHistoryEntity = new ExecutionHistoryEntity();
        executionHistoryEntity.setDate(date);
        executionHistoryEntity.setId(3L);
        executionHistoryEntity.setExecutionState(ExecutionState.DONE);
        HabitEntity habit = new HabitEntity();
        habit.setId(2L);
        habit.setStatus(HabitStatus.ACTIVE);
        habit.setUserId(userId);
        habit.setCreationDate(LocalDate.now());
        habit.setPurchaseDate(LocalDate.now());
        habit.setPoints(30);
        habit.setUnlockCost(1000);
        habit.setName("Make bed");
        habit.setDaysToMaster(30);
        HabitDto habitDto = new HabitDto(habit.getId(), habit.getName(), habit.getPoints(), habit.getCreationDate(),
                habit.getUnlockCost(), habit.getStatus(), habit.getDaysToMaster(), habit.getRemainingDays());
        executionHistoryEntity.setHabit(habit);
        ExecutionHistoryDayDto result = new ExecutionHistoryDayDto(3L, habitDto, ExecutionState.DONE, date);
        when(habitMapper.toHabitDto(habit)).thenReturn(habitDto);
        when(this.executionHistoryRepository.findExecutionInDay(userId, date)).thenReturn(List.of(executionHistoryEntity));
        assertEquals(List.of(result), this.executionHistoryService.getExecutionInDay(userId, date));
        Mockito.verify(executionHistoryRepository, Mockito.times(1)).findExecutionInDay(userId, date);
    }
}
