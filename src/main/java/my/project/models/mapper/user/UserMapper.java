package my.project.models.mapper.user;

import lombok.RequiredArgsConstructor;
import my.project.models.entity.user.User;
import my.project.models.dto.user.UserDTO;
import my.project.models.mapper.Mapper;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserMapper implements Mapper<User, UserDTO> {

    private final RoleStatisticsMapper roleStatisticsMapper;

    @Override
    public UserDTO toDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                null,
                user.getRole(),
                user.getRoleStatistics().stream()
                        .map(roleStatisticsMapper::toDTO)
                        .collect(Collectors.toSet())
        );
    }
}
