package com.anahit.mediaplayer.core.ui

import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * A [ViewBinding] property delegate for fragments that clears itself on `onDestroyView`, so the
 * binding (and the views it holds) can't leak past the fragment's view lifecycle.
 */
fun <T : ViewBinding> Fragment.viewBinding(bind: (View) -> T): ReadOnlyProperty<Fragment, T> =
    FragmentViewBindingDelegate(this, bind)

private class FragmentViewBindingDelegate<T : ViewBinding>(
    fragment: Fragment,
    private val bind: (View) -> T,
) : ReadOnlyProperty<Fragment, T> {
    private var binding: T? = null

    init {
        fragment.viewLifecycleOwnerLiveData.observeForever { owner ->
            owner?.lifecycle?.addObserver(
                LifecycleEventObserver { _, event ->
                    if (event == Lifecycle.Event.ON_DESTROY) {
                        binding = null
                    }
                },
            )
        }
    }

    override fun getValue(
        thisRef: Fragment,
        property: KProperty<*>,
    ): T = binding ?: bind(thisRef.requireView()).also { binding = it }
}
