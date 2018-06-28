package com.appsgenii.stripeintegration;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

public class MainActivity extends AppCompatActivity {

    private EditText etCardNumber, etCardCVC, etCardExpiryMonth, etCardExpiryYear;
    private Button btnDone;
    private Context context;
    private Card card;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        initViews();
        registerListeners();
    }

    private void initViews() {
        etCardNumber = findViewById(R.id.etCardNumber);
        etCardCVC = findViewById(R.id.etCardCVC);
        etCardExpiryMonth = findViewById(R.id.etCardExpiryMonth);
        etCardExpiryYear = findViewById(R.id.etCardExpiryYear);
        btnDone = findViewById(R.id.btnDone);

        etCardNumber.setText("4242424242424242");
        etCardCVC.setText("123");
        etCardExpiryMonth.setText("12");
        etCardExpiryYear.setText("2019");

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Verifying Card");
        progressDialog.setCancelable(false);
    }

    private void registerListeners() {
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateForm();
            }
        });
    }

    private void validateForm() {
        if (!TextUtils.isEmpty(etCardNumber.getText())
                && !TextUtils.isEmpty(etCardCVC.getText())
                && !TextUtils.isEmpty(etCardExpiryMonth.getText())
                && !TextUtils.isEmpty(etCardExpiryYear.getText())) {

            generateCard();

        } else {
            Toast.makeText(MainActivity.this, "Incomplete Information", Toast.LENGTH_SHORT).show();
        }
    }

    private void generateCard() {
        card = new Card(etCardNumber.getText().toString().trim(),
                Integer.parseInt(etCardExpiryMonth.getText().toString().trim()),
                Integer.parseInt(etCardExpiryYear.getText().toString().trim()),
                etCardCVC.getText().toString().trim());
        card.setName("Sadda Hussain");

        validateCard();
    }

    private void validateCard() {
        showDialog();
        if (card.validateCard()) {
            generateToken();

        } else {
            dismissDialog();
            Toast.makeText(MainActivity.this, "Invalid Card", Toast.LENGTH_SHORT).show();
        }
    }

    private void generateToken() {
        Stripe stripe = new Stripe(context, BuildConfig.API_KEY);
        stripe.createToken(
                card,
                new TokenCallback() {
                    public void onSuccess(Token token) {
                        //Send this token to server and use it according to your business logic
                        dismissDialog();
                        Toast.makeText(context, token.getCard().getName(), Toast.LENGTH_SHORT).show();
                    }

                    public void onError(Exception error) {
                        dismissDialog();
                        Toast.makeText(context,
                                error.getLocalizedMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }

    private void showDialog() {
        progressDialog.show();
    }

    private void dismissDialog() {
        progressDialog.dismiss();
    }
}