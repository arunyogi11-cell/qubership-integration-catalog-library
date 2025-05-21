package org.qubership.integration.platform.catalog.persistence.configs.repository.context;


import org.qubership.integration.platform.catalog.persistence.configs.entity.context.ContextSystem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ContextSystemRepository extends JpaRepository<ContextSystem, String>, JpaSpecificationExecutor<ContextSystem> {

    @Query(nativeQuery = true,
            value = "SELECT * FROM catalog.context_system sys WHERE sys.id = :searchCondition "
                    + "UNION "
                    + "SELECT * FROM catalog.context_system sys WHERE UPPER(sys.name) = UPPER(:searchCondition) "
                    + "UNION "
                    + "SELECT * FROM catalog.context_system sys WHERE UPPER(sys.name) <> UPPER(:searchCondition) "
                    + "AND UPPER(sys.name) LIKE UPPER('%'||:searchCondition||'%') "
                    + "UNION "
                    + "SELECT * FROM catalog.context_system sys WHERE UPPER(sys.description) LIKE UPPER('%'||:searchCondition||'%')"
    )
    List<ContextSystem> searchForContextSystems(String searchCondition);
}
