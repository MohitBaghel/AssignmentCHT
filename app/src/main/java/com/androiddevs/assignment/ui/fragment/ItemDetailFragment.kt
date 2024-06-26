package com.androiddevs.assignment.ui.fragment

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.androiddevs.assignment.R
import com.androiddevs.assignment.ui.viewmodel.ItemViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics

class ItemDetailFragment : Fragment() {

    private val args: ItemDetailFragmentArgs by navArgs()
    private val viewModel: ItemViewModel by activityViewModels()

    private lateinit var titleEditText: EditText
    private lateinit var descriptionTextView: TextView
    private lateinit var imageView: ImageView
    private var selectedImageUri: Uri? = null
    private val PICK_IMAGE_REQUEST = 1

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_item_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())

        titleEditText = view.findViewById(R.id.detailTitle)
        descriptionTextView = view.findViewById(R.id.detailDescription)
        imageView = view.findViewById(R.id.detailImage)
        val selectImageButton: Button = view.findViewById(R.id.selectImageButton)
        val shareButton: Button = view.findViewById(R.id.shareButton)

        val item = args.item
        titleEditText.setText(item.title)
        descriptionTextView.text = item.description
        if (item.imageUri != null) {
            imageView.setImageURI(item.imageUri)
        } else {
            imageView.setImageResource(R.drawable.android)
        }

        selectImageButton.setOnClickListener {
            pickImageFromGallery()
        }

        shareButton.setOnClickListener {
            if (resources.getBoolean(R.bool.is_tv)) {
                shareToFacebook()
            } else {
                shareContent()
            }
        }

        // Handle back button press
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                hideKeyboard()
                isEnabled = false
                requireActivity().onBackPressed()
            }
        })
    }

    private fun hideKeyboard() {
        val inputMethodManager = requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    private fun shareContent() {
        val title = titleEditText.text.toString()
        val description = descriptionTextView.text.toString()

        // Create Intent to share content
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "image/*"

        // Check if there is image to share
        if (selectedImageUri != null) {
            shareIntent.putExtra(Intent.EXTRA_STREAM, selectedImageUri)
        }

        // Add text
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, title)
        shareIntent.putExtra(Intent.EXTRA_TEXT, description)

        // Choose App
        val chooser = Intent.createChooser(shareIntent, "Share via")
        val resolveInfo = shareIntent.resolveActivity(requireActivity().packageManager)

        if (resolveInfo != null) {
            // Get the package name
            val packageName = resolveInfo.packageName
            // Map the package name to a user-friendly name
            val socialMediaName = getSocialMediaName(packageName)

            Log.d("TAG", socialMediaName)

            // Log analytics event for content shared
            logShareEvent(socialMediaName, title, description)
            startActivity(chooser)
        } else {
            Snackbar.make(requireView(), "No apps available to share content.", Snackbar.LENGTH_LONG).show()
        }
    }

    private fun shareToFacebook() {
        val title = titleEditText.text.toString()
        val description = descriptionTextView.text.toString()

        // Check if Facebook app is installed
        if (isFacebookInstalled()) {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "image/*"
            shareIntent.setPackage("com.facebook.katana")

            if (selectedImageUri != null) {
                shareIntent.putExtra(Intent.EXTRA_STREAM, selectedImageUri)
            }

            shareIntent.putExtra(Intent.EXTRA_SUBJECT, title)
            shareIntent.putExtra(Intent.EXTRA_TEXT, description)

            startActivity(shareIntent)
        } else {
            Snackbar.make(requireView(), "Facebook app is not installed.", Snackbar.LENGTH_LONG).show()
        }
    }

    private fun isFacebookInstalled(): Boolean {
        return try {
            requireActivity().packageManager.getPackageInfo("com.facebook.katana", 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    private fun getSocialMediaName(packageName: String): String {
        return when (packageName) {
            "com.facebook.katana" -> "Facebook"
            "com.instagram.android" -> "Instagram"
            "com.twitter.android" -> "Twitter"
            "com.whatsapp" -> "WhatsApp"
            "com.linkedin.android" -> "LinkedIn"
            else -> "Other"
        }
    }

    private fun logShareEvent(socialMedia: String, title: String, description: String) {
        val isTv = resources.getBoolean(R.bool.is_tv)
        val source = if (isTv) "Television" else "Mobile"

        // Log analytics event for sharing content
        val params = Bundle().apply {
            putString("social_media", socialMedia)
            putString("title", title)
            putString("description", description)
            putString("source", source)
        }
        firebaseAnalytics.logEvent("content_shared", params)
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data
            selectedImageUri?.let {
                imageView.setImageURI(it)
            }
        }
    }
}
