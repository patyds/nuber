package com.example.nuber


import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_nuber_shopping.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class NuberShoppingFragment : Fragment() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation : Location
    private lateinit var saladSpinner : View

    var salads_items = arrayOf("Caesar Salad", "Italian Salad", "Mexican Salad")
    var salads_descriptions = arrayOf("Ensalada estilo césar", "Ensalada tipo italiana", "Ensalada a la mexicana")
    var selected_salad : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // saladSpinner = inflater!!.inflate(R.layout.fragment_nuber_shopping, salad_spinner, false)
        saladSpinner = salad_spinner
        (saladSpinner as Spinner).adapter = ArrayAdapter(activity!!,
                R.layout.support_simple_spinner_dropdown_item, resources.getStringArray(R.array.salads_array)) as SpinnerAdapter?
        (saladSpinner as Spinner).onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                selected_salad = position
                description_textView.text = resources.getStringArray(R.array.salads_descriptions)[selected_salad]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }

        }

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nuber_shopping, container, false)
    }

    @SuppressLint("MissingPermission")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        fusedLocationClient.lastLocation.addOnSuccessListener(activity!!) { location ->
            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)

                send_salad_button.setOnClickListener {
                    val  _db = FirebaseDatabase.getInstance().getReference("salads")
                    val k = _db.push().key

                    val s = Salad(salads_items[selected_salad],
                        description_textView.text.toString(), currentLatLng.toString(), k.toString())

                    _db.child(k!!).setValue(s).addOnCompleteListener {
                        Toast.makeText(activity, "Your purchase has been placed", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    companion object {
        fun newInstance(): NuberShoppingFragment = NuberShoppingFragment()
    }
}
