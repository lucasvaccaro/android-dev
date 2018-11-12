package com.example.i857501.bankaccountapp;

import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    public static final String SHARED_PREFERENCES_NAME = "general";

    EditText fullname, cpfCnpj;
    RadioGroup personType;
    Spinner state, city;
    HashMap<String, String[]> cities;
    CheckBox creditCard, cardInsurance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.fullname = findViewById(R.id.fullname);
        this.cpfCnpj = findViewById(R.id.cpf_cnpj);
        this.personType = findViewById(R.id.person_type);

        this.state = findViewById(R.id.state);
        this.city = findViewById(R.id.city);

        this.cities = new HashMap<>();
        this.cities.put(getString(R.string.pr), new String[]{ "Cascavel", "Curitiba", "Londrina" });
        this.cities.put(getString(R.string.sc), new String[]{ "Criciúma", "Florianópolis", "Joinvile" });
        this.cities.put(getString(R.string.rs), new String[]{ "Canoas", "Porto Alegre", "São Leopoldo" });

        this.creditCard = findViewById(R.id.credit_card);
        this.cardInsurance = findViewById(R.id.card_insurance);

        this.personType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                String hint = checkedId == R.id.pf ? getString(R.string.cpf) : getString(R.string.cnpj);
                cpfCnpj.setHint(hint);
                cpfCnpj.setText(null);
            }
        });

        this.state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedState = parent.getItemAtPosition(position).toString();
                loadCities(selectedState);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        this.creditCard.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                cardInsurance.setEnabled(isChecked);
                cardInsurance.setChecked(isChecked);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.loadPreferences();
    }

    private void loadCities(String selectedState) {
        String[] selectedCities = this.cities.get(selectedState);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, selectedCities);
        this.city.setAdapter(adapter);

        SharedPreferences preferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        String savedCity = preferences.getString("city", null);
        if (savedCity != null) {
            int position = adapter.getPosition(savedCity);
            this.city.setSelection(position);
        }
    }

    public void createAccount(View view) {
        String pattern = "%s\n%s: %s\n%s - %s\n%s: %s\n%s: %s";

        String fullname = this.fullname.getText().toString();
        String label = this.cpfCnpj.getHint().toString();
        String cpfCnpj = this.cpfCnpj.getText().toString();
        String city = this.city.getSelectedItem().toString();
        String state = this.state.getSelectedItem().toString();
        String creditCardLabel = this.creditCard.getText().toString();
        String creditCardValue = this.creditCard.isChecked() ? getString(R.string.yes) : getString(R.string.no);
        String cardInsuranceLabel = this.cardInsurance.getText().toString();
        String cardInsuranceValue = this.cardInsurance.isChecked() ? getString(R.string.yes) : getString(R.string.no);

        String message = String.format(pattern,
                fullname,
                label, cpfCnpj,
                city, state,
                creditCardLabel, creditCardValue,
                cardInsuranceLabel, cardInsuranceValue);

        new AlertDialog.Builder(this)
            .setTitle(R.string.account_info)
            .setMessage(message)
            .create()
            .show();

        this.savePreferences();
    }

    private void savePreferences() {
        SharedPreferences.Editor editor = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE).edit();

        String state = this.state.getSelectedItem().toString();
        String city = this.city.getSelectedItem().toString();
        int personType = this.personType.getCheckedRadioButtonId();
        boolean creditCard = this.creditCard.isChecked();
        boolean cardInsurance = this.cardInsurance.isChecked();

        editor.putString("state", state);
        editor.putString("city", city);
        editor.putInt("personType", personType);
        editor.putBoolean("creditCard", creditCard);
        editor.putBoolean("cardInsurance", cardInsurance);

        editor.apply();
    }

    private void loadPreferences() {
        SharedPreferences preferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);

        int savedPersonType = preferences.getInt("personType", R.id.pf);
        this.personType.check(savedPersonType);

        String savedState = preferences.getString("state", getString(R.string.pr));
        int position = ((ArrayAdapter)this.state.getAdapter()).getPosition(savedState);
        this.state.setSelection(position);

        this.creditCard.setChecked(preferences.getBoolean("creditCard", false));
        this.cardInsurance.setChecked(preferences.getBoolean("cardInsurance", false));
    }
}
