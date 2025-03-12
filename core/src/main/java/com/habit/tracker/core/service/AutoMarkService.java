package com.habit.tracker.core.service;

import com.habit.tracker.core.entity.ExecutionDaysEntity;
import com.habit.tracker.core.entity.HabitEntity;
import com.habit.tracker.core.enums.ExecutionDayOption;
import com.habit.tracker.core.enums.ExecutionState;
import com.habit.tracker.core.enums.HabitStatus;
import com.habit.tracker.core.exceptions.IncorrectDateException;
import com.habit.tracker.core.repository.ExecutionDayRepository;
import com.habit.tracker.core.repository.HabitRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class AutoMarkService {
    @Value("${properties.days-back-editable}")
    private int numberOfEditableDay;

    private static final Logger logger = LoggerFactory.getLogger(AutoMarkService.class);

    private ExecutionDayRepository executionDayRepository;
    private HabitRepository habitRepository;
    private ExecutionHistoryService executionHistoryService;

    @Autowired
    public AutoMarkService(ExecutionDayRepository executionDayRepository,
                           HabitRepository habitRepository, ExecutionHistoryService executionHistoryService) {
        this.executionDayRepository = executionDayRepository;
        this.habitRepository = habitRepository;
        this.executionHistoryService = executionHistoryService;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void executeAutoMark() {
        logger.info("Starting automatic execution mark");
        List<HabitEntity> habitEntityList = this.habitRepository.findByStatus(HabitStatus.ACTIVE);
        LocalDate limitDate = LocalDate.now().minusDays(numberOfEditableDay + 1);
        for (HabitEntity habit : habitEntityList) {
            if (limitDate.isAfter(habit.getPurchaseDate())) {
                ExecutionDaysEntity executionDaysEntity = this.executionDayRepository
                        .findByHabit(habitEntityList.get(0)).orElse(null);
                if (executionDaysEntity != null) {
                    ExecutionDayOption currentDay = ExecutionDayOption.fromDayOfWeek(limitDate.getDayOfWeek());
                    if (executionDaysEntity.getExecutionDays().contains(currentDay) || executionDaysEntity
                            .getExecutionDays().contains(ExecutionDayOption.EVERYDAY)) {
                        String userId = habit.getUserId();
                        Long habitId = habitEntityList.get(0).getId();

                        try {
                            logger.info("Performing auto mark operation on habit {} for user {}", habitId, userId);
                            this.executionHistoryService.markHabitInDay(userId, habitId,
                                    ExecutionState.NOTDONEUNCHECKED, limitDate);
                        } catch (IncorrectDateException e) {
                            logger.error("Exception while performing auto mark operation on habit {} " +
                                    "for user {}", habitId, userId);
                            logger.error(e.getMessage());
                            logger.error(e.getCause().toString());
                        }
                    }
                }
            }

        }
    }
}
