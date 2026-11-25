package com.mamxanh.app.feature.tasks.presentation.ui.list

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.app.core.components.*
import com.mamxanh.app.core.components.design.AppDimensions
import com.mamxanh.app.feature.tasks.domain.model.Task
import com.mamxanh.shared.feature.tasks.domain.model.today
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey

@Preview(name = "Tasks · populated", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun TasksScreenPopulatedPreview() {
    MamXanhPreviewTheme {
        TasksScreen(
            TasksState(
                tasks =
                    listOf(
                        Task(
                            1,
                            AppText.text(TextKey.CopyDoc10TrangSachA3c873),
                            "HABIT",
                            AppText.canonical(TextKey.CopyHocTapA51516),
                            10,
                            "EVERY_DAY",
                            "",
                            "",
                            4,
                            "",
                        ),
                        Task(
                            2,
                            AppText.text(TextKey.CopyUongDuNuoc081b30),
                            "DAILY",
                            AppText.canonical(TextKey.CopySucKhoe071df7),
                            10,
                            "EVERY_DAY",
                            "",
                            today(),
                            2,
                            today(),
                            doneToday = true,
                        ),
                        Task(
                            3,
                            AppText.text(TextKey.CopyDiBo20PhutB36d50),
                            "TODO",
                            AppText.canonical(TextKey.CopySucKhoe071df7),
                            10,
                            "ONCE",
                            "",
                            "",
                            0,
                            "",
                        ),
                    ),
                isLoading = false,
            ),
            {},
        )
    }
}

@Preview(
    name = "Tasks · extended document audit",
    widthDp = 360,
    heightDp = 900,
    device = "spec:width=360dp,height=900dp,dpi=160",
)
@Composable
fun TasksExtendedDocumentPreview() {
    MamXanhPreviewTheme {
        TasksScreen(
            TasksState(
                tasks =
                    listOf(
                        Task(
                            1,
                            AppText.text(TextKey.CopyDoc10TrangSachA3c873),
                            "HABIT",
                            AppText.canonical(TextKey.CopyHocTapA51516),
                            10,
                            "EVERY_DAY",
                            "",
                            "",
                            0,
                            "",
                        ),
                        Task(
                            2,
                            AppText.text(TextKey.CopyUongDuNuoc081b30),
                            "DAILY",
                            AppText.canonical(TextKey.CopySucKhoe071df7),
                            10,
                            "EVERY_DAY",
                            "",
                            today(),
                            1,
                            today(),
                            doneToday = true,
                        ),
                        Task(
                            3,
                            AppText.text(TextKey.CopyDiBo20PhutB36d50),
                            "DAILY",
                            AppText.canonical(TextKey.CopySucKhoe071df7),
                            10,
                            "EVERY_DAY",
                            "",
                            today(),
                            1,
                            today(),
                            doneToday = true,
                        ),
                        Task(
                            4,
                            AppText.text(TextKey.CopyOnTuVung15PhutAcc66f),
                            "DAILY",
                            AppText.text(TextKey.CopyTiengAnh7ecaa5),
                            10,
                            "EVERY_DAY",
                            "",
                            "",
                            0,
                            "",
                        ),
                        Task(
                            5,
                            AppText.text(TextKey.CopyDonGocHocTapE45d91),
                            "TODO",
                            AppText.text(TextKey.CopyGiuGonGang63ed82),
                            10,
                            "ONCE",
                            "",
                            "",
                            0,
                            "",
                        ),
                        Task(
                            6,
                            AppText.text(TextKey.CopyChuanBiBaiChoNgayMai0c86eb),
                            "TODO",
                            AppText.canonical(TextKey.CopyHocTapA51516),
                            10,
                            "ONCE",
                            "",
                            "",
                            0,
                            "",
                        ),
                    ),
                isLoading = false,
            ),
            {},
        )
    }
}

@Preview(name = "Tasks · empty", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun TasksScreenEmptyPreview() {
    MamXanhPreviewTheme { TasksScreen(TasksState(isLoading = false), {}) }
}

@Preview(
    name = "Tasks · empty filter",
    widthDp = 360,
    heightDp = 514,
    device = "spec:width=360dp,height=514dp,dpi=160",
)
@Composable
fun TasksEmptyFilterPreview() {
    MamXanhPreviewTheme {
        TasksScreen(
            TasksState(
                tasks =
                    listOf(
                        Task(
                            1,
                            AppText.text(TextKey.CopyUongDuNuoc081b30),
                            "DAILY",
                            AppText.canonical(TextKey.CopySucKhoe071df7),
                            10,
                            "EVERY_DAY",
                            "",
                            "",
                            0,
                            "",
                        ),
                    ),
                filter = TaskFilter.HABIT,
                isLoading = false,
            ),
            {},
        )
    }
}

@Preview(
    name = "Tasks · daily reward claimed",
    widthDp = 300,
    heightDp = 90,
    device = "spec:width=300dp,height=90dp,dpi=160",
)
@Composable
fun DailyRewardClaimedPreview() {
    MamXanhPreviewTheme {
        Box(Modifier.padding(AppDimensions.Dp16)) { DailyRewardCard(false, {}, claimed = true) }
    }
}
