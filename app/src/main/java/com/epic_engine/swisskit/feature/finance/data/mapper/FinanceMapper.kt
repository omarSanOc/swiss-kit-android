package com.epic_engine.swisskit.feature.finance.data.mapper

import com.epic_engine.swisskit.feature.finance.data.local.FinanceEntity
import com.epic_engine.swisskit.feature.finance.domain.model.Finance
import com.epic_engine.swisskit.feature.finance.domain.model.FinanceType

fun FinanceEntity.toDomain(): Finance = Finance(
    id = id,
    title = title,
    amount = amount,
    date = date,
    notes = notes,
    category = category,
    type = FinanceType.valueOf(type)
)

fun Finance.toEntity(): FinanceEntity = FinanceEntity(
    id = id,
    title = title,
    amount = amount,
    date = date,
    notes = notes,
    category = category,
    type = type.name
)
