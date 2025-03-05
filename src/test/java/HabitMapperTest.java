import com.habit.tracker.dto.HabitDto;
import com.habit.tracker.entity.HabitEntity;
import com.habit.tracker.enums.ExecutionDayOption;
import com.habit.tracker.enums.HabitStatus;
import com.habit.tracker.mapper.HabitMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

@ExtendWith(MockitoExtension.class)
public class HabitMapperTest {
    @Test
    void testToHabitDto(){
        String userId = "test123";
        HabitEntity habitEntity = new HabitEntity(userId, "habit1", 20, LocalDate.now(), 100);
        habitEntity.setStatus(HabitStatus.ACTIVE);
        habitEntity.setId(2l);
        HabitDto habitDto = new HabitDto(2l, "habit1", 20, LocalDate.now(), 100);
        HabitMapper habitMapper = new HabitMapper();
        Assertions.assertEquals(habitDto, habitMapper.toHabitDto(habitEntity));
    }
}
