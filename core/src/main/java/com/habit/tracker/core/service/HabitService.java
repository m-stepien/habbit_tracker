package com.habit.tracker.core.service;

import com.habit.tracker.core.dto.HabitDto;
import com.habit.tracker.core.dto.HabitUpdateRequestDto;
import com.habit.tracker.core.dto.HabitWithDaysDto;
import com.habit.tracker.core.entity.ExecutionDaysEntity;
import com.habit.tracker.core.entity.HabitEntity;
import com.habit.tracker.core.enums.ExecutionDayOption;
import com.habit.tracker.core.enums.HabitStatus;
import com.habit.tracker.core.exceptions.HabitAlreadyBoughtException;
import com.habit.tracker.core.exceptions.HabitNotFoundException;
import com.habit.tracker.core.mapper.HabitMapper;
import com.habit.tracker.core.repository.ExecutionDayRepository;
import com.habit.tracker.core.repository.HabitRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
public class HabitService {
    private final HabitRepository habitRepository;
    private final HabitMapper habitMapper;
    private final PointsService pointsService;
    private final ExecutionDayRepository executionDayRepository;
    private final ExecutionHistoryService executionHistoryService;

    private static final Logger logger = LoggerFactory.getLogger(HabitService.class);


    @Autowired
    HabitService(PointsService pointsService, HabitRepository habitRepository,
                 ExecutionDayRepository executionDayRepository, HabitMapper habitMapper,
                 @Lazy ExecutionHistoryService executionHistoryService) {
        this.pointsService = pointsService;
        this.habitRepository = habitRepository;
        this.executionDayRepository = executionDayRepository;
        this.habitMapper = habitMapper;
        this.executionHistoryService = executionHistoryService;
    }

    public HabitWithDaysDto getUserHabitById(String userId, long habitId) {
        HabitEntity habit = this.habitRepository.findByIdAndUserId(habitId, userId)
                .orElseThrow(() -> new HabitNotFoundException(habitId));
        logger.info("Found habit with id {} \n {}", habitId, habit);
        ExecutionDaysEntity executionDays = this.executionDayRepository.findByHabit(habit).orElse(new ExecutionDaysEntity());
        logger.info("Searching for executionDayEntity for habit {}. Found {}", habitId, executionDays);
        return this.habitMapper.habitWithDaysDto(habit, executionDays);
    }

    public List<HabitDto> getUserHabits(String userId) {
        List<HabitEntity> userHabits = this.habitRepository.findByUserId(userId);
        logger.info("Found habit of user {} : {}", userId, userHabits);
        return userHabits.stream().map(this.habitMapper::toHabitDto).toList();
    }

    public List<HabitDto> getUserHabitToExecuteInDay(String userId, LocalDate date){
        ExecutionDayOption day = ExecutionDayOption.fromDayOfWeek(date.getDayOfWeek());
        List<HabitEntity> userHabits = this.habitRepository.findByUserId(userId);
        List<HabitDto> toDo = new ArrayList<>();
        for(HabitEntity habit : userHabits){
            if(habit.getStatus().equals(HabitStatus.ACTIVE) && habit.getCreationDate().isBefore(date)){
                ExecutionDaysEntity executionDays = this.executionDayRepository.findByHabit(habit).orElse(null);
                if(executionDays!=null){
                    if(executionDays.getExecutionDays().contains(day) || executionDays.getExecutionDays().contains(ExecutionDayOption.EVERYDAY)){
                        toDo.add(this.habitMapper.toHabitDto(habit));
                    }
                }
            }
        }
        return toDo;
    }
    public void saveUserHabit(String userId, HabitDto newHabitDto) throws HabitNotFoundException.HabitAlreadyExistException {
        List<HabitEntity> habitEntities = this.habitRepository.findByUserIdAndName(userId, newHabitDto.name());
        logger.info("Checking is habit already in database");
        if (habitEntities.isEmpty()) {
            logger.info("Habit not in database. Start creation of habit {} for user {}", newHabitDto, userId);
            HabitEntity newHabitEntity = this.habitMapper.toHabitEntity(newHabitDto);
            newHabitEntity.setStatus(HabitStatus.INACTIVE);
            newHabitEntity.setCreationDate(LocalDate.now());
            newHabitEntity.setUserId(userId);
            this.habitRepository.save(newHabitEntity);
            logger.info("Successful creating new habit");
        } else {
            throw new HabitNotFoundException.HabitAlreadyExistException("Habit with name " + newHabitDto.name()
                    + " already exist for user " + userId);
        }
    }

