package org.qubership.integration.platform.catalog.persistence.configs.entity.context;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.qubership.integration.platform.catalog.persistence.configs.entity.system.AbstractSystemEntity;

@Getter
@Setter
@Entity
@SuperBuilder
@NoArgsConstructor
@FieldNameConstants
public class ContextSystem extends AbstractSystemEntity {

    @Override
    public boolean equals(Object object) {
        return equals(object, true);
    }

}

