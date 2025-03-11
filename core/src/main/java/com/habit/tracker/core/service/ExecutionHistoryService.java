package com.habit.tracker.core.service;

import com.habit.tracker.core.dto.ExecutionHistoryDayDto;
import com.habit.tracker.core.dto.ExecutionHistoryDto;
import com.habit.tracker.core.dto.HabitDto;
import com.habit.tracker.core.dto.HabitExecutionHistoryDto;
import com.habit.tracker.core.entity.ExecutionHistoryEntity;
import com.habit.tracker.core.entity.HabitEntity;
import com.habit.tracker.core.enums.ExecutionState;
import com.habit.tracker.core.enums.HabitStatus;
import com.habit.tracker.core.exceptions.IncorrectDateException;
import com.habit.tracker.core.mapper.HabitMapper;
import com.habit.tracker.core.repository.ExecutionHistoryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExecutionHistoryService {

    @Value("${properties.day-back-editable}")
    private int numberOfEditableDay;

    private HabitService habitService;
    private ExecutionHistoryRepository executionHistoryRepository;
    private HabitMapper habitMapper;

    @Autowired
    public ExecutionHistoryService(HabitService habitService, ExecutionHistoryRepository executionHistoryRepository, HabitMapper habitMapper) {
        this.habitService = habitService;
        this.executionHistoryRepository = executionHistoryRepository;
        this.habitMapper = habitMapper;
    }

    public List<ExecutionHistoryDto> getExecutionInDay(String userId, LocalDate date) {
        List<ExecutionHistoryEntity> executionHistoryList = this.executionHistoryRepository.findExecutionInDay(userId, date);
        return executionHistoryList.stream().map(this::mapToDto).toList();
    }

    public HabitExecutionHistoryDto getExecutionOfHabit(String userId, Long habitId) {
        List<ExecutionHistoryEntity> executionHistoryEntities = this.executionHistoryRepository.findExecutionForHabitId(userId, habitId);
        List<ExecutionHistoryDayDto> executionDays = executionHistoryEntities.stream()
                .map((ExecutionHistoryEntity e) ->
                        new ExecutionHistoryDayDto(e.getId(), e.getDate(), e.getExecutionState())).toList();
        return new HabitExecutionHistoryDto(executionHistoryEntities.get(0).getHabit().getName(), executionDays);

    }

    public List<HabitExecutionHistoryDto> getAllExecutionHistory(String userId) {
        List<HabitDto> activeHabits = this.habitService.getUserHabits(userId).stream().filter(
                habit -> habit.status().equals(HabitStatus.ACTIVE)).toList();
        List<HabitExecutionHistoryDto> habitExecutionHistoryDtoList = new ArrayList<>();
        for (HabitDto habitDto : activeHabits) {
            habitExecutionHistoryDtoList.add(getExecutionOfHabit(userId, habitDto.id()));
        }
        return habitExecutionHistoryDtoList;
    }

    public void markHabitInDay(String userId, Long habitId, ExecutionState state, LocalDate date) throws IncorrectDateException {
        HabitEntity habit = this.habitService.getHabitById(habitId);
        if (date != null) {
            if (isValidToMark(userId, habit)) {
                ExecutionHistoryEntity executionHistoryEntity = this.createRecord(habitId, date, state, habit);
                this.executionHistoryRepository.save(executionHistoryEntity);
            } else if (habit.getStatus().equals(HabitStatus.INACTIVE)) {
                throw new AccessDeniedException("Habit is not purchase");
            } else {
                throw new AccessDeniedException("You don't own this habit");
            }
        } else {
            throw new IncorrectDateException("Date can not by null");
        }
    }

    public void editDateOfExecution(Long executionId, LocalDate newDate) throws IncorrectDateException {
        ExecutionHistoryEntity executionHistory = this.executionHistoryRepository
                .findById(executionId).orElseThrow(EntityNotFoundException::new);
        if (this.checkIsNotAfterToday(newDate)) {
            this.checkIsExecutionEditable(executionHistory.getDate());
            this.checkIsExecutionEditable(newDate);
            executionHistory.setDate(newDate);
            this.executionHistoryRepository.save(executionHistory);
        } else {
            throw new IncorrectDateException("Date can't be in future");
        }
    }

    public void deleteHabitExecutionInDay(String userId, Long executionId) {
        this.executionHistoryRepository.deleteUserExecutionHistory(userId, executionId);
    }

    private boolean isValidToMark(String userId, HabitEntity habit) {
        return userId.equals(habit.getUserId()) && habit.getStatus().equals(HabitStatus.ACTIVE);
    }

    private ExecutionHistoryEntity createRecord(Long habitId, LocalDate date, ExecutionState state, HabitEntity habit) {
        ExecutionHistoryEntity record = this.executionHistoryRepository.findByHabitIdAndDate(habitId, date).orElse(null);
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
        return record;
    }


    private boolean checkIsExecutionEditable(LocalDate date) throws IncorrectDateException {
        LocalDate deadline = LocalDate.now().minusDays(this.numberOfEditableDay);
        if (date.isAfter(deadline)) {
            return true;
        } else {
            throw new IncorrectDateException("You can't edit record before " + deadline.toString()
                    + "current try date was" + date.toString());
        }
    }

    private boolean checkIsNotAfterToday(LocalDate date) {
        return !date.isAfter(LocalDate.now());
    }

    private ExecutionHistoryDto mapToDto(ExecutionHistoryEntity executionHistoryEntity) {
        return new ExecutionHistoryDto(executionHistoryEntity.getId(),
                this.habitMapper.toHabitDto(executionHistoryEntity.getHabit()),
                executionHistoryEntity.getExecutionState(), executionHistoryEntity.getDate());
    }

}