    public void updateHabit(String userId, HabitUpdateRequestDto habitUpdateRequestDto) {
        HabitEntity updatedHabit = this.habitRepository.findByIdAndUserId(habitUpdateRequestDto.id(), userId)
                .orElseThrow(() -> new HabitNotFoundException(habitUpdateRequestDto.id()));
        logger.info("Start update operation. Updating habit {} with new values {}", updatedHabit, habitUpdateRequestDto);
        if (updatedHabit.getStatus().equals(HabitStatus.INACTIVE)) {
            updatedHabit = this.setNewValues(updatedHabit, habitUpdateRequestDto);
            this.habitRepository.save(updatedHabit);
        } else {
            logger.error("Cannot update habit that in not in state INACTIVE");
            throw new HabitAlreadyBoughtException("Cannot update habit that is already ACTIVE or COMPLETED");
        }
    }


    @Transactional
    public void purchaseHabit(String userId, Long habitId) {
        logger.info("Searching for habit {} for user {}", habitId, userId);
        HabitEntity habitEntity = this.habitRepository.findByIdAndUserId(habitId, userId)
                .orElseThrow(EntityNotFoundException::new);
        logger.info("Habit with id {} found {}", habitId, userId);
        int unlockCost = habitEntity.getUnlockCost();
        if (this.pointsService.hasEnougthPoints(userId, unlockCost)) {
            if (this.canUserBuy(userId, habitEntity)) {
                habitEntity.setStatus(HabitStatus.ACTIVE);
                habitEntity.setPurchaseDate(LocalDate.now());
                this.habitRepository.save(habitEntity);
                this.pointsService.pay(userId, unlockCost);
                logger.info("Purchase habit {} by user {} end successful", habitId, userId);
            } else {
                logger.error("Habit {} was already purchased by user {}", habitId, userId);
                throw new HabitAlreadyBoughtException("This habit is already active");
            }
        }
    }

    @Transactional
    public void deleteHabit(String userId, Long habitId) throws HabitNotFoundException {
        HabitEntity habit = this.habitRepository.findById(habitId).orElseThrow(() -> new HabitNotFoundException(habitId));
        if (habit.getUserId().equals(userId)) {
            ExecutionDaysEntity executionDays = this.executionDayRepository.findByHabit(habit).orElse(null);
            if (executionDays != null) {
                logger.info("Deleting execution days {} related to habit {}", executionDays.getId(), habit.getId());
                this.executionDayRepository.delete(executionDays);
            }
            this.executionHistoryService.deleteExecutionHistoryRelatedToHabit(habitId);
            this.habitRepository.delete(habit);
            logger.info("Successful deleting habit {} by user {}", habitId, userId);
        } else {
            logger.error("User {} does not own habit {}", userId, habitId);
        }
    }

    public void setHabitExecutionDays(String userId, Long habitId, List<ExecutionDayOption> executionDayOption) {
        logger.info("Setting habit execution days: {} for habit: {}", executionDayOption, habitId);
        if (executionDayOption.contains(ExecutionDayOption.EVERYDAY)) {
            executionDayOption = List.of(ExecutionDayOption.EVERYDAY);
        }
        HabitEntity habit = this.habitRepository.findByIdAndUserId(habitId, userId)
                .orElseThrow(EntityNotFoundException::new);
        ExecutionDaysEntity currentExecutionDays = this.executionDayRepository
                .findByHabit(habit).orElseGet(() -> new ExecutionDaysEntity(habit));
        currentExecutionDays.setExecutionDays(new HashSet<>(executionDayOption));
        logger.info("Setting habit execution days for habit: {} completed", habitId);
        this.executionDayRepository.save(currentExecutionDays);
    }

