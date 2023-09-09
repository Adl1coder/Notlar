package com.example.notlar.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.notlar.R
import com.example.notlar.databinding.ActivityMainBinding
import com.example.notlar.databinding.FragmentSignUpBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth

class SignUpFragment : Fragment() {

    private lateinit var navController: NavController
    private lateinit var mAuth: FirebaseAuth
    private lateinit var binding: FragmentSignUpBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Fragment için görünümünün oluşturulması
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Gerekli bileşenlerin başlatılması
        init(view)

        // "Giriş Yap" metni tıklandığında, ilgili fragment'a git
        binding.textViewSignIn.setOnClickListener {
            navController.navigate(R.id.action_signUpFragment_to_signInFragment)
        }

        // "İleri" düğmesine tıklanıldığında kullanıcı kaydını oluştur
        binding.nextBtn.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passEt.text.toString()
            val verifyPass = binding.verifyPassEt.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty() && verifyPass.isNotEmpty()) {
                if (pass == verifyPass) {

                    // Kullanıcıyı kaydet
                    registerUser(email, pass)

                } else {
                    // Şifreler eşleşmiyorsa kullanıcıya uyarı ver
                    Toast.makeText(context, "Şifreler aynı değil", Toast.LENGTH_SHORT).show()
                }
            } else
            // Boş alanlar izin verilmezse kullanıcıya uyarı ver
                Toast.makeText(context, "Boş alanlar izin verilmez", Toast.LENGTH_SHORT).show()
        }

    }

    private fun registerUser(email: String, pass: String) {
        // Firebase üzerinde kullanıcı kaydı oluştur
        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
            if (it.isSuccessful)
            // Başarılı kayıt durumunda ana ekrana yönlendir
                navController.navigate(R.id.action_signUpFragment_to_homeFragment)
            else
            // Kayıt başarısızsa hata mesajını kullanıcıya göster
                Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()

        }
    }

    private fun init(view: View) {
        // NavController ve FirebaseAuth'in başlatılması
        navController = Navigation.findNavController(view)
        mAuth = FirebaseAuth.getInstance()
    }
}
