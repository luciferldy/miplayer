package com.main.maybe.miplayer.task;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.main.maybe.miplayer.ui.activity.MusicPlayerActivity;
import com.main.maybe.miplayer.R;
import com.main.maybe.miplayer.ui.adapter.AlbumListAdapter;
import com.main.maybe.miplayer.ui.adapter.ArtistListAdapter;
import com.main.maybe.miplayer.ui.fragment.MusicPlayerFragment;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Maybe霏 on 2015/5/17.
 */
public class LoadingListTask extends AsyncTask<Void, Void, Boolean> {

    private static final String LOG_TAG = LoadingListTask.class.getSimpleName();

    private int type = 0; // 0 song, 1 artist, 2 album
    private Context context;
    public static final String songId = "songId";
    public static final String songName = "songName";
    public static final String artistName = "artistName";
    public static final String albumName = "albumName";
    public static final String path = "path";
    public static final String duration_t = "duration_t";
    public static final String duration = "duration";

    public static final String artistId = "artistId";
    public static final String songNumber = "songNumber";

    public static final String albumId = "albumId";
    public static final String albumArt = "albumArt";

    public static final String songList = "songList";
    public static final String playPosition = "playPosition";

    public static final String ENTER_FSMUSIC_PLAYER_FROM_WHERE = "ENTER_FSMUSIC_PLAYER_FROM_WHERE";

    private ArrayList<HashMap<String, String>> items;
    private RecyclerView rv;
    private LayoutInflater mInflater;
    private FragmentManager fmanager;

    public LoadingListTask(int type, Context context, RecyclerView rv, LayoutInflater mInflater, FragmentManager fmanager){
        this.type = type;
        this.context = context;
        this.rv = rv;
        this.mInflater = mInflater;
        this.fmanager = fmanager;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        // success
        if (aBoolean){
            switch (type){
                case 0:
//                    rv.setAdapter(new SongListAdapter(items, mInflater));
//                    rv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                        @Override
//                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                            // add the item to music queue
//                            // play music
//                            Intent intent = new Intent(context, MusicPlayerActivity.class);
//                            intent.putExtra(songList, items);
//                            intent.putExtra(playPosition, position);
//                            intent.putExtra(ENTER_FSMUSIC_PLAYER_FROM_WHERE, MusicPlayerFragment.FROM_CLICK_ITEM);
//                            context.startActivity(intent);
//                        }
//                    });
                    break;
                case 1:
                    // artist
//                    lv.setAdapter(new ArtistListAdapter(items, mInflater));
//                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                        @Override
//                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                            int Id;
//                            Id = Integer.parseInt(items.get(position).get(LoadingListTask.artistId));
//                            intent.putExtra("type", "artist");
//                            intent.putExtra(LoadingListTask.artistId, Id);
//                            context.startActivity(intent);

                            // change the fragment
//                            Bundle  b = new Bundle();
//                            b.putString("type", "artist");
//                            b.putInt(LoadingListTask.artistId, Id);
//
//                            SongInAlbumOrArtFragment sia = new SongInAlbumOrArtFragment();
//                            FragmentTransaction ft = fmanager.beginTransaction();
//                            ft.add(sia, null);
//                            sia.setArguments(b);
//                            ft.addToBackStack(null);
//                            Log.d(LOG_TAG, "artist item click before commit");
//                            ft.commit();
//                            Log.d(LOG_TAG, "artist item click and commit");
//                        }
//                    });
                    break;
                case 2:
                    // album
//                    lv.setAdapter(new AlbumListAdapter(items, mInflater));
//                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                        @Override
//                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                            // replace the fragment
//                            int Id;
//                            Id = Integer.parseInt(items.get(position).get(LoadingListTask.albumId));
//                            intent.putExtra("type", "album");
//                            intent.putExtra(LoadingListTask.albumId, Id);
//                            context.startActivity(intent);
//                        }
//                    });
                    break;
                case 3:
//                    ArrayList<String> list_name = new ArrayList<>();
//                    list_name.add("正在播放列表");
//                    lv.setAdapter(new ArrayAdapter<>(
//                            context, R.layout.mylistitem, R.id.mylist_name, list_name
//                    ));
//                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                        @Override
//                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                            // transfer the current queue
//                            intent.putExtra("type", "self_list");
//                            context.startActivity(intent);
//                        }
//                    });
                    break;
            }
        }else {
            // notice that something wrong
            String v;
            switch (type){
                case 0:
                    v = "歌曲";
                    break;
                case 1:
                    v = "艺术家";
                    break;
                case 2:
                    v = "专辑";
                    break;
                default:
                    v = "";
            }
            Toast.makeText(context, "没有成功从"+v+"列表成功获得信息", Toast.LENGTH_SHORT);
        }
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        items = new ArrayList<>();
        switch (type){
            case 0:
                items = getSongFromProvider();
                break;
            case 1:
                items = getArtistFromProvider();
                break;
            case 2:
                items = getAlbumFromProvider();
                break;
            default:
                break;
        }
        if (items==null)
            return false;
        else
            return true;
    }

