package com.main.maybe.miplayer;

import android.content.ServiceConnection;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.main.maybe.miplayer.binder.MusicPlayerServiceBinder;
import com.main.maybe.miplayer.music.Music;
import com.main.maybe.miplayer.music.MusicScanner;
import com.main.maybe.miplayer.service.MusicPlayerService;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {


    List<Music> list;
    List<Music> nowPlaying;
    List<Music> library;


    private SeekBar mSeekBar;
    private TextView mTotalTime;
    private TextView mCurrentTime;

    MusicPlayerService mService;
    MusicPlayerServiceBinder mBinder;
    ServiceConnection mConnection;
    OnSeekBarChangeListener mOnSeekBarChangeListener;
    boolean mBound = false;

    List<String> filePaths = null;
    String typOfOrders[] = {"Title", "Artist"};

    int state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // maybe mean nothing
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        library = new ArrayList<Music>();

        final MusicScanner musicScanner = new MusicScanner();

        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }
}
