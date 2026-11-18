package com.mamxanh.app.core.presentation.navigation

class HabiTreeNavigator<Key : HabiTreeNavKey>(private val stack: MutableList<Key>) {
    init {
        require(stack.isNotEmpty()) { "A navigation stack requires a root" }
    }

    val backStack: List<Key> get() = stack
    val current: Key get() = stack.last()
    val previous: Key? get() = stack.getOrNull(stack.lastIndex - 1)

    fun goTo(destination: Key, replaceStack: Boolean = false) {
        if (replaceStack) {
            stack.clear()
            stack.add(destination)
        } else if (destination != current) {
            stack.add(destination)
        }
    }

    fun replaceCurrent(destination: Key) {
        stack[stack.lastIndex] = destination
    }

    fun goBack(fallbackRoot: Key) {
        if (stack.size > 1) stack.removeAt(stack.lastIndex)
        else stack[0] = fallbackRoot
    }
}
