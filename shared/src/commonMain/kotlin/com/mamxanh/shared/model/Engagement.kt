package com.mamxanh.shared.model

data class HabitTemplate(val id:String,val config:TaskConfig,val effort:String)
val habitTemplates=listOf(
    HabitTemplate("study25",TaskConfig("Học tập trung 25 phút","HABIT","Học tập",verification="TIMER",minutes=25),"25 phút"),
    HabitTemplate("review15",TaskConfig("Ôn lại bài cũ","HABIT","Học tập",verification="TIMER",minutes=15),"15 phút"),
    HabitTemplate("read20",TaskConfig("Đọc sách","HABIT","Học tập",verification="TIMER",minutes=20),"20 phút"),
    HabitTemplate("words",TaskConfig("Học 10 từ vựng mới","HABIT","Học tập"),"10 từ"),
    HabitTemplate("evening",TaskConfig("Học buổi tối","HABIT","Học tập",verification="TIMER",minutes=25,startMinute=1080,endMinute=1380),"25 phút"),
    HabitTemplate("walk20",TaskConfig("Đi bộ nhẹ nhàng","HABIT","Sức khỏe",verification="TIMER",minutes=20),"20 phút"),
    HabitTemplate("stretch5",TaskConfig("Giãn cơ","HABIT","Sức khỏe",verification="TIMER",minutes=5),"5 phút"),
    HabitTemplate("water",TaskConfig("Uống đủ nước","HABIT","Sức khỏe"),"Trong ngày"),
    HabitTemplate("fruit",TaskConfig("Ăn thêm rau hoặc trái cây","HABIT","Sức khỏe"),"Một phần"),
    HabitTemplate("brush-am",TaskConfig("Đánh răng buổi sáng","HABIT","Nếp sống",startMinute=360,endMinute=540),"06:00–09:00"),
    HabitTemplate("brush-pm",TaskConfig("Đánh răng buổi tối","HABIT","Nếp sống",startMinute=1080,endMinute=1380),"18:00–23:00"),
    HabitTemplate("bed",TaskConfig("Dọn giường, gấp chăn","HABIT","Việc nhà"),"3 phút"),
    HabitTemplate("dishes",TaskConfig("Rửa chén sau bữa ăn","HABIT","Việc nhà"),"10 phút"),
    HabitTemplate("room",TaskConfig("Dọn phòng cuối tuần","HABIT","Việc nhà",frequency="WEEKENDS"),"20 phút"),
    HabitTemplate("prepare",TaskConfig("Chuẩn bị cặp sách cho ngày mai","HABIT","Học tập"),"5 phút")
)
data class FocusSession(val id:String,val taskId:Long,val title:String,val date:String,val targetSeconds:Long,
    val activeSeconds:Long,val state:String,val breakPrompt:Boolean=false) {
    val remaining get()=(targetSeconds-activeSeconds).coerceAtLeast(0)
}
data class Achievement(val id:String,val title:String,val criterion:String,val progress:Int,val target:Int,
    val unlockedAt:Long=0)
data class MemoryEvent(val id:String,val type:String,val entity:Long,val title:String,val at:Long,val note:String="")
data class ReviewDay(val date:String,val state:String)
data class TaskInsight(val taskId:Long,val title:String,val days:List<ReviewDay>,val rate:Int?,val streak:Int,
    val longest:Int,val commonHour:String,val focusMinutes:Long,val suggestion:String)
data class WeeklyReview(val start:String,val scheduled:Int,val completed:Int,val previousRate:Int?,
    val focusMinutes:Long,val earned:Int,val spent:Int,val careDays:Int,val milestones:Int,
    val consistent:String,val improve:String,val reflection:String) {
    val rate get()=if(scheduled==0)null else completed*100/scheduled
}
data class ReminderPreference(val taskId:Long,val mode:String="OFF",val minute:Int=540,val followUp:Boolean=false)
data class EngagementSettings(val paused:Boolean=true,val quietStart:Int=1320,val quietEnd:Int=420,
    val treeReminders:Boolean=false,val reducedMotion:Boolean=false,val breakPrompts:Boolean=false)
data class ReminderDelivery(val taskId:Long,val date:String,val kind:String,val at:Long,val title:String,val timer:Boolean)
data class EngagementSnapshot(val onboarded:Boolean=true,val focus:List<FocusSession> = emptyList(),
    val review:WeeklyReview?=null,val achievements:List<Achievement> = emptyList(),val memories:List<MemoryEvent> = emptyList(),
    val days:List<ReviewDay> = emptyList(),val insights:List<TaskInsight> = emptyList(),
    val settings:EngagementSettings=EngagementSettings(),val reminders:List<ReminderPreference> = emptyList(),
    val templates:Set<String> = emptySet(),val protectionTickets:Int=0,val tomorrow:String="",val focusCompleted:Int=0)
sealed interface EngagementAction {
    data class Onboard(val seed:String,val focus:String,val templates:List<String>,val key:String,val skip:Boolean=false,val reminders:Boolean=false):EngagementAction
    data class Templates(val ids:List<String>,val key:String):EngagementAction
    data class StartFocus(val taskId:Long=0,val minutes:Int=25,val key:String):EngagementAction
    data class Focus(val id:String,val action:String):EngagementAction
    data class Rest(val date:String):EngagementAction
    data class Protect(val date:String,val key:String):EngagementAction
    data class BuyProtection(val key:String):EngagementAction
    data class Reflection(val week:String,val note:String):EngagementAction
    data class MemoryNote(val id:String,val note:String):EngagementAction
    data class Reminder(val preference:ReminderPreference):EngagementAction
    data class Settings(val value:EngagementSettings):EngagementAction
}
