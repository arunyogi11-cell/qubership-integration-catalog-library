package org.qubership.integration.platform.catalog.service;

import org.qubership.integration.platform.catalog.model.system.IntegrationSystemType;
import org.qubership.integration.platform.catalog.model.system.OperationProtocol;
import org.qubership.integration.platform.catalog.persistence.configs.entity.actionlog.ActionLog;
import org.qubership.integration.platform.catalog.persistence.configs.entity.actionlog.EntityType;
import org.qubership.integration.platform.catalog.persistence.configs.entity.actionlog.LogOperation;
import org.qubership.integration.platform.catalog.persistence.configs.entity.context.ContextSystem;
import org.qubership.integration.platform.catalog.persistence.configs.repository.context.ContextSystemRepository;
import org.qubership.integration.platform.catalog.persistence.configs.repository.system.IntegrationSystemLabelsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ContextBaseService {

    private static final Map<IntegrationSystemType, Collection<OperationProtocol>> ALLOWED_PROTOCOL_MAP = Map.of(
            IntegrationSystemType.EXTERNAL, Arrays.stream(OperationProtocol.values())
                    .filter(protocol -> !OperationProtocol.METAMODEL.equals(protocol))
                    .collect(Collectors.toSet()),
            IntegrationSystemType.INTERNAL, Set.of(OperationProtocol.values()),
            IntegrationSystemType.IMPLEMENTED, Set.of(
                    OperationProtocol.HTTP,
                    OperationProtocol.SOAP,
                    OperationProtocol.GRAPHQL
            )
    );

    protected final ContextSystemRepository contextSystemRepository;
    protected final ActionsLogService actionsLogger;
    protected final IntegrationSystemLabelsRepository systemLabelsRepository;

    @Autowired
    public ContextBaseService(ContextSystemRepository contextSystemRepository, ActionsLogService actionsLogger,
                              IntegrationSystemLabelsRepository systemLabelsRepository) {
        this.contextSystemRepository = contextSystemRepository;
        this.actionsLogger = actionsLogger;
        this.systemLabelsRepository = systemLabelsRepository;
    }

    @Transactional
    public List<ContextSystem> getAll() {
        return contextSystemRepository.findAll(Sort.by("name"));
    }

    @Transactional
    public ContextSystem getByIdOrNull(String id) {
        return contextSystemRepository.findById(id).orElse(null);
    }

    @Transactional
    public ContextSystem save(ContextSystem system) {
        return update(system);
    }

    @Transactional
    public ContextSystem create(ContextSystem system) {
        return create(system, false);
    }

    @Transactional
    public ContextSystem create(ContextSystem system, boolean isImport) {
        ContextSystem savedSystem = contextSystemRepository.save(system);
        logSystemAction(savedSystem, isImport ? LogOperation.CREATE_OR_UPDATE : LogOperation.CREATE);
        return savedSystem;
    }

    @Transactional
    public ContextSystem update(ContextSystem system) {
        return update(system, true);
    }

    @Transactional
    public ContextSystem update(ContextSystem system, boolean logAction) {
        ContextSystem updatedSystem = contextSystemRepository.save(system);
        if (logAction) {
            logSystemAction(updatedSystem, LogOperation.UPDATE);
        }
        return updatedSystem;
    }

    @Transactional
    public void delete(String systemId) {
        ContextSystem system = contextSystemRepository.getReferenceById(systemId);
        contextSystemRepository.delete(system);
        logSystemAction(system, LogOperation.DELETE);
    }


    protected void logSystemAction(ContextSystem system, LogOperation operation) {
        actionsLogger.logAction(ActionLog.builder()
                .entityType(EntityType.CONTEXT_SYSTEM)
                .entityId(system.getId())
                .entityName(system.getName())
                .operation(operation)
                .build());
    }
}
