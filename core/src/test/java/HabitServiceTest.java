import com.habit.tracker.dto.HabitDto;
import com.habit.tracker.entity.ExecutionDaysEntity;
import com.habit.tracker.entity.HabitEntity;
import com.habit.tracker.enums.ExecutionDayOption;
import com.habit.tracker.enums.HabitStatus;
import com.habit.tracker.mapper.HabitMapper;
import com.habit.tracker.repository.ExecutionDayRepository;
import com.habit.tracker.repository.HabitRepository;
import com.habit.tracker.service.HabitService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class HabitServiceTest {
    @Mock
    HabitRepository habitRepository;
    @Mock
    HabitMapper habitMapper;
    @Mock
    ExecutionDayRepository executionDayRepository;

    @InjectMocks
    HabitService habitService;

    @Test
    void saveUserHabit(){
        HabitDto habitDto = new HabitDto(1L,"test123", 13, LocalDate.now(), 20, HabitStatus.INACTIVE);
        HabitEntity habitEntity = new HabitEntity("user1", "test123", 13, LocalDate.now(), 20);
        HabitEntity habitEntity1 = new HabitEntity(habitEntity);
        habitEntity1.setId(1L);
        when(habitRepository.save(any(HabitEntity.class))).thenReturn(habitEntity1);
        when(habitMapper.toHabitEntity(any(HabitDto.class))).thenReturn(habitEntity);
        this.habitService.saveUserHabit("user1", habitDto);
        Mockito.verify(habitRepository, Mockito.times(1)).save(any(HabitEntity.class));
        Mockito.verify(habitMapper, Mockito.times(1)).toHabitEntity(any(HabitDto.class));
    }

    @Test
    void fetchActiveHabitInDayTestSuccess(){
        String userId = "test123";
        ExecutionDayOption executionDayOption = ExecutionDayOption.MONDAY;
        HabitEntity habitEntity = new HabitEntity(userId, "habit1", 20, LocalDate.now(), 100);
        habitEntity.setStatus(HabitStatus.ACTIVE);
        habitEntity.setId(2L);
        HabitDto result = new HabitDto(2L, "habit1", 20, LocalDate.now(), 100, HabitStatus.ACTIVE);
        ExecutionDaysEntity executionDaysEntity = new ExecutionDaysEntity();
        executionDaysEntity.setExecutionDays(Set.of(ExecutionDayOption.MONDAY));
        executionDaysEntity.setId(1L);
        executionDaysEntity.setHabit(habitEntity);
        when(this.executionDayRepository.findForUserForDay(userId, executionDayOption, ExecutionDayOption.EVERYDAY))
                .thenReturn(List.of(executionDaysEntity));
        when(this.habitMapper.toHabitDto(habitEntity)).thenReturn(result);
        assertEquals(List.of(result), this.habitService.fetchActiveHabitInDay(userId, executionDayOption));
        verify(executionDayRepository, Mockito.times(1))
                .findForUserForDay(userId, executionDayOption, ExecutionDayOption.EVERYDAY);
    }

    @Test
    void testSetHabitExecutionDays(){
        String userId = "test123";
        ExecutionDayOption executionDayOption = ExecutionDayOption.MONDAY;
        HabitEntity habitEntity = new HabitEntity(userId, "habit1", 20, LocalDate.now(), 100);
        habitEntity.setStatus(HabitStatus.ACTIVE);
        habitEntity.setId(2L);
        when(this.habitRepository.findByIdAndUserId(2L, userId)).thenReturn(Optional.of(habitEntity));
        this.habitService.setHabitExecutionDays(userId, 2L, List.of(executionDayOption));
        ArgumentCaptor<ExecutionDaysEntity> argumentCaptor = ArgumentCaptor.forClass(ExecutionDaysEntity.class);
        verify(executionDayRepository).save(argumentCaptor.capture());
        ExecutionDaysEntity savedResults = argumentCaptor.getValue();
        assertEquals(Set.of(executionDayOption), savedResults.getExecutionDays());
    }
}
