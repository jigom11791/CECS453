package com.jose_gomez08.criminalintent

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import java.io.File
import java.util.*

private const val TAG = "JGCrimeFragment"
private const val JG_ARG_CRIME_ID = "jg_crime_id"
private const val JG_DIALOG_DATE = "DialogDate"
private const val JG_REQUEST_DATE = 0
private const val JG_REQUEST_CONTACT = 1
private const val JG_REQUEST_PHOTO = 2
private const val JG_DATE_FORMAT = "EEE, MMM, dd"

class JGCrimeFragment : Fragment(), JGDatePickerFragment.Callbacks {

    private lateinit var jgCrime: Crime
    private lateinit var jgPhotoFile: File
    private lateinit var jgPhotoUri: Uri
    private lateinit var jgTitleField: EditText
    private lateinit var jgDateButton: Button
    private lateinit var jgSolvedCheckBox: CheckBox
    private lateinit var jgReportButton: Button
    private lateinit var jgSuspectButton: Button
    private lateinit var jgPhotoButton: ImageButton
    private lateinit var jgPhotoView: ImageView
    private val jgCrimeDetailViewModel: JGCrimeDetailViewModel by lazy {
        ViewModelProviders.of(this).get(JGCrimeDetailViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        jgCrime = Crime()
        val jgCrimeId: UUID = arguments?.getSerializable(JG_ARG_CRIME_ID) as UUID
        jgCrimeDetailViewModel.jgLoadCrime(jgCrimeId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val jgView = inflater.inflate(R.layout.fragment_crime, container, false)

        jgTitleField = jgView.findViewById(R.id.jg_crime_title) as EditText
        jgDateButton = jgView.findViewById(R.id.jg_crime_date) as Button
        jgSolvedCheckBox = jgView.findViewById(R.id.jg_crime_solved) as CheckBox
        jgReportButton = jgView.findViewById(R.id.jg_crime_report) as Button
        jgSuspectButton = jgView.findViewById(R.id.jg_crime_suspect) as Button
        jgPhotoButton = jgView.findViewById(R.id.jg_crime_camera) as ImageButton
        jgPhotoView = jgView.findViewById(R.id.jg_crime_photo) as ImageView

        return jgView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val jgCrimeId = arguments?.getSerializable(JG_ARG_CRIME_ID) as UUID
        jgCrimeDetailViewModel.jgCrimeLiveData.observe(
            viewLifecycleOwner,
            Observer { jgCrime ->
                jgCrime?.let {
                    this.jgCrime = jgCrime
                    jgPhotoFile = jgCrimeDetailViewModel.jgGetPhotoFile(jgCrime)
                    jgPhotoUri = FileProvider.getUriForFile(requireActivity(),
                    "com.jose_gomez08.criminalintent.fileprovider",
                    jgPhotoFile)
                    jgUpdateUI()
                }
            })
    }

    override fun onStart() {
        super.onStart()

        val jgTitleWatcher = object : TextWatcher {

            override fun beforeTextChanged(
                sequence: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                // This space intentionally left blank
            }

            override fun onTextChanged(
                sequence: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                jgCrime.title = sequence.toString()
            }

            override fun afterTextChanged(sequence: Editable?) {
                // This one too
            }
        }
        jgTitleField.addTextChangedListener(jgTitleWatcher)

        jgSolvedCheckBox.apply {
            setOnCheckedChangeListener { _, isChecked ->
                jgCrime.isSolved = isChecked
            }
        }

        jgDateButton.setOnClickListener {
            JGDatePickerFragment.jgNewInstance(jgCrime.date).apply {
                setTargetFragment(this@JGCrimeFragment, JG_REQUEST_DATE)
                show(this@JGCrimeFragment.requireFragmentManager(), JG_DIALOG_DATE)
            }
        }

        jgReportButton.setOnClickListener {
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, jgGetCrimeReport())
                putExtra(
                    Intent.EXTRA_SUBJECT,
                    getString(R.string.jg_crime_report_subject))
            }.also { intent ->
                val jgChooserIntent =
                    Intent.createChooser(intent, getString(R.string.jg_send_report))
                startActivity(jgChooserIntent)
            }
        }

        jgSuspectButton.apply {
            val jgPickContactIntent =
                Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)

            setOnClickListener {
                startActivityForResult(jgPickContactIntent, JG_REQUEST_CONTACT)
            }

            val jgPackageManager: PackageManager = requireActivity().packageManager
            val jgResolvedActivity: ResolveInfo? =
                jgPackageManager.resolveActivity(jgPickContactIntent,
                    PackageManager.MATCH_DEFAULT_ONLY)
            if (jgResolvedActivity == null) {
                isEnabled = false
            }
        }

