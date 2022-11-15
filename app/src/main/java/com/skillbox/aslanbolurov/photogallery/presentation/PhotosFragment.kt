package com.skillbox.aslanbolurov.photogallery.presentation

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.messaging.FirebaseMessaging
import com.skillbox.aslanbolurov.photogallery.R
import com.skillbox.aslanbolurov.photogallery.data.App
import com.skillbox.aslanbolurov.photogallery.data.PhotoDao
import com.skillbox.aslanbolurov.photogallery.databinding.FragmentPhotosBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


class PhotosFragment : Fragment() {



    private val viewModel: PhotosViewModel by viewModels{
        object: ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val photoDao: PhotoDao =
                    (requireActivity().application as App).db.photoDao()
                return PhotosViewModel(photoDao) as T
            }
        }
    }


    private var _binding:FragmentPhotosBinding?=null
    val binding get() = _binding!!

    private lateinit var recyclerView:RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding=FragmentPhotosBinding.inflate(inflater,container,false)

        binding.buttonTakePhoto.setOnClickListener {
            findNavController().navigate(R.id.action_PhotosList_to_PhotoTaking)
        }
        binding.buttonGoogleMaps.setOnClickListener {
            findNavController().navigate(R.id.action_PhotosList_to_GoogleMapsFragment)
        }
        binding.buttonThrowException.setOnClickListener{
            createNotification()
            try{
                throw Exception("My first exeption")
            }catch (e:Exception){
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val listAdapter=PhotosListAdapter()

        recyclerView=binding.recyclerView
        recyclerView.adapter=listAdapter

        lifecycleScope.launchWhenStarted {
            viewModel.flowPhotosList.onEach {
                listAdapter.submitList(it)
            }.launchIn(viewLifecycleOwner.lifecycleScope)
        }

        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            Log.d("registration token", it.result.toString())
        }


    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel.refreshloadPhotos()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    fun createNotification(){

        val intent=Intent(requireContext(),MainActivity::class.java)



        val pendingIntent= if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            PendingIntent.getActivity(
                requireContext(),0,intent,PendingIntent.FLAG_IMMUTABLE)
        }else{
            PendingIntent.getActivity(
                requireContext(),0,intent,PendingIntent.FLAG_UPDATE_CURRENT)

        }



        val notification= NotificationCompat.Builder(requireContext(),App.NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("My first notification")
            .setContentText("Description of my first notification")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(requireContext()).notify(NOTIFICATION_ID,notification)


    }

    companion object {
        private const val NOTIFICATION_ID=1000

        fun newInstance() = PhotosFragment()
    }

}