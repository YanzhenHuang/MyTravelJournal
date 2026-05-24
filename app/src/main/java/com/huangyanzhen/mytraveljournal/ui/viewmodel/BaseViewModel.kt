package com.huangyanzhen.mytraveljournal.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.huangyanzhen.mytraveljournal.data.db.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

open class BaseUIState (
    open val isLoading: Boolean = true
)

abstract class BaseViewModel<T: BaseUIState>(
    protected val repository: AppRepository,
    initialState: T
) : ViewModel() {

    protected val uiStateRW: MutableStateFlow<T> = MutableStateFlow(initialState)
    val uiStateR: StateFlow<T> = uiStateRW.asStateFlow()

    /**
     * 将一个异步函数封装成可随时调用的协程。
     * @param task: 需要被封装的异步函数，返回值为`R`。
     * @return: 返回一个函数，接收两个参数：`onSuccess`和`onError`。
     */
    protected fun <R> createAsyncTask(
        task: suspend () -> R
    ): (onSuccess: ((R) -> Unit)?, onError: ((Exception) -> Unit)?) -> Unit {

        return { onSuccess: ((R) -> Unit)?, onError: ((Exception) -> Unit)? ->
            viewModelScope.launch {
                try {
                    val res = task()
                    onSuccess?.invoke(res)
                } catch (e: Exception) {
                    onError?.invoke(e)
                }
            }
        }
    }
}