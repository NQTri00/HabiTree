package com.mamxanh.shared.model

import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey

data class HabitTemplate(
    val id: String,
    val config: TaskConfig,
    val effort: String,
)

val habitTemplates get() =
    listOf(
        HabitTemplate(
            "study25",
            TaskConfig(
                AppText.text(TextKey.CopyHocTapTrung25Phut9c7edf),
                "HABIT",
                AppText.canonical(TextKey.CopyHocTapA51516),
                verification = "TIMER",
                minutes = 25,
            ),
            AppText.text(TextKey.Copy25Phut9786ed),
        ),
        HabitTemplate(
            "review15",
            TaskConfig(
                AppText.text(TextKey.CopyOnLaiBaiCuF7dc50),
                "HABIT",
                AppText.canonical(TextKey.CopyHocTapA51516),
                verification = "TIMER",
                minutes = 15,
            ),
            AppText.text(TextKey.Copy15Phut43786c),
        ),
        HabitTemplate(
            "read20",
            TaskConfig(
                AppText.text(TextKey.CopyDocSach4bc171),
                "HABIT",
                AppText.canonical(TextKey.CopyHocTapA51516),
                verification = "TIMER",
                minutes = 20,
            ),
            AppText.text(TextKey.Copy20PhutFb6d0d),
        ),
        HabitTemplate(
            "words",
            TaskConfig(AppText.text(TextKey.CopyHoc10TuVungMoi37981e), "HABIT", AppText.canonical(TextKey.CopyHocTapA51516)),
            AppText.text(TextKey.Copy10Tu8bca08),
        ),
        HabitTemplate(
            "evening",
            TaskConfig(
                AppText.text(TextKey.CopyHocBuoiToi33d892),
                "HABIT",
                AppText.canonical(TextKey.CopyHocTapA51516),
                verification = "TIMER",
                minutes = 25,
                startMinute = 1080,
                endMinute = 1380,
            ),
            AppText.text(TextKey.Copy25Phut9786ed),
        ),
        HabitTemplate(
            "walk20",
            TaskConfig(
                AppText.text(TextKey.CopyDiBoNheNhang4c4f1a),
                "HABIT",
                AppText.canonical(TextKey.CopySucKhoe071df7),
                verification = "TIMER",
                minutes = 20,
            ),
            AppText.text(TextKey.Copy20PhutFb6d0d),
        ),
        HabitTemplate(
            "stretch5",
            TaskConfig(
                AppText.text(TextKey.CopyGianCoC28690),
                "HABIT",
                AppText.canonical(TextKey.CopySucKhoe071df7),
                verification = "TIMER",
                minutes = 5,
            ),
            AppText.text(TextKey.Copy5PhutE98d21),
        ),
        HabitTemplate(
            "water",
            TaskConfig(AppText.text(TextKey.CopyUongDuNuoc081b30), "HABIT", AppText.canonical(TextKey.CopySucKhoe071df7)),
            AppText.text(TextKey.CopyTrongNgayBf53f8),
        ),
        HabitTemplate(
            "fruit",
            TaskConfig(AppText.text(TextKey.CopyAnThemRauHoacTraiCay4d0f18), "HABIT", AppText.canonical(TextKey.CopySucKhoe071df7)),
            AppText.text(TextKey.CopyMotPhanC473ce),
        ),
        HabitTemplate(
            "brush-am",
            TaskConfig(
                AppText.text(TextKey.CopyDanhRangBuoiSang731635),
                "HABIT",
                AppText.canonical(TextKey.CopyNepSong8d1a98),
                startMinute = 360,
                endMinute = 540,
            ),
            "06:00–09:00",
        ),
        HabitTemplate(
            "brush-pm",
            TaskConfig(
                AppText.text(TextKey.CopyDanhRangBuoiToiCb42b3),
                "HABIT",
                AppText.canonical(TextKey.CopyNepSong8d1a98),
                startMinute = 1080,
                endMinute = 1380,
            ),
            "18:00–23:00",
        ),
        HabitTemplate(
            "bed",
            TaskConfig(AppText.text(TextKey.CopyDonGiuongGapChan3b6c91), "HABIT", AppText.canonical(TextKey.CopyViecNha126ef3)),
            AppText.text(TextKey.Copy3Phut36d5bc),
        ),
        HabitTemplate(
            "dishes",
            TaskConfig(AppText.text(TextKey.CopyRuaChenSauBuaAnF2df04), "HABIT", AppText.canonical(TextKey.CopyViecNha126ef3)),
            AppText.text(TextKey.Copy10PhutEd33fe),
        ),
        HabitTemplate(
            "room",
            TaskConfig(
                AppText.text(TextKey.CopyDonPhongCuoiTuan6c7624),
                "HABIT",
                AppText.canonical(TextKey.CopyViecNha126ef3),
                frequency = "WEEKENDS",
            ),
            AppText.text(TextKey.Copy20PhutFb6d0d),
        ),
        HabitTemplate(
            "prepare",
            TaskConfig(AppText.text(TextKey.CopyChuanBiCapSachChoNgayMaiF64a3c), "HABIT", AppText.canonical(TextKey.CopyHocTapA51516)),
            AppText.text(TextKey.Copy5PhutE98d21),
        ),
    )

