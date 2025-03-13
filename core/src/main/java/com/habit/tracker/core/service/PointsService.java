package com.habit.tracker.core.service;

import com.habit.tracker.core.entity.PointsEntity;
import com.habit.tracker.core.enums.ExecutionState;
import com.habit.tracker.core.repository.PointsRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PointsService {

    private final PointsRepository pointsRepository;

    @Value("${properties.start-points}")
    private int defaultPoints;
    private static final Logger logger = LoggerFactory.getLogger(PointsService.class);

    @Autowired
    PointsService(PointsRepository pointsRepository) {
        this.pointsRepository = pointsRepository;
    }

    public PointsEntity createUserPoints(String userId) {
        PointsEntity points = new PointsEntity(userId, defaultPoints);
        if (!this.pointsRepository.existsById(userId)) {
            points = this.pointsRepository.save(points);
        }
        logger.info("Creating points for user {} end successfull. Initial point value {}", userId, defaultPoints);
        return points;
    }

    public PointsEntity getUserPoints(String userId) {
        return this.pointsRepository.findById(userId).orElse(this.createUserPoints(userId));
    }

    @Transactional(readOnly = true)
    public boolean isUserExist(String userId){
        PointsEntity userPoint = this.pointsRepository.findById(userId).orElse(null);
        return userPoint==null;
    }

    @Transactional(readOnly = true)
    public boolean hasEnougthPoints(String userId, int cost) {
        PointsEntity points = this.pointsRepository.findById(userId).orElseThrow(EntityNotFoundException::new);
        logger.info("Checking can user buy habit user point value: {} habit cost {}",points, cost);
        return points.getPoints() >= cost;
    }

    @Transactional
    public void pay(String userId, int cost) {
        PointsEntity points = this.pointsRepository.findById(userId).orElseThrow(EntityNotFoundException::new);
        points.setPoints(points.getPoints() - cost);
        this.pointsRepository.save(points);
    }

    @Transactional
    public void addPoints(String userId, int additionalPoints) {
        PointsEntity points = this.pointsRepository.findById(userId).orElseThrow(EntityNotFoundException::new);
        points.setPoints(points.getPoints() + additionalPoints);
        this.pointsRepository.save(points);
    }

    @Transactional
    public void changePointAfterMark(ExecutionState state, int points, String userId, ExecutionState stateBefore){
        PointsEntity pointsEntity = this.pointsRepository.findById(userId).orElseThrow(EntityNotFoundException::new);

        if(state.equals(ExecutionState.DONE)){
            pointsEntity.setPoints(pointsEntity.getPoints()+points);
        }
        else if(state.equals(ExecutionState.NOTDONECHECKED)){
            pointsEntity.setPoints(pointsEntity.getPoints()- points / 2);
        }
        else{
            pointsEntity.setPoints(pointsEntity.getPoints()-points);
        }
        if(stateBefore!=null){
            if(stateBefore.equals(ExecutionState.DONE)){
                pointsEntity.setPoints(pointsEntity.getPoints()-points);
            }
            else if(stateBefore.equals(ExecutionState.NOTDONECHECKED)){
                pointsEntity.setPoints(pointsEntity.getPoints()+points / 2);
            }
            else{
                pointsEntity.setPoints(pointsEntity.getPoints()+points);
            }
        }
        this.pointsRepository.save(pointsEntity);
    }
}
