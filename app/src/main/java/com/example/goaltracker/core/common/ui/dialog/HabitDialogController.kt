package com.example.goaltracker.core.common.ui.dialog


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.goaltracker.core.model.Habit

class HabitDialogController {
    var isAddOpen by mutableStateOf(false)
        private set

    var isEditOpen by mutableStateOf(false)
        private set

    var isDeleteOpen by mutableStateOf(false)
        private set

    var selectedHabit: Habit?=null
        private set

    fun openAddDialog(){
        isAddOpen=true
    }
    fun closeAddDialog(){
        isAddOpen=false
    }
    fun openEditDialog(habit:Habit){
        selectedHabit=habit
        isEditOpen=true
    }
    fun closeEditDialog(){
        selectedHabit=null
        isEditOpen=false
    }
    fun openDeleteConfirm(habit:Habit){
        selectedHabit=habit
        isDeleteOpen=true
    }
    fun closeDeleteConfirm(){
        selectedHabit=null
        isDeleteOpen=false
    }
}