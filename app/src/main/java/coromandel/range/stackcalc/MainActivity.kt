package coromandel.range.stackcalc

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import android.view.View.OnTouchListener
import android.view.View.generateViewId
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.preference.PreferenceManager
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {

    lateinit var stack: Stack
    lateinit var inputBuffer: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
            WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
        );

        // lookup theme settings
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val themeMode = sharedPreferences.getString("theme", "Follow System")
        if (themeMode.equals("Light")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else if (themeMode.equals("Dark")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }

        stack = Stack(this, findViewById(R.id.stack_items))

        val deleteButton = findViewById<ImageButton>(R.id.delete_button)
        deleteButton.setOnClickListener() {
            stack.deleteFirstItem()
        }
        enableHaptics(deleteButton)

        val cloneButton = findViewById<ImageButton>(R.id.clone_button)
        cloneButton.setOnClickListener() {
            stack.cloneFirstItem()
        }
        enableHaptics(cloneButton)

        val settingsButton = findViewById<ImageButton>(R.id.settings_button)
        settingsButton.setOnClickListener() {
            val i = Intent(this@MainActivity, SettingsActivity::class.java)
            startActivity(i)
        }
        enableHaptics(settingsButton)

        setupNumpad()
        setupFunctionGroups()
        setupOperations()
    }

    fun setupNumpad() {
        inputBuffer = findViewById<EditText>(R.id.input_text)
        inputBuffer.requestFocus()

        val numpadButtons = listOf(
            findViewById<Button>(R.id.number_0_button),
            findViewById<Button>(R.id.number_1_button),
            findViewById<Button>(R.id.number_2_button),
            findViewById<Button>(R.id.number_3_button),
            findViewById<Button>(R.id.number_4_button),
            findViewById<Button>(R.id.number_5_button),
            findViewById<Button>(R.id.number_6_button),
            findViewById<Button>(R.id.number_7_button),
            findViewById<Button>(R.id.number_8_button),
            findViewById<Button>(R.id.number_9_button),
        )

        var acceptButton = findViewById<ImageButton>(R.id.accept_button)
        acceptButton.setOnClickListener() {
            insertFromBuffer()
        }
        enableHaptics(acceptButton)
        inputBuffer.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                insertFromBuffer()
                return@OnKeyListener true
            }
            false
        })

        var clearButton = findViewById<ImageButton>(R.id.clear_button)
        clearButton.setOnClickListener() {
            inputBuffer.setText("")
            stack.clearStack()
        }
        enableHaptics(clearButton)

        var backspaceButton = findViewById<ImageButton>(R.id.backspace_button)
        backspaceButton.setOnClickListener() {
            inputBuffer.dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
        }
        backspaceButton.setOnLongClickListener() {
            inputBuffer.setText("")
            return@setOnLongClickListener true
        }
        enableHaptics(backspaceButton)

        var numpadSignButton = findViewById<Button>(R.id.sign_button)
        numpadSignButton.setOnClickListener() {
            if (inputBuffer.text.startsWith('-')) {
                inputBuffer.getText().delete(0, 1)
            } else {
                inputBuffer.getText().insert(0, "-")
            }
        }
        enableHaptics(numpadSignButton)

        var numpadPointButton = findViewById<Button>(R.id.decimal_button)
        numpadPointButton.setOnClickListener() {
            val start = Math.max(inputBuffer.getSelectionStart(), 0)
            if (start == 0 || (start == 1 && inputBuffer.text.startsWith('-'))) {
                inputBuffer.dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_0))
            }
            inputBuffer.dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_PERIOD))
        }
        enableHaptics(numpadPointButton)

        for (button in numpadButtons) {
            button.setOnClickListener() {
                val start = Math.max(inputBuffer.getSelectionStart(), 0)
                val end = Math.max(inputBuffer.getSelectionEnd(), 0)
                inputBuffer.getText()
                    .replace(Math.min(start, end), Math.max(start, end), button.text, 0, 1)
            }
            enableHaptics(button)
        }
    }

    fun insertFromBuffer() {
        if (inputBuffer.text.isNotBlank()) {
            stack.push(inputBuffer.text.toString().toDouble())
            inputBuffer.setText("")
        }
    }

    fun setupOperations() {
        var addButton = findViewById<Button>(R.id.add_button)
        var subtractButton = findViewById<Button>(R.id.subtract_button)
        var multiplyButton = findViewById<Button>(R.id.multiply_button)
        var divideButton = findViewById<Button>(R.id.divide_button)
        var flipButton = findViewById<Button>(R.id.flip_button)
        var clearUnitButton = findViewById<Button>(R.id.clear_unit_button)

        addButton.setOnClickListener() {
            insertFromBuffer()
            stack.push("ADD")
        }
        enableHaptics(addButton)

        subtractButton.setOnClickListener() {
            insertFromBuffer()
            stack.push("SUBTRACT")
        }
        enableHaptics(subtractButton)

        multiplyButton.setOnClickListener() {
            insertFromBuffer()
            stack.push("MULTIPLY")
        }
        enableHaptics(multiplyButton)

        divideButton.setOnClickListener() {
            insertFromBuffer()
            stack.push("DIVIDE")
        }
        enableHaptics(divideButton)

        flipButton.setOnClickListener() {
            insertFromBuffer()
            stack.push("FLIP")
        }
        enableHaptics(flipButton)

        clearUnitButton.setOnClickListener() {
            insertFromBuffer()
            stack.push("CLEAR_UNIT")
        }
        enableHaptics(clearUnitButton)
    }

    fun setupFunctionGroups() {
        var selectedGroup: String = ""

        var groupButtons = hashMapOf<String, ImageButton>()
        groupButtons["time"] = findViewById<ImageButton>(R.id.time_unit_button)
        groupButtons["distance"] =  findViewById<ImageButton>(R.id.distance_unit_button)
        groupButtons["weight"] =  findViewById<ImageButton>(R.id.weight_unit_button)
        groupButtons["volume"] =  findViewById<ImageButton>(R.id.volume_unit_button)
        groupButtons["rate"] =  findViewById<ImageButton>(R.id.rate_unit_button)
        groupButtons["more"] =  findViewById<ImageButton>(R.id.more_opps_button)

        var groupFunctions = hashMapOf<String, List<String>>()
        groupFunctions["time"] = listOf("hr", "min", "sec", "SPLIT")
        groupFunctions["distance"] = listOf("Km", "m", "Mi", "NMi", "ft", "in")
        groupFunctions["weight"] = listOf("Kg", "g", "Lbs")
        groupFunctions["volume"] = listOf("L", "GAL", "Qrt")
        groupFunctions["rate"] = listOf("Km/hr", "m/sec", "NMi/hr", "Mi/hr", "L/hr", "GAL/hr")
        groupFunctions["more"] = listOf("SIN", "ASIN", "COS", "ACOS", "TAN", "ATAN", "POWER", "LOG", "ABS", "SQRT", "PI")

        var groupsLayout = findViewById<ConstraintLayout>(R.id.groups)
        var groupsFlow = findViewById<androidx.constraintlayout.helper.widget.Flow>(R.id.group_flow)

        var groupFunctionButtons = hashMapOf<String, List<MaterialButton>>()
        val referenceIds = mutableListOf<Int>()

        // lookup button colour for the current theme
        var btnColourValue = TypedValue()
        getTheme().resolveAttribute(R.attr.secondary_operator_button, btnColourValue, true)

        for ((group, functions) in groupFunctions) {
            var buttons = mutableListOf<MaterialButton>()
            for (function in functions) {
                var button = MaterialButton(this)
                button.id = generateViewId()
                button.setText(function)
                button.textSize = 10F
                button.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 120)
                button.minimumWidth = 0
                button.minWidth = 150
                button.insetTop = 0
                button.insetBottom = 0
                button.setBackgroundTintList(AppCompatResources.getColorStateList(this, btnColourValue.resourceId));
                groupsLayout.addView(button)
                button.visibility = View.GONE
                enableHaptics(button)
                buttons.add(button)
                referenceIds.add(button.id)
                button.setOnClickListener() {
                    insertFromBuffer()
                    stack.push(function)
                }
            }
            groupFunctionButtons[group] = buttons.toList()
        }
        groupsFlow.referencedIds = referenceIds.toIntArray()

        for ((group, button) in groupButtons) {
            button.setOnClickListener() {
                if (selectedGroup.isNotBlank()) {
                    getTheme().resolveAttribute(R.attr.group_unselected, btnColourValue, true)
                    groupButtons[selectedGroup]?.setBackgroundTintList(AppCompatResources.getColorStateList(this, btnColourValue.resourceId));
                    for (button in groupFunctionButtons[selectedGroup]!!) {
                        button.visibility = View.GONE
                    }
                }

                if (selectedGroup.equals(group)) {
                    for (button in groupFunctionButtons[selectedGroup]!!) {
                        button.visibility = View.GONE
                    }
                    selectedGroup = ""
                    getTheme().resolveAttribute(R.attr.group_unselected, btnColourValue, true)
                    button.setBackgroundTintList(AppCompatResources.getColorStateList(this, btnColourValue.resourceId));
                } else {
                    selectedGroup = group
                    getTheme().resolveAttribute(R.attr.group_selected, btnColourValue, true)
                    button.setBackgroundTintList(AppCompatResources.getColorStateList(this, btnColourValue.resourceId));
                    for (button in groupFunctionButtons[selectedGroup]!!) {
                        button.visibility = View.VISIBLE
                    }
                }
            }
            enableHaptics(button)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun enableHaptics(view: View) {
        view.setOnTouchListener(OnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                return@OnTouchListener false
            }
            false
        })
    }
}