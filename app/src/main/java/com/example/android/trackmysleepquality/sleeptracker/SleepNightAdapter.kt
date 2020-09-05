/*
 * Copyright 2019, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.trackmysleepquality.sleeptracker

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.databinding.ListItemSleepNightBinding
import com.example.android.trackmysleepquality.sleeptracker.SleepNightAdapter.TextViewHolder.Companion.from


private val ITEM_VIEW_TYPE_HEADER = 0
private val ITEM_VIEW_TYPE_ITEM = 1
class SleepNightAdapter(val clickListener: SleepNightListener):
        ListAdapter<SleepNightAdapter.SleepNightListener.DataItem, RecyclerView.ViewHolder>(SleepNightDiffCallback()) {
    fun addHeaderAndSubmitList(list: List<SleepNight>?) {
        val items = when (list) {
            null -> listOf(SleepNightListener.DataItem.Header)
            else -> listOf(SleepNightListener.DataItem.Header) + list.map { SleepNightListener.DataItem.SleepNightItem(it) }
        }
        submitList(items)
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is RecyclerView.ViewHolder -> {
                val nightItem = getItem(position) as SleepNightListener.DataItem.SleepNightItem
                holder.bind(nightItem.sleepNight, clickListener)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): RecyclerView.ViewHolder {
               return when (viewType) {
                   ITEM_VIEW_TYPE_HEADER ->
                       TextViewHolder.from(parent)
                   ITEM_VIEW_TYPE_ITEM -> ViewHolder.from(parent)
                   else -> throw ClassCastException("Unknown viewType ${viewType}")
               }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is SleepNightListener.DataItem.Header -> ITEM_VIEW_TYPE_HEADER
            is SleepNightListener.DataItem.SleepNightItem -> ITEM_VIEW_TYPE_ITEM
        }
               }
    }
        class ViewHolder private constructor(val binding: ListItemSleepNightBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(item: SleepNight, clickListener: SleepNightListener) {
            binding.sleep = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemSleepNightBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}
class TextViewHolder(view: View): RecyclerView.ViewHolder(view) {
    companion object {
        fun from(parent: ViewGroup): TextViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(R.layout.header, parent, false)
            return TextViewHolder(view)
        }
    }
}



class SleepNightDiffCallback : DiffUtil.ItemCallback<SleepNightListener.DataItem>() {

    override fun areItemsTheSame(oldItem: SleepNightListener.DataItem, newItem: SleepNightListener.DataItem): Boolean {
        return oldItem.id == newItem.id
    }
    @SuppressLint("DiffUtilEquals")

    override fun areContentsTheSame(oldItem: SleepNightListener.DataItem, newItem: SleepNightListener.DataItem): Boolean {
        return oldItem == newItem
    }
}


class SleepNightListener(val clickListener: (sleepId: Long) -> Unit) {
    fun onClick(night: SleepNight) = clickListener(night.nightId)
    sealed class DataItem {
        abstract val id: Long
        data class SleepNightItem(val sleepNight: SleepNight): DataItem()      {
            override val id = sleepNight.nightId
        }

        object Header: DataItem() {
            override val id = Long.MIN_VALUE
        }
    }
}
