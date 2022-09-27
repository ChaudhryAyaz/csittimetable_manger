package com.ayaz.csittimetableapp
//ALL DONE
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.File
import java.io.FileInputStream
import java.util.*
import kotlin.collections.ArrayList

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class HomeFragment : Fragment() {
    private val filePath =
        File(Environment.getExternalStorageDirectory().toString() + "/Download/Timetable_v4.xlsx")
    var selectedclass: String? = ""
    private lateinit var class_details_array: ArrayList<class_details_data>
    lateinit var listview: ListView
    private var param1: String? = null
    private var param2: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val totalclass_view = view.findViewById<TextView>(R.id.totalclass_view)
        super.onViewCreated(view, savedInstanceState)
        val textView: TextView = view.findViewById(R.id.class_name)
        selectedclass = arguments?.getString("key")
        listview = view.findViewById(R.id.home_listview)
        textView.setOnFocusChangeListener { v, hasFocus ->
            Toast.makeText(requireContext(), "Click Again To Change the Class", Toast.LENGTH_SHORT)
                .show()
        }
        textView.setOnClickListener {
            val prograssbar1 = view.findViewById<ProgressBar>(R.id.progressBar1)
            prograssbar1.visibility = View.VISIBLE
            val fragmentmanger = activity?.supportFragmentManager
            val fragemettransaction = fragmentmanger?.beginTransaction()
            fragemettransaction?.replace(R.id.framelayout, classnamepicker())
            fragemettransaction?.commit()
        }
        var index = 0
        val dayarr =
            arrayOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
        var day_name_view = view.findViewById<AutoCompleteTextView>(R.id.date_view)
        var arrayadapter = ArrayAdapter<String>(requireContext(), R.layout.drop_down_item, dayarr)
        day_name_view.setAdapter(arrayadapter)
        var tempstarting_row = 2
        var ending = 19

        var day_name = Calendar.getInstance(TimeZone.getTimeZone("UTC")).get(Calendar.DAY_OF_WEEK)

        when (day_name) {

            1 -> {
                day_name_view.setText(day_name_view.adapter.getItem(0).toString(), false)
                index = 0
                tempstarting_row = 2
                ending = 19
                totalclass_view.text = "Today Is OFF"
                listview.visibility = GONE
            }
            2 -> {
                day_name_view.setText(day_name_view.adapter.getItem(1).toString(), false)
                index = 0
                tempstarting_row = 2
                ending = 19
            }
            3 -> {
                day_name_view.setText(day_name_view.adapter.getItem(2).toString(), false)
                index = 0
                tempstarting_row = 22
                ending = 39
            }
            4 -> {
                day_name_view.setText(day_name_view.adapter.getItem(3).toString(), false)
                index = 0
                tempstarting_row = 42
                ending = 59
            }
            5 -> {
                day_name_view.setText(day_name_view.adapter.getItem(4).toString(), false)
                index = 0
                tempstarting_row = 62
                ending = 79
            }
            6 -> {
                day_name_view.setText(day_name_view.adapter.getItem(5).toString(), false)
//                day_name_view.text = "Friday"

                index = 0
                tempstarting_row = 82
                ending = 99
            }
            7 -> {
                day_name_view.setText(day_name_view.adapter.getItem(6).toString(), false)
                index = 0
                tempstarting_row = 2
                ending = 19
                totalclass_view.text = "Today Is OFF"
                listview.visibility = GONE

            }
            else -> {
                Toast.makeText(
                    requireContext(),
                    "There is problem Baby $day_name",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        val progressBar6 = view.findViewById<ProgressBar>(R.id.progressBar6)
        day_name_view.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                listview.visibility = View.VISIBLE
                progressBar6.visibility = View.VISIBLE
                val selecteditem = parent.getItemAtPosition(position)
                when (selecteditem) {

                    "Monday" -> {
                        giveclassesbro(2, 19)
                        progressBar6.visibility = View.GONE
                    }
                    "Tuesday" -> {
                        giveclassesbro(22, 39)
                        progressBar6.visibility = View.GONE
                    }
                    "Wednesday" -> {
                        giveclassesbro(42, 59)
                        progressBar6.visibility = View.GONE
                    }
                    "Thursday" -> {
                        giveclassesbro(62, 79)
                        progressBar6.visibility = View.GONE
                    }
                    "Friday" -> {
                        giveclassesbro(82, 99)
                        progressBar6.visibility = View.GONE
                    }
                    else -> {
                        totalclass_view.text = "Today Is OFF"
                        progressBar6.visibility = View.GONE
                        listview.visibility = View.GONE

                    }
                }

            }
        var starting_row = tempstarting_row
        var starting_cell = 2
        var temp_text: String
        var text = ""
        var searchText = "Double Click to Select Class"
        if (selectedclass != null) {
            searchText = selectedclass.toString()
        } else {
            if (loaddata() != "null") {
                searchText = loaddata().toString()
            }
        }
        textView.text = searchText
        val inputStream = FileInputStream(filePath)
        val xlWb = WorkbookFactory.create(inputStream)
        val xlWs = xlWb.getSheetAt(index)
        var class_detail = arrayOf<String?>()
        var Room_number = arrayOf<String?>()
        var Time_detail = arrayOf<String?>()
        class_details_array = ArrayList()
        while (starting_row != ending) {
            temp_text = xlWs.getRow(starting_row).getCell(starting_cell).toString()
            if (temp_text.contains(searchText.toString())) {
                val class_conf = temp_text.split("\n").toTypedArray()
                if (searchText == class_conf[0]) {
                    val time = xlWs.getRow(tempstarting_row - 1).getCell(starting_cell).toString()
                    val cl_no = xlWs.getRow(starting_row).getCell(1).toString()
                    Time_detail = append(Time_detail, time)
                    Room_number = append(Room_number, cl_no.toString())
                    text = temp_text
                    class_detail = append(class_detail, text)
                }
            }
            starting_row++
        }
        starting_cell = 3
        starting_row = tempstarting_row
        while (starting_row != ending) {
            temp_text = xlWs.getRow(starting_row).getCell(starting_cell).toString()
            if (temp_text.contains(searchText.toString())) {
                val class_conf = temp_text.split("\n").toTypedArray()
                if (searchText == class_conf[0]) {
                    val time = xlWs.getRow(tempstarting_row - 1).getCell(starting_cell).toString()
                    val cl_no = xlWs.getRow(starting_row).getCell(1).toString()
                    Time_detail = append(Time_detail, time)
                    Room_number = append(Room_number, cl_no.toString())
                    text = temp_text
                    class_detail = append(class_detail, text)
                }

            }
            starting_row++
        }
        starting_cell = 4
        starting_row = tempstarting_row
        while (starting_row != ending) {
            temp_text = xlWs.getRow(starting_row).getCell(starting_cell).toString()
            if (temp_text.contains(searchText.toString())) {
                val class_conf = temp_text.split("\n").toTypedArray()
                if (searchText == class_conf[0]) {
                    val time = xlWs.getRow(tempstarting_row - 1).getCell(starting_cell).toString()
                    val cl_no = xlWs.getRow(starting_row).getCell(1).toString()
                    Time_detail = append(Time_detail, time)
                    Room_number = append(Room_number, cl_no.toString())
                    text = temp_text
                    class_detail = append(class_detail, text)
                }

            }
            starting_row++
        }
        starting_cell = 5
        starting_row = tempstarting_row
        while (starting_row != ending) {
            temp_text = xlWs.getRow(starting_row).getCell(starting_cell).toString()
            if (temp_text.contains(searchText.toString())) {
                val class_conf = temp_text.split("\n").toTypedArray()
                if (searchText == class_conf[0]) {
                    val time = xlWs.getRow(tempstarting_row - 1).getCell(starting_cell).toString()
                    val cl_no = xlWs.getRow(starting_row).getCell(1).toString()
                    Time_detail = append(Time_detail, time)
                    Room_number = append(Room_number, cl_no.toString())
                    text = temp_text
                    class_detail = append(class_detail, text)
                }
            }
            starting_row++
        }
        starting_cell = 6
        starting_row = tempstarting_row
        while (starting_row != ending) {
            temp_text = xlWs.getRow(starting_row).getCell(starting_cell).toString()
            if (temp_text.contains(searchText.toString())) {
                val class_conf = temp_text.split("\n").toTypedArray()
                if (searchText == class_conf[0]) {
                    val time = xlWs.getRow(tempstarting_row - 1).getCell(starting_cell).toString()
                    val cl_no = xlWs.getRow(starting_row).getCell(1).toString()
                    Time_detail = append(Time_detail, time)
                    Room_number = append(Room_number, cl_no.toString())
                    text = temp_text
                    class_detail = append(class_detail, text)
                }
            }
            starting_row++

        }
        starting_cell = 7
        starting_row = tempstarting_row
        while (starting_row != ending) {
            temp_text = xlWs.getRow(starting_row).getCell(starting_cell).toString()
            if (temp_text.contains(searchText.toString())) {
                val class_conf = temp_text.split("\n").toTypedArray()
                if (searchText == class_conf[0]) {
                    val time = xlWs.getRow(tempstarting_row - 1).getCell(starting_cell).toString()
                    val cl_no = xlWs.getRow(starting_row).getCell(1).toString()
                    Time_detail = append(Time_detail, time)
                    Room_number = append(Room_number, cl_no.toString())
                    text = temp_text
                    class_detail = append(class_detail, text)
                }

            }
            starting_row++

        }
        starting_cell = 8
        starting_row = tempstarting_row
        while (starting_row != ending) {
            temp_text = xlWs.getRow(starting_row).getCell(starting_cell).toString()
            if (temp_text.contains(searchText.toString())) {
                val class_conf = temp_text.split("\n").toTypedArray()
                if (searchText == class_conf[0]) {
                    val time = xlWs.getRow(tempstarting_row - 1).getCell(starting_cell).toString()
                    val cl_no = xlWs.getRow(starting_row).getCell(1).toString()
                    Time_detail = append(Time_detail, time)
                    Room_number = append(Room_number, cl_no.toString())
                    text = temp_text
                    class_detail = append(class_detail, text)
                }
            }
            starting_row++

        }
        xlWb.close()
        for (i in class_detail.indices) {
            val class_d = class_details_data(
                Time_detail[i].toString(),
                Room_number[i].toString(),
                class_detail[i].toString()
            )
            class_details_array.add(class_d)
        }
        if (day_name > 1 && day_name < 7) {
            totalclass_view.text = class_detail.size.toString()
            listview.adapter = class_details_adapter(requireContext(), class_details_array)
        }
        savedata()

    }

    companion object {
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    fun <T> append(arr: Array<T>, element: T): Array<T?> {
        val array = arr.copyOf(arr.size + 1)
        array[arr.size] = element
        return array
    }

    fun savedata() {
        val classname_viwe = view?.findViewById<TextView>(R.id.class_name)
        val classname = classname_viwe?.text.toString()

        val sheredperfernence: SharedPreferences? =
            this.activity?.getSharedPreferences("sheredPrefs", Context.MODE_PRIVATE)
        val editor = sheredperfernence?.edit()
        editor?.apply {
            putString("class_name", classname)

        }?.apply()

    }

    fun giveclassesbro(tempstarting_row: Int, ending: Int) {
        val totalclass_view = view?.findViewById<TextView>(R.id.totalclass_view)
        val index = 0
        var starting_row = tempstarting_row
        var starting_cell = 2
        var temp_text: String
        var text = ""
        var searchText = "BSCS 6th (Self)"
        searchText = loaddata().toString()
        val inputStream = FileInputStream(filePath)
        val xlWb = WorkbookFactory.create(inputStream)
        val xlWs = xlWb.getSheetAt(index)
        var class_detail = arrayOf<String?>()
        var Room_number = arrayOf<String?>()
        var Time_detail = arrayOf<String?>()
        class_details_array = ArrayList()
        while (starting_row != ending) {
            temp_text = xlWs.getRow(starting_row).getCell(starting_cell).toString()
            if (temp_text.contains(searchText.toString())) {
                val class_conf = temp_text.split("\n").toTypedArray()
                if (searchText == class_conf[0]) {
                    val time = xlWs.getRow(tempstarting_row - 1).getCell(starting_cell).toString()
                    val cl_no = xlWs.getRow(starting_row).getCell(1).toString()
                    Time_detail = append(Time_detail, time)
                    Room_number = append(Room_number, cl_no.toString())
                    text = temp_text
                    class_detail = append(class_detail, text)
                }
            }
            starting_row++
        }
        starting_cell = 3
        starting_row = tempstarting_row
        while (starting_row != ending) {
            temp_text = xlWs.getRow(starting_row).getCell(starting_cell).toString()
            if (temp_text.contains(searchText.toString())) {
                val class_conf = temp_text.split("\n").toTypedArray()
                if (searchText == class_conf[0]) {
                    val time = xlWs.getRow(tempstarting_row - 1).getCell(starting_cell).toString()
                    val cl_no = xlWs.getRow(starting_row).getCell(1).toString()
                    Time_detail = append(Time_detail, time)
                    Room_number = append(Room_number, cl_no.toString())
                    text = temp_text
                    class_detail = append(class_detail, text)
                }

            }
            starting_row++
        }
        starting_cell = 4
        starting_row = tempstarting_row
        while (starting_row != ending) {
            temp_text = xlWs.getRow(starting_row).getCell(starting_cell).toString()
            if (temp_text.contains(searchText.toString())) {
                val class_conf = temp_text.split("\n").toTypedArray()
                if (searchText == class_conf[0]) {
                    val time = xlWs.getRow(tempstarting_row - 1).getCell(starting_cell).toString()
                    val cl_no = xlWs.getRow(starting_row).getCell(1).toString()
                    Time_detail = append(Time_detail, time)
                    Room_number = append(Room_number, cl_no.toString())
                    text = temp_text
                    class_detail = append(class_detail, text)
                }

            }
            starting_row++
        }
        starting_cell = 5
        starting_row = tempstarting_row
        while (starting_row != ending) {
            temp_text = xlWs.getRow(starting_row).getCell(starting_cell).toString()
            if (temp_text.contains(searchText.toString())) {
                val class_conf = temp_text.split("\n").toTypedArray()
                if (searchText == class_conf[0]) {
                    val time = xlWs.getRow(tempstarting_row - 1).getCell(starting_cell).toString()
                    val cl_no = xlWs.getRow(starting_row).getCell(1).toString()
                    Time_detail = append(Time_detail, time)
                    Room_number = append(Room_number, cl_no.toString())
                    text = temp_text
                    class_detail = append(class_detail, text)
                }
            }
            starting_row++
        }
        starting_cell = 6
        starting_row = tempstarting_row
        while (starting_row != ending) {
            temp_text = xlWs.getRow(starting_row).getCell(starting_cell).toString()
            if (temp_text.contains(searchText.toString())) {
                val class_conf = temp_text.split("\n").toTypedArray()
                if (searchText == class_conf[0]) {
                    val time = xlWs.getRow(tempstarting_row - 1).getCell(starting_cell).toString()
                    val cl_no = xlWs.getRow(starting_row).getCell(1).toString()
                    Time_detail = append(Time_detail, time)
                    Room_number = append(Room_number, cl_no.toString())
                    text = temp_text
                    class_detail = append(class_detail, text)
                }
            }
            starting_row++

        }
        starting_cell = 7
        starting_row = tempstarting_row
        while (starting_row != ending) {
            temp_text = xlWs.getRow(starting_row).getCell(starting_cell).toString()
            if (temp_text.contains(searchText.toString())) {
                val class_conf = temp_text.split("\n").toTypedArray()
                if (searchText == class_conf[0]) {
                    val time = xlWs.getRow(tempstarting_row - 1).getCell(starting_cell).toString()
                    val cl_no = xlWs.getRow(starting_row).getCell(1).toString()
                    Time_detail = append(Time_detail, time)
                    Room_number = append(Room_number, cl_no.toString())
                    text = temp_text
                    class_detail = append(class_detail, text)
                }

            }
            starting_row++

        }
        starting_cell = 8
        starting_row = tempstarting_row
        while (starting_row != ending) {
            temp_text = xlWs.getRow(starting_row).getCell(starting_cell).toString()
            if (temp_text.contains(searchText.toString())) {
                val class_conf = temp_text.split("\n").toTypedArray()
                if (searchText == class_conf[0]) {
                    val time = xlWs.getRow(tempstarting_row - 1).getCell(starting_cell).toString()
                    val cl_no = xlWs.getRow(starting_row).getCell(1).toString()
                    Time_detail = append(Time_detail, time)
                    Room_number = append(Room_number, cl_no.toString())
                    text = temp_text
                    class_detail = append(class_detail, text)
                }
            }
            starting_row++

        }
        xlWb.close()
        for (i in class_detail.indices) {
            val class_d = class_details_data(
                Time_detail[i].toString(),
                Room_number[i].toString(),
                class_detail[i].toString()
            )
            class_details_array.add(class_d)
        }

        totalclass_view?.text = class_detail.size.toString()
        listview.adapter = class_details_adapter(requireContext(), class_details_array)

    }

    fun loaddata(): String? {
        val sheredperfernence: SharedPreferences? =
            this.activity?.getSharedPreferences("sheredPrefs", Context.MODE_PRIVATE)
        val savestring = sheredperfernence?.getString("class_name", null)
        return savestring.toString()
    }


}