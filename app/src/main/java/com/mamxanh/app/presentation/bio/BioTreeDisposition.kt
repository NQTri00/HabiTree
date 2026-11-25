package com.mamxanh.app.presentation.bio

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mamxanh.app.feature.tree.domain.repository.TreeCareReceipt
import com.mamxanh.app.feature.tree.presentation.ui.detail.TreeAction
import com.mamxanh.app.feature.tree.presentation.ui.detail.TreeViewModel
import org.koin.androidx.compose.koinViewModel

internal class BioTreeDisposition(
    val name: String?,
    val quote: TreeCareReceipt?,
    val loading: Boolean,
    val confirm: () -> Unit,
    val dismiss: () -> Unit,
)

@Composable
internal fun bindBioTreeDisposition(
    argument: String,
    selling: Boolean,
): BioTreeDisposition {
    val treeId = argument.toLongOrNull()
    val model = koinViewModel<TreeViewModel>()
    val state = model.state.collectAsStateWithLifecycle().value
    val quote = state.takeIf { it.dispositionId == treeId && it.selling == selling }?.quote
    LaunchedEffect(model, treeId, selling) {
        if (treeId != null && quote == null) model.dispatch(TreeAction.AskDisposition(treeId, selling, openDialog = false))
    }
    return BioTreeDisposition(
        name = state.detail?.takeIf { it.id == treeId }?.name,
        quote = quote,
        loading = state.isLoading,
        confirm = { if (treeId != null) model.dispatch(TreeAction.Dispose(treeId, selling)) },
        dismiss = { model.dispatch(TreeAction.DismissDisposition) },
    )
}
