package com.habit.tracker.core.service;

import com.habit.tracker.core.dto.*;
import com.habit.tracker.core.entity.ExecutionHistoryEntity;
import com.habit.tracker.core.entity.HabitEntity;
import com.habit.tracker.core.enums.ExecutionState;
import com.habit.tracker.core.enums.HabitStatus;
import com.habit.tracker.core.exceptions.ExecutionHistoryNotFoundException;
import com.habit.tracker.core.exceptions.HabitNotFoundException;
import com.habit.tracker.core.exceptions.IncorrectDateException;
import com.habit.tracker.core.mapper.HabitMapper;
import com.habit.tracker.core.repository.ExecutionHistoryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExecutionHistoryService {

    @Value("${properties.days-back-editable}")
    private int numberOfEditableDay;

    private static final Logger logger = LoggerFactory.getLogger(ExecutionHistoryService.class);
    private HabitService habitService;
    private ExecutionHistoryRepository executionHistoryRepository;
    private HabitMapper habitMapper;
    private PointsService pointsService;


    @Autowired
    public ExecutionHistoryService(HabitService habitService, ExecutionHistoryRepository executionHistoryRepository,
                                   HabitMapper habitMapper, PointsService pointsService) {
        this.habitService = habitService;
        this.executionHistoryRepository = executionHistoryRepository;
        this.habitMapper = habitMapper;
        this.pointsService = pointsService;
    }

    public List<ExecutionHistoryDayDto> getExecutionInDay(String userId, LocalDate date) {
        List<ExecutionHistoryEntity> executionHistoryList = this.executionHistoryRepository.findExecutionInDay(userId, date);
        logger.info("Execution of habits in day for user {} on day {}: {}", userId, date, executionHistoryList);
        return executionHistoryList.stream().map(this::mapToDto).toList();
    }

    public HabitExecutionHistoryDto getExecutionOfHabit(String userId, Long habitId) {
        HabitEntity habit = this.habitService.getHabitById(habitId);
        List<ExecutionHistoryEntity> executionHistoryEntities = this.executionHistoryRepository.findExecutionForHabitId(habitId);
        if(!habit.getUserId().equals(userId)){
            throw new AccessDeniedException("User don't own this habit");
        }
        List<ExecutionHistoryDto> executionDays = executionHistoryEntities.stream()
                .map((ExecutionHistoryEntity e) ->
                        new ExecutionHistoryDto(e.getId(), e.getDate(), e.getExecutionState())).toList();
        logger.info("History of execution of habit {} : {}", habitId, executionDays);
        return new HabitExecutionHistoryDto(habit.getName(), executionDays);

    }

    public List<HabitExecutionHistoryDto> getAllExecutionHistory(String userId) {
        List<HabitDto> activeHabits = this.habitService.getUserHabits(userId).stream().filter(
                habit -> habit.status().equals(HabitStatus.ACTIVE)).toList();
        List<HabitExecutionHistoryDto> habitExecutionHistoryDtoList = new ArrayList<>();
        for (HabitDto habitDto : activeHabits) {
            habitExecutionHistoryDtoList.add(getExecutionOfHabit(userId, habitDto.id()));
        }
        logger.info("Full execution history of user {}: {}", userId, activeHabits);
        return habitExecutionHistoryDtoList;
    }

    public void markHabitInDay(String userId, Long habitId, ExecutionState state, LocalDate date) throws IncorrectDateException {
        HabitEntity habit = this.habitService.getHabitById(habitId);
        logger.info("Marking execution for: habit {} user {} state {} date {}", habitId, userId, state, date);
        ExecutionState stateBefore = null;
        if (date != null) {
            if (this.checkIsNotAfterToday(date) && this.checkIsExecutionEditable(date)) {
                if (isValidToMark(userId, habit)) {
                    logger.info("Habit {} valid to mark", habitId);
                    ExecutionHistoryEntity record = this.executionHistoryRepository.findByHabitIdAndDate(habitId, date).orElse(null);
                    if (record != null) {
                        stateBefore = record.getExecutionState();
                    }
                    ExecutionHistoryEntity executionHistoryEntity = this.updateRecord(date, state, habit, record);
                    this.executionHistoryRepository.save(executionHistoryEntity);
                    if (state.equals(ExecutionState.DONE) && !ExecutionState.DONE.equals(stateBefore)) {
                        this.habitService.decreaseRemainingDaysOfHabit(habit);
                    } else if (ExecutionState.DONE.equals(stateBefore) && !state.equals(ExecutionState.DONE)) {
                        logger.info("Rollback of habit remaining days after state change");
                        this.habitService.increaseRemainingDaysOfHabit(habit);
                    }
                    if (!state.equals(stateBefore)) {
                        logger.info("Rollback points of habit {} after state change", habitId);
                        this.pointsService.changePointAfterMark(state, habit.getPoints(), userId, stateBefore);
                    }
                } else if (habit.getStatus().equals(HabitStatus.INACTIVE)) {
                    throw new AccessDeniedException("Habit is not purchase");
                } else {
                    throw new AccessDeniedException("You don't own this habit");
                }
            }
        } else {
            logger.error("Date was null");
            throw new IncorrectDateException("Date can not by null");
        }
    }

    public void editDateOfExecution(String userId, Long executionId, LocalDate newDate) throws IncorrectDateException {
        ExecutionHistoryEntity executionHistory = this.executionHistoryRepository
                .findById(executionId).orElseThrow(EntityNotFoundException::new);
        if (!executionHistory.getHabit().getUserId().equals(userId)) {
            logger.error("User {} don't own habit {}", userId, executionHistory.getHabit().getId());
            throw new AccessDeniedException("You don't own habit related to this execution");
        }
        if (this.checkIsNotAfterToday(newDate)) {
            this.checkIsExecutionEditable(executionHistory.getDate());
            this.checkIsExecutionEditable(newDate);
            executionHistory.setDate(newDate);
            this.executionHistoryRepository.save(executionHistory);
        } else {
            throw new IncorrectDateException("Date can't be in future");
        }
        logger.info("Successful edit execution date to {} for executionId {}", newDate, executionId);
    }

    public void deleteHabitExecutionInDay(String userId, Long executionId) {
        ExecutionHistoryEntity executionHistory = this.executionHistoryRepository.findById(executionId)
                .orElseThrow(() -> new ExecutionHistoryNotFoundException(executionId));
        if(executionHistory.getHabit().getUserId().equals(userId)) {
            this.executionHistoryRepository.delete(executionHistory);
        }
        else{
            throw new AccessDeniedException("User don't own this habit");
        }
        logger.info("Deleting {} by user {} successful", executionId, userId);
    }

    @Transactional
    public void deleteExecutionHistoryRelatedToHabit(Long habitId) {
        logger.info("Deleting execution history related to habitId {}", habitId);
        List<ExecutionHistoryEntity> executionHistoryEntities = this.executionHistoryRepository.findExecutionForHabitId(habitId);
        for (ExecutionHistoryEntity executionHistoryEntity : executionHistoryEntities) {
            this.executionHistoryRepository.delete(executionHistoryEntity);
        }
    }

    private boolean isValidToMark(String userId, HabitEntity habit) {
        return userId.equals(habit.getUserId()) && habit.getStatus().equals(HabitStatus.ACTIVE);
    }

    private ExecutionHistoryEntity updateRecord(LocalDate date, ExecutionState state, HabitEntity habit, ExecutionHistoryEntity record) {
        logger.info("Updating execution history for habit: {}. Before: {}", habit.getId(), record);
        if (record == null) {
            record = new ExecutionHistoryEntity();
        }
        if (record.getDate() == null) {
            record.setDate(date);
        }
        if (record.getExecutionState() == null || !record.getExecutionState().equals(state)) {
            record.setExecutionState(state);
        }
        if (record.getHabit() == null) {
            record.setHabit(habit);
        }
        logger.info("Updating record for habit: {}. Record after: {}", habit.getId(), record);
        return record;
    }


    private boolean checkIsExecutionEditable(LocalDate date) throws IncorrectDateException {
        LocalDate deadline = LocalDate.now().minusDays(this.numberOfEditableDay);
        if (date.isAfter(deadline)) {
            return true;
        } else {
            throw new IncorrectDateException("You can't edit record before " + deadline
                    + " current try date was " + date);
        }
    }

    private boolean checkIsNotAfterToday(LocalDate date) {
        return !date.isAfter(LocalDate.now());
    }

    private ExecutionHistoryDayDto mapToDto(ExecutionHistoryEntity executionHistoryEntity) {
        return new ExecutionHistoryDayDto(executionHistoryEntity.getId(),
                this.habitMapper.toHabitDto(executionHistoryEntity.getHabit()),
                executionHistoryEntity.getExecutionState(), executionHistoryEntity.getDate());
    }

}
