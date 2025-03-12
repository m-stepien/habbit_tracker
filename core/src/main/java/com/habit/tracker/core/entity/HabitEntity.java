package com.habit.tracker.core.entity;

import com.habit.tracker.core.enums.HabitStatus;
import jakarta.persistence.*;

import java.time.LocalDate;
@Entity
public class HabitEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userId;
    private String name;
    private Integer points;
    private LocalDate creationDate;
    private LocalDate purchaseDate;
    private Integer unlockCost;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HabitStatus status;
    private Integer daysToMaster;
    private Integer remainingDays;


    public HabitEntity() {
    }

    public HabitEntity(String userId, String name, Integer points, LocalDate creationDate, Integer unlockCost) {
        this.userId = userId;
        this.name = name;
        this.points = points;
        this.creationDate = creationDate;
        this.unlockCost = unlockCost;
        this.status = HabitStatus.INACTIVE;
    }

    public HabitEntity(String name, Integer points, LocalDate creationDate, Integer unlockCost) {
        this.name = name;
        this.points = points;
        this.creationDate = creationDate;
        this.unlockCost = unlockCost;
        this.status = HabitStatus.INACTIVE;
    }

    public HabitEntity(String name, Integer points, LocalDate creationDate, Integer unlockCost, HabitStatus habitStatus) {
        this.name = name;
        this.points = points;
        this.creationDate = creationDate;
        this.unlockCost = unlockCost;
        this.status = habitStatus;
    }

    public HabitEntity(HabitEntity habitEntity){
        this.name= habitEntity.getName();
        this.points= habitEntity.getPoints();
        this.unlockCost= habitEntity.getUnlockCost();
        this.id=habitEntity.getId();
        this.creationDate=habitEntity.getCreationDate();
        this.status = habitEntity.getStatus();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public LocalDate getCreationDate() {
        return this.creationDate;
    }

    public void getCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public Integer getUnlockCost() {
        return unlockCost;
    }

    public void setUnlockCost(Integer unlockCost) {
        this.unlockCost = unlockCost;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public HabitStatus getStatus() {
        return status;
    }

    public void setStatus(HabitStatus status) {
        this.status = status;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public Integer getDaysToMaster() {
        return daysToMaster;
    }

    public void setDaysToMaster(Integer daysToMaster) {
        this.daysToMaster = daysToMaster;
        this.remainingDays = daysToMaster;
    }

    public Integer getRemainingDays() {
        return remainingDays;
    }

    public void decreaseRemainingDays(){
        this.remainingDays-=1;
    }

}