    public ArrayList<HashMap<String, String>> getSongFromProvider(){
        ArrayList<HashMap<String, String>> songs = new ArrayList<>();
        ContentResolver resolver = context.getContentResolver();
        int second;
        try {
            // get audio from content resolver
            Cursor cursor = resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
                    MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
            HashMap<String, String> item;
//            Log.d(TAG_MSG, "songs id");
            while (cursor.moveToNext()){
                item = new HashMap<>();
                item.put(songId, cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));

//                Log.d(TAG_MSG, cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));
//                Log.d(TAG_MSG, cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)));
//                Log.d(TAG_MSG, cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID)));

                second = Integer.parseInt(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))) / 1000;


                item.put(songName, cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
                item.put(artistName, cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
                item.put(albumName, cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)));
                item.put(albumId, cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)));

                int minute = second/60;
                second = second%60;
                String format_duration;
                if (minute < 10 && second < 10)
                    format_duration = "0" + minute + ":" + "0" + second;
                else if (minute < 10 && second > 10)
                    format_duration = "0" + minute + ":" + second;
                else if (minute > 10 && second < 10)
                    format_duration = minute + ":" + "0"+second;
                else
                    format_duration = minute + ":" + second;
                item.put(duration, format_duration);

                item.put(duration_t, cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)));
                item.put(path, cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
                songs.add(item);
            }
            return songs;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    public ArrayList<HashMap<String, String>> getAlbumFromProvider(){
        ArrayList<HashMap<String, String>> albums = new ArrayList<>();
        ContentResolver resolver = context.getContentResolver();
        try {
            Cursor cursor = resolver.query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, null, null, null,
                    MediaStore.Audio.Albums.DEFAULT_SORT_ORDER);
            HashMap<String, String> item;

//            Log.d(TAG_MSG, "albums id");
            while (cursor.moveToNext()){
                item = new HashMap<>();

//                Log.d(TAG_MSG, cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums._ID)));

                item.put(albumId, cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums._ID)));
                item.put(albumName, cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM)));
                item.put(artistName, cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST)));
                item.put(songNumber, cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.NUMBER_OF_SONGS)));
                item.put(albumArt, cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART)));

                albums.add(item);
            }
            return albums;
        }catch (Exception e){
            e.printStackTrace();
            return albums;
        }
    }

    public ArrayList<HashMap<String, String>> getArtistFromProvider(){
        ArrayList<HashMap<String, String>> artists = new ArrayList<>();
        ContentResolver resolver = context.getContentResolver();
        try{
            Cursor cursor = resolver.query(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, null, null, null,
                    MediaStore.Audio.Artists.DEFAULT_SORT_ORDER);
            HashMap<String, String> item;
//            Log.d(TAG_MSG, "artist id");
            while (cursor.moveToNext()){
                item = new HashMap<>();

//                Log.d(TAG_MSG, cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Artists._ID)))

                item.put(artistId, cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Artists._ID)));
                item.put(artistName, cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST)));
                item.put(songNumber, cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS)));
                artists.add(item);
            }
            return artists;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

}
