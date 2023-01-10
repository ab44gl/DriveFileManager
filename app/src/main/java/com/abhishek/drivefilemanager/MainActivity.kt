package com.abhishek.drivefilemanager

import android.accounts.Account
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.abhishek.drivefilemanager.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity() {
    private  lateinit var binding:ActivityMainBinding
    lateinit var driveHelper: DriveHelper
    private val globalStateViewModel:GlobalStateViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
       super.onCreate(savedInstanceState)
       binding=ActivityMainBinding.inflate(layoutInflater)
       setContentView(binding.root)
       //-------------------------------------
        setSupportActionBar(binding.toolbar)

        //supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.apply {

        }


        //livedata
        globalStateViewModel.toolbarState.observe(this){
            Utils.logD("toolbar ${it.name}")
            updateToolbar(it)
        }
        //backButton
        globalStateViewModel.showBackButton.observe(this){
            Utils.logD("backButton $it")
            if(it)
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
            else
                supportActionBar?.setDisplayHomeAsUpEnabled(false)

        }

        //signIn
        requestSignIn()

    }



    override fun onStart() {
        super.onStart()
        //dialog
        //showInputDialog()
    }
     suspend fun showMessageToast(msg: String) {
        withContext(Dispatchers.Main){
            Toast.makeText(this@MainActivity,msg,Toast.LENGTH_SHORT).show()
        }
    }
    private fun showInputDialog() {
        val dialog=InputDialog()
        dialog.setOnClickListener { text, isOk ->
            Utils.logD("$text    $isOk")
            //lets create a folder
            lifecycleScope.launch(Dispatchers.IO){
                if(text.isNotEmpty()) {
                    try {
                        val res=driveHelper.createFolder(text)
                        showMessageToast("folder created")
                    }catch (e:Exception){
                        showMessageToast("folder not created")
                        Utils.logD("error",e)
                    }

                }
            }
        }
        dialog.show(supportFragmentManager,"Dialog")


    }

    private fun updateToolbar(state:ToolbarState){
        val toolbarLayout=binding.toolbarInclude.layoutIncludeToolbar
        toolbarLayout.setLayoutVisible(state.ordinal)
    }
    override fun onSupportNavigateUp(): Boolean {
        Utils.logD("back")
        onBackPressedDispatcher.onBackPressed()
        return true
    }
    //google
    private fun requestSignIn() {
        val lastSignIn = GoogleSignIn.getLastSignedInAccount(this)
        if (lastSignIn == null) {
            val signInOptions =
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .requestScopes(Scope(DriveScopes.DRIVE_FILE))
                    .build()
            val client = GoogleSignIn.getClient(this, signInOptions)
            signInResultLauncher.launch(client.signInIntent)
        } else {
            lastSignIn.account?.let { initDriveHelper(it) }
        }
    }

    private fun initDriveHelper(account: Account) {
        val credential = GoogleAccountCredential.usingOAuth2(
            this, arrayListOf(DriveScopes.DRIVE_FILE)
        )
        credential.selectedAccount = account
        val googleDriveService = Drive.Builder(
            AndroidHttp.newCompatibleTransport(),
            GsonFactory(),
            credential
        )
            .setApplicationName(resources.getString(R.string.app_name))
            .build()
        driveHelper = DriveHelper(googleDriveService)
    }
    private val signInResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                handleSignInResult(it.data)
            }
        }

    private fun handleSignInResult(data: Intent?) {
        GoogleSignIn.getSignedInAccountFromIntent(data)
            .addOnSuccessListener {
                Utils.logD("Signed in as " + it.email)
                // Use the authenticated account to sign in to the Drive service.
                it.account?.let { it1 -> initDriveHelper(it1) }
            }
            .addOnFailureListener {
                Utils.logD("unable to sign in", it)
            }

    }



}

