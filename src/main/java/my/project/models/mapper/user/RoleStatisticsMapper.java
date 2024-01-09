package my.project.models.mapper.user;

import lombok.RequiredArgsConstructor;
import my.project.models.dto.user.RoleStatisticsDTO;
import my.project.models.entity.user.RoleStatistics;
import my.project.models.mapper.Mapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleStatisticsMapper implements Mapper<RoleStatistics, RoleStatisticsDTO> {

    @Override
    public RoleStatisticsDTO toDTO(RoleStatistics entity) {
        return new RoleStatisticsDTO(
                entity.getId(),
                entity.getRoleName(),
                entity.getCurrentStreak(),
                entity.getDateOfLastStreak(),
                entity.getRecordStreak()
        );
    }
}
