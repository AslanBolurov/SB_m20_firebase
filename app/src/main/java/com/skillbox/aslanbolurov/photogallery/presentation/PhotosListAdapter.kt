package com.skillbox.aslanbolurov.photogallery.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.skillbox.aslanbolurov.photogallery.data.Photo
import com.skillbox.aslanbolurov.photogallery.databinding.ItemForRecyclerviewBinding

class PhotosListAdapter: ListAdapter<Photo, PhotosHolder>(DiffUtilCallback()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotosHolder {
        return PhotosHolder(
            ItemForRecyclerviewBinding.inflate(
                LayoutInflater.from(parent.context),parent,false)
        )
    }

    override fun onBindViewHolder(holder: PhotosHolder, position: Int) {
        var item=getItem(position)
        with(holder.recycler_binding){
            tvDate.text=item.date
            this?.let {
                Glide
                    .with(imageViewPhoto.context)
                    .load(item.path)
                    .into(imageViewPhoto)
            }
        }


    }

}

class DiffUtilCallback: DiffUtil.ItemCallback<Photo>() {
    override fun areItemsTheSame(oldItem: Photo, newItem: Photo): Boolean {
        return oldItem.id==newItem.id
    }

    override fun areContentsTheSame(oldItem: Photo, newItem: Photo): Boolean {
        return oldItem==newItem
    }


}

class PhotosHolder(
    val recycler_binding: ItemForRecyclerviewBinding
): RecyclerView.ViewHolder(recycler_binding.root)
