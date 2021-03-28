package com.ids789.stackcalc;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Stack stack;

    TextView stackOutput;
    EditText textInput;
    Button btnMultiply;
    Button btnDivide;
    Button btnAdd;
    Button btnSubtract;
    Button btnClear;
    Button btnPop;
    Button btnSwap;
    Button btnFlip;

    Button btnSec;
    Button btnMin;
    Button btnHr;
    Button btnKm;
    Button btnM;
    Button btnNm;
    Button btnFt;
    Button btnKg;
    Button btnLb;
    Button btnL;
    Button btnGAL;
    Button btnNoUnit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stack = new Stack(this);

        stackOutput = (TextView)findViewById(R.id.stackOutput);
        textInput = (EditText)findViewById(R.id.textInput);

        btnMultiply = (Button)findViewById(R.id.btnMultiply);
        btnDivide = (Button)findViewById(R.id.btnDivide);
        btnAdd = (Button)findViewById(R.id.btnAdd);
        btnSubtract = (Button)findViewById(R.id.btnSubtract);
        btnClear = (Button)findViewById(R.id.btnClear);
        btnPop = (Button)findViewById(R.id.btnPop);
        btnSwap = (Button)findViewById(R.id.btnSwap);
        btnFlip = (Button)findViewById(R.id.btnFlip);

        btnSec = (Button)findViewById(R.id.btnSec);
        btnMin = (Button)findViewById(R.id.btnMin);
        btnHr = (Button)findViewById(R.id.btnHr);
        btnKm = (Button)findViewById(R.id.btnKm);
        btnM = (Button)findViewById(R.id.btnM);
        btnNm = (Button)findViewById(R.id.btnNM);
        btnFt = (Button)findViewById(R.id.btnFt);
        btnL = (Button)findViewById(R.id.btnL);
        btnGAL = (Button)findViewById(R.id.btnGAL);
        btnKg = (Button)findViewById(R.id.btnKg);
        btnLb = (Button)findViewById(R.id.btnLb);
        btnNoUnit = (Button)findViewById(R.id.btnNoUnit);

        btnMultiply.setOnClickListener(this);
        btnDivide.setOnClickListener(this);
        btnAdd.setOnClickListener(this);
        btnSubtract.setOnClickListener(this);

        btnSec.setOnClickListener(this);
        btnMin.setOnClickListener(this);
        btnHr.setOnClickListener(this);
        btnKm.setOnClickListener(this);
        btnM.setOnClickListener(this);
        btnNm.setOnClickListener(this);
        btnFt.setOnClickListener(this);
        btnL.setOnClickListener(this);
        btnGAL.setOnClickListener(this);
        btnKg.setOnClickListener(this);
        btnLb.setOnClickListener(this);
        btnNoUnit.setOnClickListener(this);

        btnClear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                stack.clear();
                textInput.setText("");
                displayStack();
            }
        });

        btnPop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Double value = stack.pop();
                if (value != null) {
                    textInput.setText(String.format("%g", value).replaceAll("\\.0*$", ""));
                    textInput.setSelection(textInput.getText().length());
                    displayStack();
                }
            }
        });

        btnFlip.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                stack.flip();
                displayStack();
            }
        });

        btnSwap.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                stack.swap();
                displayStack();
            }
        });

        stackOutput.setMovementMethod(new ScrollingMovementMethod());

        textInput.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if (actionId == EditorInfo.IME_ACTION_DONE)
                {
                    addNumToStack();
                    displayStack();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View view)
    {
        if (textInput.getText().length() > 0) {
            addNumToStack();
        }

        switch (view.getId()) {
            case R.id.btnMultiply:
                stack.push("MULTIPLY");
                break;
            case R.id.btnDivide:
                stack.push("DIVIDE");
                break;
            case R.id.btnAdd:
                stack.push("ADD");
                break;
            case R.id.btnSubtract:
                stack.push("SUBTRACT");
                break;
            case R.id.btnSec:
                stack.push(Unit.UnitType.SECOND);
                break;
            case R.id.btnMin:
                stack.push(Unit.UnitType.MINUTE);
                break;
            case R.id.btnHr:
                stack.push(Unit.UnitType.HOUR);
                break;
            case R.id.btnKm:
                stack.push(Unit.UnitType.KM);
                break;
            case R.id.btnM:
                stack.push(Unit.UnitType.M);
                break;
            case R.id.btnNM:
                stack.push(Unit.UnitType.NM);
                break;
            case R.id.btnFt:
                stack.push(Unit.UnitType.FT);
                break;
            case R.id.btnL:
                stack.push(Unit.UnitType.L);
                break;
            case R.id.btnGAL:
                stack.push(Unit.UnitType.GAL);
                break;
            case R.id.btnKg:
                stack.push(Unit.UnitType.KG);
                break;
            case R.id.btnLb:
                stack.push(Unit.UnitType.LB);
                break;
            case R.id.btnNoUnit:
                stack.push("UNIT_NONE");
                break;
        }
        displayStack();
    }

    void addNumToStack() {
        if (!textInput.getText().equals("")) {
            try {
                Double value = Double.parseDouble(textInput.getText().toString());
                stack.push(value);
                textInput.setText("");
            } catch (Exception e) {

            }
        }
    }

    void displayStack() {
        String output = "";
        String[] items = stack.get();
        for (int i = items.length-1; i > 0; i--) {
            output += "<small><small><small>";
            output += items[i];
            output += "</small></small></small><br>";
        }
        if (items.length > 0) {
            output += "<small><small><br></small></small>" + items[0];
        }
        stackOutput.setText(Html.fromHtml(output));
    }
}