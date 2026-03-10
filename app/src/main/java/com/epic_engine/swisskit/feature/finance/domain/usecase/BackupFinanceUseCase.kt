package com.epic_engine.swisskit.feature.finance.domain.usecase

import com.epic_engine.swisskit.feature.finance.domain.model.Finance
import com.epic_engine.swisskit.feature.finance.domain.model.FinanceType
import com.epic_engine.swisskit.feature.finance.domain.repository.FinanceRepository
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class BackupFinanceUseCase @Inject constructor(
    private val repository: FinanceRepository
) {
    suspend operator fun invoke(): Result<String> =
        repository.getAll().mapCatching { items ->
            val dtos = items.map { it.toBackupDto() }
            Json.encodeToString(dtos)
        }
}

class RestoreFinanceUseCase @Inject constructor(
    private val repository: FinanceRepository
) {
    suspend operator fun invoke(jsonContent: String): Result<Int> = runCatching {
        val dtos = Json.decodeFromString<List<FinanceBackupDto>>(jsonContent)
        val items = dtos.map { it.toDomain() }
        repository.deleteAll().getOrThrow()
        repository.insertAll(items).getOrThrow()
        items.size
    }
}

@Serializable
data class FinanceBackupDto(
    val id: String,
    val title: String,
    val amount: Double,
    val date: Long,
    val notes: String? = null,
    val category: String,
    val type: String
)

fun Finance.toBackupDto() = FinanceBackupDto(id, title, amount, date, notes, category, type.name)
fun FinanceBackupDto.toDomain() = Finance(id, title, amount, date, notes, category, FinanceType.valueOf(type))
