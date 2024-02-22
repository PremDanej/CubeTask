package com.merp.my.cube.task.ui;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.color.DynamicColors;
import com.merp.my.cube.task.R;

public class MainActivity extends AppCompatActivity {


    public int gridSize = 0;
    private ImageView[][] boxes;
    private GridLayout gridLayout;
    private Spinner spinnerDirection;
    private EditText edtNumber;
    private boolean isValidate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DynamicColors.applyToActivitiesIfAvailable(this.getApplication());

        Button btnSubmit    = findViewById(R.id.btnSubmit);
        Button btnReset     = findViewById(R.id.btnReset);
        spinnerDirection    = findViewById(R.id.spinnerDirection);
        gridLayout          = findViewById(R.id.gridLayout);
        edtNumber           = findViewById(R.id.edtNumber);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.spinnerItem, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDirection.setAdapter(adapter);

        btnSubmit.setOnClickListener(view -> {
            isValidate = fieldValidate();
            if (isValidate) {
                gridSize = Integer.parseInt(edtNumber.getText().toString());
                if (gridSize == 0) {
                    edtNumber.requestFocus();
                    edtNumber.setError("Number must be greater than 0.");
                } else {
                    createGrid();
                    edtNumber.clearFocus();
                }
            }
        });

        btnReset.setOnClickListener(v -> resetGrid());
    }

    private boolean fieldValidate() {
        if(edtNumber.length() == 0){
            edtNumber.requestFocus();
            edtNumber.setError("This field is required.");
            return false;
        }
        return true;
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    private void resetGrid(){
        gridSize = 0;
        gridLayout.removeAllViews(); // Clearing any existing views
        boxes = new ImageView[][]{};
        edtNumber.setText("");
        edtNumber.clearFocus();
        spinnerDirection.setSelection(0);
    }

    private void createGrid() {
        gridSize = Integer.parseInt(edtNumber.getText().toString());
        gridLayout.removeAllViews(); // Clearing any existing views
        gridLayout.setColumnCount(gridSize);
        gridLayout.setRowCount(gridSize);

        boxes = new ImageView[gridSize][gridSize];
        int myWidth = (getScreenWidth() - 120) / gridSize;
        int myHeight = (getScreenWidth() - 120) / gridSize;

        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                ImageView box = new ImageView(this);
                box.setImageResource(R.drawable.box_drawable);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = myWidth; // Adjust box width as needed
                params.height = myHeight; // Adjust box height as needed
                params.setMargins(5, 5, 5, 5); // Adjust margins as needed
                box.setLayoutParams(params);
                box.setOnClickListener(new BoxClickListener(i, j));
                boxes[i][j] = box;
                gridLayout.addView(box);
            }
        }
    }

    private void blinkSurroundingBoxes(final int row, final int col) {
        switch (spinnerDirection.getSelectedItem().toString()) {
            case "Diagonal":
                blinkLeftDiagonal(row, col);
                blinkRightDiagonal(row, col);
                break;
            case "Horizontal":
                blinkHorizontal(row, col);
                break;
            case "Vertical":
                blinkVertical(row, col);
                break;
            case "HV":
                blinkHorizontal(row, col);
                blinkVertical(row, col);
                break;
            case "Square":
                blinkSquare(row, col);
                break;
            case "Selection Based":
                blinkSelection(row, col);
                break;
            default:
                Toast.makeText(this, "Please Select Direction", Toast.LENGTH_SHORT).show();
        }
    }

    private void blinkSelection(int row, int col) {
        for(int myRow = 0; myRow <= row;myRow++){
            for(int myCol = 0; myCol <= col; myCol++){
                blinkBox(myRow,myCol);
            }
        }
    }

    private void blinkVertical(int row, int col) {
        for(int myRow = row; myRow < gridSize; myRow++) blinkBox(myRow,col);
        for(int myRow = row; myRow >= 0; myRow--) blinkBox(myRow,col);
    }

    private void blinkHorizontal(int row, int col) {
        for(int myCol = col; myCol >= 0; myCol--){
            blinkBox(row,myCol);
        }
        for(int myCol = col; myCol < gridSize; myCol++){
            blinkBox(row,myCol);
        }
    }

    private void blinkSquare(int row, int col) {
        blinkBox(row , col); // Blink box
        blinkBox(row - 1, col); // Blink top box
        blinkBox(row + 1, col); // Blink bottom box
        blinkBox(row, col - 1); // Blink left box
        blinkBox(row, col + 1); // Blink right box
        blinkBox(row - 1, col - 1); // Blink top left box
        blinkBox(row - 1, col + 1); // Blink top right box
        blinkBox(row + 1, col - 1); // Blink bottom left box
        blinkBox(row + 1, col + 1); // Blink bottom right box
    }

    private void blinkLeftDiagonal(int row, int col) {
        int rightCol = col;
        // Top - Right Direction
        for(int myRow = row; myRow >= 0; myRow--){
            blinkBox(myRow,rightCol++);
        }
        // Bottom - Left Direction
        int leftCol = col;
        for(int myRow = row; myRow < gridSize; myRow++){
            blinkBox(myRow,leftCol--);
        }
    }
    private void blinkRightDiagonal(int row, int col) {
        // Top - Left Direction
        int leftCol = col;
        for(int myRow = row; myRow >= 0; myRow--){
            blinkBox(myRow, leftCol--);
        }

        // Bottom - Right Direction
        int rightCol = col;
        for(int myRow = row; myRow < gridSize; myRow++){
            blinkBox(myRow, rightCol++);
        }
    }

    private void blinkBox(final int row, final int col) {
        if (row >= 0 && row < gridSize && col >= 0 && col < gridSize) {
            final ImageView box = boxes[row][col];
            final Animation blinkAnimation = new AlphaAnimation(1, 0);
            blinkAnimation.setDuration(500);
            blinkAnimation.setRepeatCount(1);
            blinkAnimation.setRepeatMode(Animation.REVERSE);
            box.startAnimation(blinkAnimation);
        }
    }

    private class BoxClickListener implements View.OnClickListener {
        private final int row;
        private final int col;

        public BoxClickListener(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void onClick(View v) {
            // Perform your desired action here
            // Toast.makeText(MainActivity.this, "Box clicked at: (" + row + ", " + col + ")", Toast.LENGTH_SHORT).show();
            blinkSurroundingBoxes(row, col);
        }
    }

}