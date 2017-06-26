package rocky.teatime.activities;

import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import rocky.teatime.R;
import rocky.teatime.database.TeaStuff.Tea;
import rocky.teatime.fragments.tea_detail.TeaBasicsFragment;

public class ViewTeaActivity extends AppCompatActivity {

    TeaBasicsFragment basicsFragment;   // A reference to the fragment tracking all of the basics.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String jsonsedTea = getIntent().getExtras().getString(Tea.TEA_PAYLOAD_KEY);
        setContentView(R.layout.activity_view_tea);
        basicsFragment = TeaBasicsFragment.newInstance(jsonsedTea);
        getSupportFragmentManager().beginTransaction().add(R.id.view_fragment_container,
                basicsFragment).commit();
    }


    public void onFragmentInteractListener() {

    }
}