data class FocusSession(
    val id: String,
    val taskId: Long,
    val title: String,
    val date: String,
    val targetSeconds: Long,
    val activeSeconds: Long,
    val state: String,
    val breakPrompt: Boolean = false,
) {
    val remaining get() = (targetSeconds - activeSeconds).coerceAtLeast(0)
}

data class Achievement(
    val id: String,
    val title: String,
    val criterion: String,
    val progress: Int,
    val target: Int,
    val unlockedAt: Long = 0,
)

data class MemoryEvent(
    val id: String,
    val type: String,
    val entity: Long,
    val title: String,
    val at: Long,
    val note: String = "",
)

data class ReviewDay(
    val date: String,
    val state: String,
)

data class TaskInsight(
    val taskId: Long,
    val title: String,
    val days: List<ReviewDay>,
    val rate: Int?,
    val streak: Int,
    val longest: Int,
    val commonHour: String,
    val focusMinutes: Long,
    val suggestion: String,
)

data class WeeklyReview(
    val start: String,
    val scheduled: Int,
    val completed: Int,
    val previousRate: Int?,
    val focusMinutes: Long,
    val earned: Int,
    val spent: Int,
    val careDays: Int,
    val milestones: Int,
    val consistent: String,
    val improve: String,
    val reflection: String,
) {
    val rate get() = if (scheduled == 0)null else completed * 100 / scheduled
}

data class ReminderPreference(
    val taskId: Long,
    val mode: String = "OFF",
    val minute: Int = 540,
    val followUp: Boolean = false,
)

data class EngagementSettings(
    val paused: Boolean = true,
    val quietStart: Int = 1320,
    val quietEnd: Int = 420,
    val treeReminders: Boolean = false,
    val reducedMotion: Boolean = false,
    val breakPrompts: Boolean = false,
)

data class ReminderDelivery(
    val taskId: Long,
    val date: String,
    val kind: String,
    val at: Long,
    val title: String,
    val timer: Boolean,
)

data class EngagementSnapshot(
    val onboarded: Boolean = true,
    val focus: List<FocusSession> = emptyList(),
    val review: WeeklyReview? = null,
    val achievements: List<Achievement> = emptyList(),
    val memories: List<MemoryEvent> = emptyList(),
    val days: List<ReviewDay> = emptyList(),
    val insights: List<TaskInsight> = emptyList(),
    val settings: EngagementSettings = EngagementSettings(),
    val reminders: List<ReminderPreference> = emptyList(),
    val templates: Set<String> = emptySet(),
    val protectionTickets: Int = 0,
    val tomorrow: String = "",
    val focusCompleted: Int = 0,
)

sealed interface EngagementAction {
    data class Onboard(
        val seed: String,
        val focus: String,
        val templates: List<String>,
        val key: String,
        val skip: Boolean = false,
        val reminders: Boolean = false,
    ) : EngagementAction

    data class Templates(
        val ids: List<String>,
        val key: String,
    ) : EngagementAction

    data class StartFocus(
        val taskId: Long = 0,
        val minutes: Int = 25,
        val key: String,
    ) : EngagementAction

    data class Focus(
        val id: String,
        val action: String,
    ) : EngagementAction

    data class Rest(
        val date: String,
    ) : EngagementAction

    data class Protect(
        val date: String,
        val key: String,
    ) : EngagementAction

    data class BuyProtection(
        val key: String,
    ) : EngagementAction

    data class Reflection(
        val week: String,
        val note: String,
    ) : EngagementAction

    data class MemoryNote(
        val id: String,
        val note: String,
    ) : EngagementAction

    data class Reminder(
        val preference: ReminderPreference,
    ) : EngagementAction

    data class Settings(
        val value: EngagementSettings,
    ) : EngagementAction
}
