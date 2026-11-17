package com.mamxanh.shared.feature.home.domain.model

import com.mamxanh.shared.localization.TextKey

import com.mamxanh.shared.localization.AppText

data class HomeSpecies(val id: String, val name: String, val emoji: String)
val homeSpeciesCatalog get()= listOf(
    HomeSpecies("succulent", AppText.text(TextKey.CopySenDaNgocAba46f), "🪴"),
    HomeSpecies("monstera", AppText.text(TextKey.CopyTrauBaLaXeF22d98), "🌿"),
    HomeSpecies("ficus", AppText.text(TextKey.CopyCayBangBupEb6814), "🌳"),
    HomeSpecies("snake", AppText.text(TextKey.CopyLuoiHoXanh998396), "🎋"),
    HomeSpecies("cactus", AppText.text(TextKey.CopyXuongRongHoa723f84), "🌵"),
    HomeSpecies("aloe", AppText.text(TextKey.CopyNhaDamMongD63ca0), "🌱"),
    HomeSpecies("lavender", AppText.text(TextKey.CopyOaiHuongTim2ffaf9), "🪻"),
    HomeSpecies("pilea", AppText.text(TextKey.CopyCoDongTienF85b17), "🪙"),
    HomeSpecies("cherry", AppText.text(TextKey.CopyDaoHongXuanC2fd99), "🌸"),
    HomeSpecies("pine", AppText.text(TextKey.CopyThongXanhBiecBb5db6), "🌲")
)
