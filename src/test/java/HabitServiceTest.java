import com.habit.tracker.dto.HabitDto;
import com.habit.tracker.entity.HabitEntity;
import com.habit.tracker.mapper.HabitMapper;
import com.habit.tracker.repository.HabitRepository;
import com.habit.tracker.service.HabitService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;


@ExtendWith(MockitoExtension.class)
public class HabitServiceTest {
    @Mock
    HabitRepository habitRepository;
    @Mock
    HabitMapper habitMapper;

    @InjectMocks
    HabitService habitService;

    @Test
    void saveUserHabit(){
        HabitDto habitDto = new HabitDto(1L,"test123", 13, LocalDate.now(), 20);
        HabitEntity habitEntity = new HabitEntity("user1", "test123", 13, LocalDate.now(), 20);
        HabitEntity habitEntity1 = new HabitEntity(habitEntity);
        habitEntity1.setId(1l);
        Mockito.when(habitRepository.save(any(HabitEntity.class))).thenReturn(habitEntity1);
        Mockito.when(habitMapper.toHabitEntity(any(HabitDto.class))).thenReturn(habitEntity);
        this.habitService.saveUserHabit("user1", habitDto);
        Mockito.verify(habitRepository, Mockito.times(1)).save(any(HabitEntity.class));
        Mockito.verify(habitMapper, Mockito.times(1)).toHabitEntity(any(HabitDto.class));
    }

}
