package s.a.m.a.nr;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.cm.kinfoc.KInfocClientAssist;
import com.cm.kinfoc.api.InfocInitHelper;

public class MainActivity extends Activity {

    Button btn_01 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InfocInitHelper.init(this);
        setContentView(R.layout.activity_main);
        btn_01 = (Button) findViewById(R.id.btn_01);
        btn_01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KInfocClientAssist.getInstance().forceReportData("newsrepublic_act3", "");
            }
        });
    }
}
