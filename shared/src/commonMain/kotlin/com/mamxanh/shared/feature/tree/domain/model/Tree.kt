package com.mamxanh.shared.feature.tree.domain.model
typealias Tree = com.mamxanh.shared.model.Tree
data class TreeSpecies(val id: String,val name: String)
val treeSpeciesCatalog = com.mamxanh.shared.model.catalog.map{TreeSpecies(it.id,it.name)}
