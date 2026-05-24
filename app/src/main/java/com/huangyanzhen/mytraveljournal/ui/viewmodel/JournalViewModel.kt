package com.huangyanzhen.mytraveljournal.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.huangyanzhen.mytraveljournal.data.db.AppRepository
import com.huangyanzhen.mytraveljournal.data.model.JournalEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class JournalUIState(
    val journals: List<JournalEntity> = emptyList(),
    override val isLoading: Boolean = true
) : BaseUIState(isLoading = true)

class JournalViewModel(
    repository: AppRepository
) : BaseViewModel<JournalUIState>(
    repository, JournalUIState()
) {

    init {
        loadJournals()
    }

    /**
     * 内部方法，读取所有日记，并更新UI状态。
     */
    fun loadJournals() = createAsyncTask {
        repository.getAllJournals().combine(uiStateRW) { journals, currentState ->
            currentState.copy(
                journals = journals,
                isLoading = false,
//                error = null
            )
        }.collect { newState -> uiStateRW.value = newState }
    }

//    fun loadJournals = initialize

    /**
     * 刷新，重新获取数据库中的所有日记
     */
    fun refreshJournals() {
        loadJournals()
    }

    /**
     * 通过日记ID获取日记
     * @param journalId: 日记ID
     */
    fun getJournalById(journalId: String): StateFlow<JournalEntity?> {
        return repository.getJournalById(journalId).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = null
        )
    }

    /**
     * 通过Query搜索日记，可以指定过滤函数。默认过滤通过条件为: Query是标题的子串。
     * @param query: 搜索关键字 (Query)
     * @param filterFunc: 过滤函数
     */
    fun searchJournals(
        query: String,
        filterFunc: (String, JournalEntity) -> Boolean = { query, entity ->
            entity.title.contains(query)
        }
    ): StateFlow<List<JournalEntity>> {
        return uiStateR.map { state ->
            state.journals.filter { journal ->
                filterFunc(query, journal)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList()
        )
    }


    /**
     * 创建新的日记
     * @param title: 标题
     * @param startDate: 开始时间
     * @param endDate: 结束时间
     * @return: 若成功则返回日记ID，若失败则返回`null`
     */
    fun createJournal(
        title: String,
        startDate: Long,
        endDate: Long
    ) = createAsyncTask {
        repository.createJournal(title, startDate, endDate)
    }

    /**
     * 删除日记
     * @param id: 日记ID
     * @return: 返回是否成功删除
     */
    fun deleteJournal(id: String) = createAsyncTask {
        repository.softDeleteJournal(id)
    }
}