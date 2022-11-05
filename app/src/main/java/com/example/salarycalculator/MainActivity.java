package com.example.salarycalculator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;


import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;

import android.widget.Switch;
import android.widget.TextView;

import java.text.DecimalFormat;


public class MainActivity extends AppCompatActivity {

    private Button calculate_button;
    private Switch dark_switch;
    double taxed_salary;
    double entered_salary;
    EditText user_input;
    String conversion_var;
    String fluff_removed;
    // for future implementation of a try catch
//    TextView error_text = findViewById(R.id.error_message);



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Allows "Calculate" button to run calculations
        calculate_button = (Button) findViewById(R.id.Calculate_button);
        calculate_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user_input = (EditText)findViewById(R.id.entered_salary);
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




            }
        });

        dark_switch = (Switch) findViewById(R.id.dark_switch);
        ConstraintLayout main_view = findViewById(R.id.parent_layout);

        dark_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled

                    main_view.setBackgroundColor(Color.rgb(20, 20, 20));

                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);



                } else {
                    // The toggle is disabled
                    main_view.setBackgroundColor(Color.rgb(240, 239, 232));
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
            }
        });

        }

    // Converts hourly wage to annual salary
    public static double Convert_hourly_wage(double wage) {
        double salary = wage * 2080;
        return salary;
    }

    // Calculates GA State tax
    // GA_Tax is the 5.75% flat tax over 7k
    public static double GA_State_Tax(double taxable_salary)  {
        double GA_Tax = (taxable_salary - 7000) * 0.0575 + 230;
        return  GA_Tax;
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

        double taxable_salary;



        // SSM is social security and medicare flat rate of 7.65%
        double SSM_Tax = gross_salary * 0.0765;

        // Medicare only for above 142.8k
        double Medicare_Tax = gross_salary * 0.0145;

        // Medicare tax increases to 2.35% after 200k
        double Medicare_Tax_200k = gross_salary * 0.0235;

        // Social Security can only tax up to 142.8k
        double Max_SocialSecurity_Tax = 8853.60;

        // Standard deduction is 12550 added in the final salary value
        double Standard_Deduction = 12550;

        double tax_bracket_salary;
        double final_salary = 0;
        double GA_Tax;


        // standard deduction from gross salary is taxable salary
        taxable_salary = gross_salary - Standard_Deduction;

        // This var is used in most of the conditionals below to
        GA_Tax = GA_State_Tax(gross_salary);


        // Conditionals for each tax bracket
        /*
        Math:
            1. Calculates federal tax based on tax bracket system from 2021
            2. Social Security and Medicare have a flat rate of 7.65%
            3. Uses Georgia State tax which is 5.75% for anyone making 7k+
            4. Atlanta does NOT have a local income tax and thus doesn't change anything
            5. Assumes you work 40 hours per week and work 52 weeks per year,
               totaling 2080 hours per year.
            6. Uses the standard deduction of $12,550.
            7. Tax brackets are based on single filing, NOT married.
            8. Social Security only applies up to 142.8k
            9. The GA tax rate needlessly convoluted and minor, as it has
            numerous tax brackets for anything below 7k so we will
               add a 3.5% GA flat rate to any salaries below 10k
            10. During tax bracket calculations, the previous tax brackets maximum taxable value
            is subtracted and the remainder is taxed as the current tax bracket rate. Then, it
            is added back during the final salary calculation. This ensures you don't get
            the entire salary taxed at a flat rate.
         */

        // Note: Taxable salary is only negative when salary is lower than standard deduct
        if (taxable_salary < 0) {
            final_salary = gross_salary * 0.965 - SSM_Tax;
            return final_salary;
        }
        // 1st tax bracket, Federal tax of 10% + add a flat 3.5% for GA tax = 0.865
        else if (taxable_salary > 0 && taxable_salary <= 9950) {
            final_salary = taxable_salary * 0.865 - SSM_Tax + Standard_Deduction;
            return final_salary;
        }
        // 2nd tax bracket
        else if (taxable_salary > 9950 && taxable_salary <= 40525) {
            tax_bracket_salary = ((taxable_salary - 9950) * 0.88);
            final_salary = tax_bracket_salary + 8955 - SSM_Tax - GA_Tax + Standard_Deduction;
            return final_salary;
        }
        // 3rd tax bracket
        else if (taxable_salary > 40525 && taxable_salary <= 86375) {
            tax_bracket_salary = ((taxable_salary - 40525) * 0.78);
            final_salary = tax_bracket_salary + 35860 - SSM_Tax - GA_Tax + Standard_Deduction;
            return final_salary;
        }
        // 4th tax bracket, nested if statement because of max social security tax at 142.8k
        else if (taxable_salary > 86375 && taxable_salary <= 164925) {
            tax_bracket_salary = ((taxable_salary - 86375) * 0.76);
            if (taxable_salary <= 142800) {
                final_salary = tax_bracket_salary + 71833 - SSM_Tax - GA_Tax + Standard_Deduction;
                return final_salary;
            } else {
                final_salary = (tax_bracket_salary + 71833 - Medicare_Tax - Max_SocialSecurity_Tax
                        - GA_Tax + Standard_Deduction);
                return final_salary;
            }
        }
        // 5th tax bracket
        else if (taxable_salary > 164925 && taxable_salary <= 209425) {
            tax_bracket_salary = ((taxable_salary - 164925) * 0.68);
            final_salary = (tax_bracket_salary + 131530 - Medicare_Tax - Max_SocialSecurity_Tax
                    - GA_Tax + Standard_Deduction);
            return final_salary;
        }
        // 6th tax bracket
        else if (taxable_salary > 209425 && taxable_salary <= 523600) {
            tax_bracket_salary = ((taxable_salary - 209425) * 0.65);
            final_salary = (tax_bracket_salary + 161789 - Medicare_Tax_200k -
                    Max_SocialSecurity_Tax - GA_Tax + Standard_Deduction);
            return final_salary;
        }
        // 7th & final tax bracket
        else if (taxable_salary > 523600) {
            tax_bracket_salary = ((taxable_salary - 523600) * 0.63);
            final_salary = (tax_bracket_salary + 366002 - Medicare_Tax_200k -
                    Max_SocialSecurity_Tax - GA_Tax + Standard_Deduction);
            return final_salary;
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

        // TESTING ANNUAL VALUE
//        ((TextView)findViewById(R.id.Annual_value)).setText(Double.toString(taxed_salary));
        ((TextView)findViewById(R.id.Annual_value)).setText(Annual_display_value);
        ((TextView)findViewById(R.id.Hourly_value)).setText(hourly_display_value);
        ((TextView)findViewById(R.id.Biweekly_value)).setText(biweekly_display_value);
        ((TextView)findViewById(R.id.Monthly_value)).setText(monthly_display_value);

    }


}