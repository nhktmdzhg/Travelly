package com.nhk.travelly;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainHomeLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void search(@NonNull View view) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.search_dialog);
        LinearLayout linearLayout = (LinearLayout) view.getParent();
        TextView searchTextView = (TextView) linearLayout.getChildAt(0);
        String searchType = searchTextView.getText().toString();
        TextView message = dialog.findViewById(R.id.dialogMessage);
        message.setText(String.format("Search for %s", searchType));
        LinearLayout OKButton = dialog.findViewById(R.id.dialogOkButton);
        OKButton.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    public void bookingServices(@NonNull View view) {
        if (view.getId() != R.id.transport) {
            Toast.makeText(this, "This feature is not available yet", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, BookingActivity.class);
        intent.putExtra("isTransport", true);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void changeTab(@NonNull View view) {
        int id = view.getId();
        Intent intent;
        if (id == R.id.account)
            intent = new Intent(this, AccountActivity.class);
        else if (id == R.id.booking)
            intent = new Intent(this, BookingActivity.class);
        else {
            Toast.makeText(this, "This feature is not available yet", Toast.LENGTH_SHORT).show();
            return;
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtra("isTransport", false);
        startActivity(intent);
    }
}