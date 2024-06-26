package com.example.Context.Database.CommonEntity.EntityInterface

import org.jetbrains.exposed.sql.Column
import java.time.OffsetDateTime

interface IEntityAuditDeleted {

    /**
     * Дата удаления
     */
    val deletedAt: Column<OffsetDateTime?>
}