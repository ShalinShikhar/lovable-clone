package com.example.lovable_clone.repository;

import com.example.lovable_clone.entity.Project;
import com.example.lovable_clone.enums.ProjectRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project,Long> {

    @Query("""
    SELECT p as Project,pm.projectRole as role FROM Project p
    JOIN ProjectMember pm ON pm.project = p
    WHERE p.deletedAt IS NULL
      AND pm.user.id = :userId
    ORDER BY p.updatedAt DESC""")
    List<ProjectWithRole> findAllAccessibleByUser(@Param("userId") Long userId);

    @Query("SELECT p from Project p JOIN ProjectMember pm ON pm.project = p WHERE  p.id = :projectId AND p.deletedAt IS NULL AND pm.user.id =:userId")
    Optional<Project> findAccessibleProjectById(@Param("projectId")Long projectId,@Param("userId")Long userId);

    @Query("SELECT p as Project,pm.projectRole as role FROM Project p JOIN ProjectMember pm ON pm.project = p WHERE  p.id = :projectId AND p.deletedAt IS NULL AND pm.user.id =:userId")
    Optional<ProjectWithRole> findAccessibleProjectByIdWithRole(@Param("projectId")Long projectId,@Param("userId")Long userId);

    interface  ProjectWithRole{
        Project getProject();
        ProjectRole getRole();
    }
}