        jgPhotoButton.apply {
            val jgPackageManager: PackageManager = requireActivity().packageManager
            val jgCaptureImage = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val jgResolvedActivity: ResolveInfo? =
                jgPackageManager.resolveActivity(jgCaptureImage,
                PackageManager.MATCH_DEFAULT_ONLY)
            if(jgResolvedActivity == null) {
                isEnabled = false
            }

            setOnClickListener {
                jgCaptureImage. putExtra(MediaStore.EXTRA_OUTPUT, jgPhotoUri)

                val jgCameraActivities: List<ResolveInfo> =
                    jgPackageManager.queryIntentActivities(jgCaptureImage,
                    PackageManager.MATCH_DEFAULT_ONLY)

                for (jgCameraActivity in jgCameraActivities) {
                    requireActivity().grantUriPermission(
                        jgCameraActivity.activityInfo.packageName,
                        jgPhotoUri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                }
                startActivityForResult(jgCaptureImage, JG_REQUEST_PHOTO)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        jgCrimeDetailViewModel.jgSaveCrime(jgCrime)
    }

    override fun onDetach() {
        super.onDetach()
        requireActivity().revokeUriPermission(
            jgPhotoUri,
            Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        )
    }

    override fun jgOnDateSelected(date: Date) {
        jgCrime.date = date
        jgUpdateUI()
    }

    private fun jgUpdateUI() {
        jgTitleField.setText(jgCrime.title)
        jgDateButton.text = jgCrime.date.toString()
        jgSolvedCheckBox.apply {
            isChecked = jgCrime.isSolved
            jumpDrawablesToCurrentState()
        }
        if (jgCrime.suspect.isNotEmpty()) {
            jgSuspectButton.text = jgCrime.suspect
        }
        jgUpdatePhotoView()
    }

    private fun jgUpdatePhotoView() {
        if(jgPhotoFile.exists()) {
            val jgBitmap = jgGetScaledBitmap(jgPhotoFile.path, requireActivity())
            jgPhotoView.setImageBitmap(jgBitmap)
            jgPhotoView.contentDescription =
                    getString(R.string.jg_crime_photo_image_description)
        } else {
            jgPhotoView.setImageDrawable(null)
            jgPhotoView.contentDescription =
                    getString(R.string.jg_crime_photo_no_image_description)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when {
            resultCode != Activity.RESULT_OK -> return

            requestCode == JG_REQUEST_CONTACT && data != null -> {
                val jgContactUri: Uri? = data.data
                // Specify which fields you want your query to return values for.
                val jgQueryFields = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)
                // Perform your query - the contactUri is like a "where" clause here
                val jgCursor = jgContactUri?.let {
                    requireActivity().contentResolver
                        .query(it, jgQueryFields, null, null, null)
                }
                jgCursor?.use {
                    // Double-check that you actually got results
                    if (it.count == 0) {
                        return
                    }

                    // Pull out the first column of the first row of data -
                    // that is your suspect's name.
                    it.moveToFirst()
                    val jgSuspect = it.getString(0)
                    jgCrime.suspect = jgSuspect
                    jgCrimeDetailViewModel.jgSaveCrime(jgCrime)
                    jgSuspectButton.text = jgSuspect
                }
            }

            requestCode == JG_REQUEST_PHOTO -> {
                requireActivity().revokeUriPermission(
                    jgPhotoUri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
                jgUpdatePhotoView()
            }
        }
    }

    private fun jgGetCrimeReport(): String {
        val jgSolvedString = if (jgCrime.isSolved) {
            getString(R.string.jg_crime_report_solved)
        } else {
            getString(R.string.jg_crime_report_unsolved)
        }

        val jgDateString = DateFormat.format(JG_DATE_FORMAT, jgCrime.date).toString()
        val suspect = if (jgCrime.suspect.isBlank()) {
            getString(R.string.jg_crime_report_no_suspect)
        } else {
            getString(R.string.jg_crime_report_suspect, jgCrime.suspect)
        }

        return getString(R.string.jg_crime_report,
            jgCrime.title, jgDateString, jgSolvedString, suspect)
    }

    companion object {

        fun newInstance(crimeId: UUID): JGCrimeFragment {
            val args = Bundle().apply {
                putSerializable(JG_ARG_CRIME_ID, crimeId)
            }
            return JGCrimeFragment().apply {
                arguments = args
            }
        }
    }
}