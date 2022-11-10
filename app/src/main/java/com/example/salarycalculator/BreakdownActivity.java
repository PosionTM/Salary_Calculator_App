package com.example.salarycalculator;

import static com.example.salarycalculator.MainActivity.Caclulate_FICA_Tax;
import static com.example.salarycalculator.MainActivity.GA_State_Tax;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.DecimalFormat;



public class BreakdownActivity extends AppCompatActivity {

    private ImageButton Back_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_breakdown);

        Back_button = findViewById(R.id.Back_button);
        Back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMainAcivity();
                finish();

            }
        });


        Display_breakdown(MainActivity.entered_salary, MainActivity.taxed_salary);
    }


    // Presents all values to user in breakdown page
    public void Display_breakdown(double gross_salary, double taxed_salary) {


        double BD_Federal_tax = Calculate_IncomeTax(gross_salary);
        double BD_FICA_tax = Caclulate_FICA_Tax(gross_salary);
        double BD_State_tax = GA_State_Tax(gross_salary);
        double BD_Total_tax = BD_Federal_tax + BD_FICA_tax + BD_State_tax;
        double BD_Annual_salary = gross_salary;
        double BD_Taxed_annually = taxed_salary;
        double BD_Monthly = taxed_salary / 12;
        double BD_Biweekly = BD_Monthly / 2;
        double BD_Weekly = BD_Monthly / 4;
        double BD_Hourly = gross_salary / 2080;


        DecimalFormat Format_num = new DecimalFormat("#,###");
        DecimalFormat Format_wage = new DecimalFormat("#,###.00");

        String BD_Federal_tax_value = Format_num.format(BD_Federal_tax);
        String BD_FICA_tax_value = Format_num.format(BD_FICA_tax);
        String BD_State_tax_value = Format_num.format(BD_State_tax);
        String BD_Total_tax_value = Format_num.format(BD_Total_tax);
        String BD_Annual_salary_value = Format_num.format(BD_Annual_salary);
        String BD_Taxed_annually_value = Format_num.format(BD_Taxed_annually);
        String BD_Monthly_value = Format_num.format(BD_Monthly);
        String BD_Biweekly_value = Format_num.format(BD_Biweekly);
        String BD_Weekly_value = Format_num.format(BD_Weekly);
        String BD_Hourly_value = Format_wage.format(BD_Hourly);


        ((TextView)findViewById(R.id.BD_Federal_tax_value)).setText(BD_Federal_tax_value);
        ((TextView)findViewById(R.id.BD_FICA_tax_value)).setText(BD_FICA_tax_value);
        ((TextView)findViewById(R.id.BD_State_tax_value)).setText(BD_State_tax_value);
        ((TextView)findViewById(R.id.BD_Total_tax_value)).setText(BD_Total_tax_value);
        ((TextView)findViewById(R.id.BD_Annual_salary_value)).setText(BD_Annual_salary_value);
        ((TextView)findViewById(R.id.BD_Taxed_annually_value)).setText(BD_Taxed_annually_value);
        ((TextView)findViewById(R.id.BD_Monthly_value)).setText(BD_Monthly_value);
        ((TextView)findViewById(R.id.BD_Biweekly_value)).setText(BD_Biweekly_value);
        ((TextView)findViewById(R.id.BD_Weekly_value)).setText(BD_Weekly_value);
        ((TextView)findViewById(R.id.BD_Hourly_value)).setText(BD_Hourly_value);

    }

    public void openMainAcivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public double Calculate_IncomeTax(double taxable_salary) {

        double income_tax = 0;
        double gross_salary = MainActivity.entered_salary;
        double tax_bracket_salary;

        if (taxable_salary <= 0) {
            income_tax = (gross_salary * 0.965) - gross_salary;
        }
        // 1st tax bracket, Federal tax of 10% + add a flat 3.5% for GA tax = 0.865
        else if (taxable_salary > 0 && taxable_salary <= 9950) {
            income_tax = (taxable_salary * 0.865) - taxable_salary ;
        }
        // 2nd tax bracket
        else if (taxable_salary > 9950 && taxable_salary <= 40525) {
            tax_bracket_salary = ((taxable_salary - 9950) * 0.88);
            income_tax = (tax_bracket_salary + 8955) - taxable_salary;
        }
        // 3rd tax bracket
        else if (taxable_salary > 40525 && taxable_salary <= 86375) {
            tax_bracket_salary = ((taxable_salary - 40525) * 0.78);
            income_tax = (tax_bracket_salary + 35860) - taxable_salary;
        }
        // 4th tax bracket, nested if statement because of max social security tax at 142.8k
        else if (taxable_salary > 86375 && taxable_salary <= 164925) {
            tax_bracket_salary = ((taxable_salary - 86375) * 0.76);
            income_tax = (tax_bracket_salary + 71833) - taxable_salary;

        }
        // 5th tax bracket
        else if (taxable_salary > 164925 && taxable_salary <= 209425) {
            tax_bracket_salary = ((taxable_salary - 164925) * 0.68);
            income_tax = (tax_bracket_salary + 131530) - taxable_salary;
        }
        // 6th tax bracket
        else if (taxable_salary > 209425 && taxable_salary <= 523600) {
            tax_bracket_salary = ((taxable_salary - 209425) * 0.65);
            income_tax = (tax_bracket_salary + 161789) - taxable_salary;
        }
        // 7th & final tax bracket
        else if (taxable_salary > 523600) {
            tax_bracket_salary = ((taxable_salary - 523600) * 0.63);
            income_tax = (tax_bracket_salary + 366002) - taxable_salary;
        }
        return Math.abs(income_tax);
    }


}