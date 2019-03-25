package com.example.nuber

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_nuber_products.*


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [NuberProductsFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [NuberProductsFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class NuberProductsFragment : Fragment() {
    // TODO: Rename and change types of parameters

    private val salads = listOf<Salad>(Salad("pollo", "Best", "899"))
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nuber_products, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        list_recycler_view.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = NuberProductsAdapter(salads)
        }
    }


}
