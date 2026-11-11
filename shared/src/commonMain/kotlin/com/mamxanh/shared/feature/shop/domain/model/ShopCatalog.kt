package com.mamxanh.shared.feature.shop.domain.model

/** UI and transactional catalog share one versioned balance source. */
val shopCatalog = com.mamxanh.shared.model.catalog.map {
    ShopSeed(it.id,it.name,it.emoji,it.description,it.price)
}
