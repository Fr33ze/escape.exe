package at.ac.tuwien.mmue_sb10;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import at.ac.tuwien.mmue_sb10.persistence.User;

public class FinishStageActivity extends Activity {

    User user;
    String stage_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_stage);
    }

    @Override
    protected void onResume() {
        super.onResume();

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        user = (User)getIntent().getSerializableExtra("user");
        stage_name = getIntent().getStringExtra("stage_name");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
    }

    public void onCLickLayout(View view) {
        finish();
    }
}