    public List<HabitDto> fetchActiveHabitInDay(String userId, ExecutionDayOption day) {
        logger.info("Searching for active habit for user; {} day: {}", userId, day);
        List<HabitEntity> habitForDay = this.executionDayRepository
                .findForUserForDay(userId, day, ExecutionDayOption.EVERYDAY).stream()
                .map(ExecutionDaysEntity::getHabit).toList();
        habitForDay = habitForDay.stream().filter(habit -> habit.getStatus().equals(HabitStatus.ACTIVE))
                .toList();
        List<HabitDto> habitDtoList = new ArrayList<>();
        for (HabitEntity habit : habitForDay) {
            habitDtoList.add(this.habitMapper.toHabitDto(habit));
        }
        logger.info("Active habit for user {} on day {} : {}", userId, day, habitDtoList);
        return habitDtoList;
    }

    public HabitEntity getHabitById(Long habitId) {
        return this.habitRepository.findById(habitId).orElseThrow(EntityNotFoundException::new);
    }

    @Transactional
    public void decreaseRemainingDaysOfHabit(HabitEntity habit) {
        logger.info("Habit: {} remaining days {}", habit.getId(), habit.getRemainingDays());
        habit.decreaseRemainingDays();
        if (habit.getRemainingDays() == 0) {
            habit.setStatus(HabitStatus.COMPLETED);
        }
        this.habitRepository.save(habit);
        logger.info("Successful decrease days of habit {}. Remaining days {}", habit.getId(), habit.getRemainingDays());
    }

    @Transactional
    public void increaseRemainingDaysOfHabit(HabitEntity habit) {
        logger.info("Habit: {} remaining days {}", habit.getId(), habit.getRemainingDays());
        habit.increaseRemainingDays();
        if (habit.getStatus().equals(HabitStatus.COMPLETED)) {
            habit.setStatus(HabitStatus.ACTIVE);
        }
        logger.info("Successful increase days of habit {}. Remaining days {}", habit.getId(), habit.getRemainingDays());

    }

    @Transactional
    private boolean canUserBuy(String userId, HabitEntity habit) {
        logger.info("Checking is habit {} owned by user {}", habit.getId(), userId);
        List<HabitEntity> userActiveHabit = this.habitRepository.findByUserIdAndStatus(userId, HabitStatus.INACTIVE);
        for (HabitEntity activeHabit : userActiveHabit) {
            if (activeHabit.getId().equals(habit.getId())) {
                logger.info("Habit {} is owned by user {} and inactive", habit.getId(), userId);
                return true;
            }
        }
        logger.info("Habit {} is not owned by user {} or is not inactive", habit.getId(), userId);
        return false;
    }

    private HabitEntity setNewValues(HabitEntity updatedHabit, HabitUpdateRequestDto habitUpdateRequestDto) {
        if (habitUpdateRequestDto.name() != null) {
            updatedHabit.setName(habitUpdateRequestDto.name());
        }
        if (habitUpdateRequestDto.daysToMaster() != null) {
            updatedHabit.setDaysToMaster(habitUpdateRequestDto.daysToMaster());
        }
        if (habitUpdateRequestDto.points() != null) {
            updatedHabit.setPoints(habitUpdateRequestDto.points());
        }
        if (habitUpdateRequestDto.unlockCost() != null) {
            updatedHabit.setUnlockCost(habitUpdateRequestDto.unlockCost());
        }
        return updatedHabit;
    }
}

