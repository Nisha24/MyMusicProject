package com.nisha.apps.musicproject;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;

import java.util.ArrayList;

public class AllSongFragment extends ListFragment {


    private static ContentResolver contentResolver1;

    public ArrayList<SongsList> songsList;
    public ArrayList<SongsList> newList;
    String select;
    private ListView listView;

    private createDataParse createDataParse;
    private ContentResolver contentResolver;

    public static Fragment getInstance(int position, ContentResolver mcontentResolver) {
        Bundle bundle = new Bundle();
        bundle.putInt("pos", position);
        AllSongFragment tabFragment = new AllSongFragment();
        tabFragment.setArguments(bundle);
        contentResolver1 = mcontentResolver;
        return tabFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        createDataParse = (createDataParse) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_tab, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        listView = view.findViewById(R.id.list_playlist);
        contentResolver = contentResolver1;
        setContent();
    }

    /**
     * Setting the content in the listView and sending the data to the Activity
     */
    public void setContent() {
        boolean searchedList = false;
        songsList = new ArrayList<>();
        newList = new ArrayList<>();
        getMusic();
        SongAdapter adapter = new SongAdapter(getContext(), songsList);
        if (!createDataParse.queryText().equals("")) {
            adapter = onQueryTextChange();
            adapter.notifyDataSetChanged();
            searchedList = true;
        } else {
            searchedList = false;
        }
        createDataParse.getLength(songsList.size());
        listView.setAdapter(adapter);

        final boolean finalSearchedList = searchedList;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Toast.makeText(getContext(), "You clicked :\n" + songsList.get(position), Toast.LENGTH_SHORT).show();
                if (!finalSearchedList) {
                    createDataParse.onDataPass(songsList.get(position).getTitle(), songsList.get(position).getPath());
                    createDataParse.fullSongList(songsList, position);
                } else {
                    createDataParse.onDataPass(newList.get(position).getTitle(), newList.get(position).getPath());
                    createDataParse.fullSongList(songsList, position);
                }
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showDialog(position);
                return true;
            }
        });
    }


    public void getMusic() {
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCursor = contentResolver.query(songUri, null, null, null, null);
        if (songCursor != null && songCursor.moveToFirst()) {
            int songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int songPath = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);

            do {
                songsList.add(new SongsList(songCursor.getString(songTitle), songCursor.getString(songArtist), songCursor.getString(songPath)));
            } while (songCursor.moveToNext());
            songCursor.close();
        }
    }

    public SongAdapter onQueryTextChange() {
        String text = createDataParse.queryText();
        for (SongsList songs : songsList) {
            String title = songs.getTitle().toLowerCase();
            if (title.contains(text)) {
                newList.add(songs);
            }
        }
        return new SongAdapter(getContext(), newList);

    }

    private void showDialog(final int position) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        final String[] items = {"None", "Set Ringtone", "Delete Song" , "Add to favourites"};
        builder.setTitle(getString(R.string.play_next));
        final int checked = -1;
        builder.setSingleChoiceItems(items, checked, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//                Toast.makeText(MainActivity.this, checkedItem, Toast.LENGTH_SHORT).show();
                select = items[i];
//                Toast.makeText(getContext(), select, Toast.LENGTH_SHORT).show();
                switch (i) {
                    case 0:
                        Toast.makeText(getContext(), select, Toast.LENGTH_SHORT).show();
                        break;

                    case 3:
                        Toast.makeText(getContext(), select, Toast.LENGTH_SHORT).show();
                        break;
                }


            }
        });;
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(select != null){
                    createDataParse.currentSong(songsList.get(position));
                        setContent();
                }
                else {
                    Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                }
//                Toast.makeText(getContext(), "Item selected: " + select, Toast.LENGTH_SHORT).show();

//                switch (which){
//                    case 0:
//                        Toast.makeText(getContext(), "None Selected", Toast.LENGTH_SHORT).show();
//                        break;
//
//                    case 1:
//                        Toast.makeText(getContext(), "Play Next Selected", Toast.LENGTH_SHORT).show();
//                        break;
//
//                    case 2:
//                        Toast.makeText(getContext(), "Set Ringtone Selected", Toast.LENGTH_SHORT).show();
//                        break;
//                }
            }
        });
        builder.show();
    }

    public interface createDataParse {
        public void onDataPass(String name, String path);

        public void fullSongList(ArrayList<SongsList> songList, int position);

        public String queryText();

        public void currentSong(SongsList songsList);
        public void getLength(int length);
    }

}
