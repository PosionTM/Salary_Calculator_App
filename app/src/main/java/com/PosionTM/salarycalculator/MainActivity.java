package com.PosionTM.salarycalculator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;

import android.widget.TextView;

import java.text.DecimalFormat;

// Created by Tommy M, 2022
public class MainActivity extends AppCompatActivity {

    public static double taxed_salary;
    public static double entered_salary;
    Button calculate_button;
    Button Breakdown_button;
    SwitchCompat dark_switch;
    EditText user_input;
    String conversion_var;
    String fluff_removed;
    SharedPreferences sharedPreferences = null;
    int counter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.PosionTM.salarycalculator.R.layout.activity_main);



        // Allows "Calculate" button to run calculations
        calculate_button = findViewById(R.id.Calculate_button);
        calculate_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    user_input = findViewById(R.id.entered_salary);
                    conversion_var = user_input.getText().toString();
                    fluff_removed = conversion_var.replaceAll("[,]", "");
                    entered_salary = Double.parseDouble(fluff_removed);
                    if (entered_salary < 1000) {
                        entered_salary = Convert_hourly_wage(entered_salary);
                    }
                    taxed_salary = Calculate_taxed_salary(entered_salary);
                    Display_salaries(taxed_salary);

                    // Puts keyboard away upon clicking Calculate button
                    user_input.onEditorAction(EditorInfo.IME_ACTION_DONE);

                    findViewById(R.id.error_message).setVisibility(View.INVISIBLE);

                } catch (Exception e) {
                    System.out.println("Error running calculations with inputted values");
                    findViewById(R.id.error_message).setVisibility(View.VISIBLE);

                }





            }
        });

        // Allows keyboard to be hidden upon clicking the outside of input field
        user_input = findViewById(R.id.entered_salary);
        user_input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        // Opens Breakdown Activity upon clicking breakdown button
        Breakdown_button = findViewById(R.id.Breakdown_button);
        Breakdown_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBreakdownAcivity();
            }
        });






        // Saves night present if app is closed with dark mode on
        dark_switch = findViewById(R.id.dark_switch);
        ConstraintLayout main_view = findViewById(R.id.parent_layout);

        sharedPreferences = getSharedPreferences("night", 0);
        Boolean booleanValue = sharedPreferences.getBoolean("night_mode", true);
        if (booleanValue) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            dark_switch.setChecked(true);
            main_view.setBackgroundColor(Color.rgb(20, 20, 20));

        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            dark_switch.setChecked(false);
            main_view.setBackgroundColor(Color.rgb(240, 239, 232));
        }




        // Night mode switch functionality
        dark_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    dark_switch.setChecked(true);
                    main_view.setBackgroundColor(Color.rgb(20, 20, 20));
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("night_mode", true);
                    editor.apply();
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    dark_switch.setChecked(false);
                    main_view.setBackgroundColor(Color.rgb(240, 239, 232));
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("night_mode", false);
                    editor.apply();
                }
            }
        });



        }

    // Converts hourly wage to annual salary
    public static double Convert_hourly_wage(double wage) {
        return wage * 2080;
    }

    // Calculates GA State tax
    // GA_Tax is the 5.75% flat tax over 7k
    public static double GA_State_Tax(double gross_salary)  {
        return  (gross_salary - 7000) * 0.0575 + 230;
    }


    // Calculates Medicare and Social Security taxes
    public static double Caclulate_FICA_Tax(double gross_salary) {
        double taxes;

        // SSM is social security and medicare flat rate of 7.65%
        if (gross_salary < 142800) {
            taxes = (gross_salary * 0.0765) + (gross_salary * 0.0145);
        }
        // Social Security can only tax up to 142.8k, thus maximum is 8853.60
        else if (gross_salary > 142800 && gross_salary < 200000){
            taxes = 8853.60 + (gross_salary * 0.0145);
        }
        // Medicare tax increases to 2.35% after 200k, max tax at 1.45% is 2900
        else {
            taxes = 8853.60 + 2900 + ((gross_salary - 200000) * 0.0235);
        }
        return taxes;
    }


    // Calculates taxes from inputted salary
    public static double Calculate_taxed_salary(double gross_salary) {

        // if user inputted number less than 1, returns 0 as calculations
        if (gross_salary <= 0) {
            return 0;
        }

        // Convert hourly wage to annual
        if (gross_salary > 0 && gross_salary < 999) {
            gross_salary *= 2080;
        }

        double FICA_Taxes = Caclulate_FICA_Tax(gross_salary);

        // Standard deduction is 12550 added in the final salary value
        double Standard_Deduction = 12550;

        double taxable_salary;
        double tax_bracket_salary;
        double final_salary = 0;
        double GA_Tax;


        // standard deduction from gross salary is taxable salary
        taxable_salary = gross_salary - Standard_Deduction;

        // Georgia state tax calculation
        GA_Tax = GA_State_Tax(gross_salary);


        // Conditionals for each tax bracket
        /*
        Math:
            1. Calculates federal tax based on tax bracket system from 2021.
            2. Social Security and Medicare have a flat rate of 7.65% for things below 200k.
            3. Uses Georgia State tax which is 5.75% for anyone making above 7k+.
            4. Atlanta does NOT have any additional income tax and thus doesn't change anything.
            5. Assumes you work 40 hours per week and work 52 weeks per year,
               totaling 2080 hours per year.
            6. Uses the standard deduction of $12,550. (Alternative is itemized deductions).
            7. Tax brackets are based on single filing, NOT married. (For now)
            8. Social Security only applies up to 142.8k.
            9. The GA tax rate needlessly convoluted and minor, as it has
            numerous tax brackets for anything below 7k so we will
               add a 3.5% GA flat rate to any salaries below 10k
            10. During tax bracket calculations, the previous tax brackets maximum taxable value
            is subtracted and the remainder is taxed as the current tax bracket rate. Then, it
            is added back during the final salary calculation. This ensures you don't get
            the entire salary taxed at a flat rate for being in a higher bracket.
         */

        // Note: Taxable salary is only negative when salary is lower than standard deduct
        if (taxable_salary <= 0) {
            final_salary = gross_salary * 0.965 - FICA_Taxes;
        }
        // 1st tax bracket, Federal tax of 10% + add a flat 3.5% for GA tax = 0.865
        else if (taxable_salary > 0 && taxable_salary <= 9950) {
            final_salary = taxable_salary * 0.865 - FICA_Taxes + Standard_Deduction;
        }
        // 2nd tax bracket
        else if (taxable_salary > 9950 && taxable_salary <= 40525) {
            tax_bracket_salary = ((taxable_salary - 9950) * 0.88);
            final_salary = tax_bracket_salary + 8955 - FICA_Taxes - GA_Tax + Standard_Deduction;
        }
        // 3rd tax bracket
        else if (taxable_salary > 40525 && taxable_salary <= 86375) {
            tax_bracket_salary = ((taxable_salary - 40525) * 0.78);
            final_salary = tax_bracket_salary + 35860 - FICA_Taxes - GA_Tax + Standard_Deduction;
        }
        // 4th tax bracket
        else if (taxable_salary > 86375 && taxable_salary <= 164925) {
            tax_bracket_salary = ((taxable_salary - 86375) * 0.76);
            final_salary = (tax_bracket_salary + 71833 - FICA_Taxes - GA_Tax + Standard_Deduction);

        }
        // 5th tax bracket
        else if (taxable_salary > 164925 && taxable_salary <= 209425) {
            tax_bracket_salary = ((taxable_salary - 164925) * 0.68);
            final_salary = (tax_bracket_salary + 131530 - FICA_Taxes
                    - GA_Tax + Standard_Deduction);
        }
        // 6th tax bracket
        else if (taxable_salary > 209425 && taxable_salary <= 523600) {
            tax_bracket_salary = ((taxable_salary - 209425) * 0.65);
            final_salary = (tax_bracket_salary + 161789 - FICA_Taxes - GA_Tax + Standard_Deduction);
        }
        // 7th & final tax bracket
        else if (taxable_salary > 523600) {
            tax_bracket_salary = ((taxable_salary - 523600) * 0.63);
            final_salary = (tax_bracket_salary + 366002 - FICA_Taxes - GA_Tax + Standard_Deduction);
        }

        return final_salary;



    }

    // Performs simple calculations from taxed salary and displays to user
    public void Display_salaries(double taxed_salary) {
        double hourly_income = entered_salary / 2080;
        double monthly_income = taxed_salary / 12;
        double biweekly_income = monthly_income / 2;

        DecimalFormat Format_num = new DecimalFormat("#,###");
        DecimalFormat Format_wage = new DecimalFormat("#,###.00");

        String Annual_display_value = Format_num.format(entered_salary);
        String monthly_display_value = Format_num.format(monthly_income);
        String biweekly_display_value = Format_num.format(biweekly_income);
        String hourly_display_value = Format_wage.format(hourly_income);


        ((TextView)findViewById(R.id.Annual_value)).setText(Annual_display_value);
        ((TextView)findViewById(R.id.Hourly_value)).setText(hourly_display_value);
        ((TextView)findViewById(R.id.Biweekly_value)).setText(biweekly_display_value);
        ((TextView)findViewById(R.id.Monthly_value)).setText(monthly_display_value);

    }

    // Opens breakdown screen when called (upon breakdown button click)
    public void openBreakdownAcivity() {
        Intent intent = new Intent(this, BreakdownActivity.class);
        startActivity(intent);
    }


    // Counts each time this instance is called
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        System.out.println("Processing: Saving instance");
        outState.putInt("Counter", counter);
    }


    // If this instance is called too many times, it restarts app
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        counter = savedInstanceState.getInt("Counter");
        counter++;
        if (counter >= 3) {
            Intent i = getBaseContext().getPackageManager()
                    .getLaunchIntentForPackage( getBaseContext().getPackageName() );
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }
        System.out.println("Processing: Restore instance, Counter: " + counter);

    }


    // Allows values to persist after opening breakdown activity
    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("Processing: OnResume");
        taxed_salary = Calculate_taxed_salary(entered_salary);
        Display_salaries(taxed_salary);
    }


    // Hides keyboard
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}