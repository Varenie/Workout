package com.example.workout.main.Adapters

interface ItemTouchHelperAdapter { //интерфекйс для swipe b drag&drop

    fun onItemMove(fromPosition: Int, toPosition: Int)

    fun onItemDismiss(position: Int)
}