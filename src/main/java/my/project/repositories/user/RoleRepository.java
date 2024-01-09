package my.project.repositories.user;

import my.project.models.entity.user.RoleStatistics;
import my.project.models.entity.user.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<RoleStatistics, Long> {

    Optional<RoleStatistics> findByRoleName(RoleName roleName);
}
