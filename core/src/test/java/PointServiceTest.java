import com.habit.tracker.entity.PointsEntity;
import com.habit.tracker.repository.PointsRepository;
import com.habit.tracker.service.PointsService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PointServiceTest {
    @Mock
    PointsRepository pointsRepository;
    @InjectMocks
    PointsService pointsService;

    @Test
    void testCanUserBuySuccess(){
        String userId = "123";
        PointsEntity points = new PointsEntity(userId, 200);
        when(this.pointsRepository.findById(userId)).thenReturn(Optional.of(points));
        assertTrue(this.pointsService.canUserBuy(userId, 50));
    }

    @Test
    void testCanUserBuyFailure(){
        String userId = "123";
        PointsEntity points = new PointsEntity(userId, 50);
        when(this.pointsRepository.findById(userId)).thenReturn(Optional.of(points));
        assertFalse(this.pointsService.canUserBuy(userId, 90));
    }

    @Test
    void testCanUserButyThrowExceptionWhenRecordDoesntExist(){
        String userId = "123";
        when(this.pointsRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, ()->this.pointsService.canUserBuy(userId, 50));
    }

    @Test
    void testPaySubtractValueSuccess(){
        String userId = "123";
        PointsEntity points = new PointsEntity(userId, 100);
        when(this.pointsRepository.findById(userId)).thenReturn(Optional.of(points));
        this.pointsService.pay(userId, 30);
        assertEquals(70, points.getPoints());
        verify(pointsRepository).save(points);
    }

    @Test
    void testPayThrowExceptionWhenRecordDoesntExist(){
        String userId = "123";
        when(this.pointsRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, ()->this.pointsService.pay(userId, 50));
    }

    @Test
    void testAddPointsSuccess(){
        String userId = "123";
        PointsEntity points = new PointsEntity(userId, 80);
        when(this.pointsRepository.findById(userId)).thenReturn(Optional.of(points));
        this.pointsService.addPoints(userId, 20);
        assertEquals(100, points.getPoints());
        verify(this.pointsRepository).save(points);
    }

    @Test
    void testAddPointsThrowExceptionDoesntExist(){
        String userId = "123";
        when(this.pointsRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, ()->this.pointsService.addPoints(userId, 20));
    }
}
