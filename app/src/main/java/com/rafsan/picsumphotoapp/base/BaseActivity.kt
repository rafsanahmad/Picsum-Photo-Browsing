package com.rafsan.picsumphotoapp.base

import android.os.Bundle
import android.widget.Toast
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.rafsan.picsumphotoapp.ext.observe

abstract class BaseActivity<VB : ViewBinding, VM : BaseViewModel> : AppCompatActivity() {

    lateinit var binding: VB
    lateinit var viewModel: VM

    abstract fun getVM(): VM
    abstract fun bindVM(binding: VB, vm: VM)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = this.setBinding()
        setContentView(binding.root)
        viewModel = getVM()
        bindVM(binding, viewModel)
        with(viewModel) {
            observe(errorMessage) { msg ->
                Toast.makeText(
                    this@BaseActivity,
                    msg,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        //ready view to use
        onViewReady(savedInstanceState)
    }

    abstract fun setBinding(): VB

    @CallSuper
    protected open fun onViewReady(savedInstanceState: Bundle?) {
        // use this method in child activity
    }

    fun launchOnLifecycleScope(execute: suspend () -> Unit) {
        lifecycleScope.launchWhenCreated {
            execute()
        }
    }
}