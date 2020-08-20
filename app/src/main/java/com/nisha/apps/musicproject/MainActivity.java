package com.nisha.apps.musicproject;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Bundle;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.SearchView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatToggleButton;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AllSongFragment.createDataParse, FavSongFragment.createDataParsed, CurrentSongFragment.createDataParsed {

    private Menu menu;

    private ImageButton imgBtnPlayPause, imgbtnReplay, imgBtnPrev, imgBtnNext, imgBtnRepeat;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private SeekBar seekbarController;
    SharedPreferences sharedPreferences;
    Toolbar toolbar;
    private DrawerLayout mDrawerLayout;
    private TextView tvCurrentTime, tvTotalTime;

    private ArrayList<SongsList> songList;
    private int currentPosition;
    private String searchText = "";
    private SongsList currSong;
    int count = 0;
    private boolean checkFlag = false, repeatoneFlag = false, playContinueFlag = false, favFlag = true, playlistFlag = false, playflag = true;
    private final int MY_PERMISSION_REQUEST = 100;
    private int allSongLength;

    MediaPlayer mediaPlayer;
    Handler handler;
    Runnable runnable;
    SwitchCompat switchCompat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        grantedPermission();
        Intent i = new Intent(getApplicationContext() , MainActivity.class);
        startActivity(i);

    }

    private void init() {
        imgBtnPrev = findViewById(R.id.img_btn_previous);
        imgBtnNext = findViewById(R.id.img_btn_next);
        imgbtnReplay = findViewById(R.id.img_btn_replay);
        imgBtnRepeat = findViewById(R.id.img_btn_repeat);

        tvCurrentTime = findViewById(R.id.tv_current_time);
        tvTotalTime = findViewById(R.id.tv_total_time);
        FloatingActionButton refreshSongs = findViewById(R.id.btn_refresh);
        seekbarController = findViewById(R.id.seekbar_controller);
        viewPager = findViewById(R.id.songs_viewpager);
        NavigationView navigationView = findViewById(R.id.nav_view);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        imgBtnPlayPause = findViewById(R.id.img_btn_play);
        toolbar = findViewById(R.id.toolbar);
        handler = new Handler();
        mediaPlayer = new MediaPlayer();

        toolbar.setTitleTextColor(getResources().getColor(R.color.textColor));
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);

        imgBtnNext.setOnClickListener(this);
        imgBtnPrev.setOnClickListener(this);
        imgbtnReplay.setOnClickListener(this);
        refreshSongs.setOnClickListener(this);
        imgBtnPlayPause.setOnClickListener(this);
        imgBtnRepeat.setOnClickListener(this);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);
                mDrawerLayout.closeDrawers();
                switch (item.getItemId()) {
                    case R.id.nav_about:
                        about();
                        break;
                    case R.id.nav_share:
                        share();
                        break;

                    case R.id.nav_theme_color:
//                        Intent i = new Intent(getApplicationContext(), ThemeActivity.class);
//                        startActivity(i);
                        theme();
                        break;

                    case R.id.nav_rate:
                        ratingbar();
                        break;

                    case R.id.nav_sleep_timer:
//                        sleep();
                        break;
                }
                return true;
            }
        });
    }

    private void grantedPermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            } else {
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    Snackbar snackbar = Snackbar.make(mDrawerLayout, "Provide the Storage Permission", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        } else {
            setPagerLayout();
        }
    }

    /**
     * Checking if the permission is granted or not
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                        setPagerLayout();
                    } else {
                        Snackbar snackbar = Snackbar.make(mDrawerLayout, "Provide the Storage Permission", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        finish();
                    }
                }
        }
    }

    /**
     * Setting up the tab layout with the viewpager in it.
     */

    private void setPagerLayout() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), getContentResolver());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    /**
     * Function to show the dialog for about us.
     */
    private void about() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.about))
                .setMessage(getString(R.string.about_text))
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void share() {
        Intent i = new Intent((Intent.ACTION_VIEW));
        i.setAction(Intent.ACTION_SEND);
        i.setType("text/plain");
        String sb = "Download this Application now:-https://www.youtube.com";
        i.putExtra(Intent.EXTRA_TEXT, sb);
        startActivity(i);
    }
    private void sleep(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View layout= null;
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout = inflater.inflate(R.layout.sleep, null);
        final TimePicker timePicker = (TimePicker) layout.findViewById(R.id.timePicker1);
        builder.setTitle("Set Sleep Timer");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                long value = timePicker.getDrawingTime();
                Toast.makeText(MainActivity.this,"Time is : "+value, Toast.LENGTH_LONG).show();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);
        builder.setView(layout);
        builder.show();
    }
    private void ratingbar(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View layout= null;
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout = inflater.inflate(R.layout.ratingbar, null);
        final RatingBar ratingBar = (RatingBar)layout.findViewById(R.id.ratingBar);
        builder.setTitle("Rate This App");
        builder.setMessage("Thank you for rating us , it will help us to provide you the best service .");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Float value = ratingBar.getRating();
                Toast.makeText(MainActivity.this,"Rating is : "+value, Toast.LENGTH_LONG).show();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);
        builder.setView(layout);
        builder.show();

    }

    private void theme() {

//        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
//        alertDialog.setTitle("Choose Themes..");
//        String[] items = {"Dark Theme", "Light Theme"};
//        int checkedItem = 1;
//        alertDialog.setSingleChoiceItems(items, checkedItem, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                switch (which) {
//                    case 0:
//                        break;
//                    case 1:
//                        break;
//                }
//            }
//        });
//        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//            }
//        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//            }
//        });
//        AlertDialog alert = alertDialog.create();
//        alert.setCanceledOnTouchOutside(false);
//        alert.show();
//    }

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        final boolean isDarkModeOn = sharedPreferences.getBoolean("isDarkModeOn", false);
        if (isDarkModeOn) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            builder.setMessage("Disable Dark Mode");
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            builder.setMessage("Enable Dark Mode");
        }
        builder.setTitle("Theme")
                .setMessage("Want to change the Theme")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

                        // it will set isDarkModeOn
                        // boolean to true
                        editor.putBoolean("isDarkModeOn", true);
                        editor.apply();

                        // change text of Button
                        builder.setMessage("Disable Dark Mode");

//                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                // it will set isDarkModeOn
                // boolean to false
                editor.putBoolean("isDarkModeOn", false);
                editor.apply();

                // change text of Button
                builder.setMessage("Enable Dark Mode");
//                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.action_bar_menu, menu);
        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchText = newText;
                queryText();
                setPagerLayout();
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("WrongConstant")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(Gravity.START);
                return true;
            case R.id.menu_search:
                Toast.makeText(this, "Search", Toast.LENGTH_SHORT).show();
                return true;
//            case R.id.menu_play:
//                if (playlistFlag) {
//                    if (mediaPlayer != null) {
//                        if (playflag) {
//                            Toast.makeText(this, "Added to Playlist", Toast.LENGTH_SHORT).show();
//                            item.setIcon(R.drawable.ic_playlist_add_black_24dp);
//                            SongsList playlist = new SongsList(songList.get(currentPosition).getTitle(),
//                                    songList.get(currentPosition).getSubTitle(), songList.get(currentPosition).getPath());
//                            CurrentPlaylistOperations currentPlaylistOperations = new CurrentPlaylistOperations(this);
//                            currentPlaylistOperations.addSongFav(playlist);
//                            setPagerLayout();
//                            playflag = false;
//                        } else {
////                            item.setIcon(R.drawable.ic_favorite_black_24dp);
//                            playflag = true;
//                        }
//                    }
//                    return true;
//                }
            case R.id.menu_favorites:
                if (checkFlag)
                    if (mediaPlayer != null) {
                        if (favFlag) {
                            Toast.makeText(this, "Added to Favorites", Toast.LENGTH_SHORT).show();
                            item.setIcon(R.drawable.ic_favorite_black_24dp);
                            SongsList favList = new SongsList(songList.get(currentPosition).getTitle(),
                                    songList.get(currentPosition).getSubTitle(), songList.get(currentPosition).getPath());
                            FavoritesOperations favoritesOperations = new FavoritesOperations(this);
                            favoritesOperations.addSongFav(favList);
                            setPagerLayout();
                            favFlag = false;
                        } else {
//                            item.setIcon(R.drawable.ic_favorite_black_24dp);
                            favFlag = true;
                        }
//                if (checkFlag)
//                    if (mediaPlayer != null) {
//                        if (favFlag) {
//                            Toast.makeText(this, "Added to Favorites", Toast.LENGTH_SHORT).show();
//                            item.setIcon(R.drawable.ic_playlist_add_black_24dp);
//                            SongsList favList = new SongsList(songList.get(currentPosition).getTitle(),
//                                    songList.get(currentPosition).getSubTitle(), songList.get(currentPosition).getPath());
//                            FavoritesOperations favoritesOperations = new FavoritesOperations(this);
//                            favoritesOperations.addSongFav(favList);
//                            setPagerLayout();
//                            favFlag = false;
//                        } else {
////                            item.setIcon(R.drawable.favorite_icon);
//                            favFlag = true;
//                        }
                    }
//            case R.id.img_btn_play:
//                if (playflag)
//                    if (mediaPlayer != null) {
//                        if (playflag) {
//                            Toast.makeText(this, "Added to Playlist", Toast.LENGTH_SHORT).show();
//                            item.setIcon(R.drawable.ic_playlist_add_black_24dp);
//                            SongsList playlist = new SongsList(songList.get(currentPosition).getTitle(),
//                                    songList.get(currentPosition).getSubTitle(), songList.get(currentPosition).getPath());
//                            CurrentPlaylistOperations currentPlaylistOperations = new CurrentPlaylistOperations(this);
//                            currentPlaylistOperations.addSongFav(playlist);
//                            setPagerLayout();
//                            playflag = false;
//                        } else {
////                            item.setIcon(R.drawable.ic_favorite_black_24dp);
//                            playflag = true;
//                        }
//                        return true;
//                    }
        }

        return super.onOptionsItemSelected(item);

    }

    /**
     * Function to handle the click events.
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_btn_play:
                if (checkFlag) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        imgBtnPlayPause.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                    } else if (!mediaPlayer.isPlaying()) {
                        mediaPlayer.start();
                        imgBtnPlayPause.setImageResource(R.drawable.ic_pause_black_24dp);
                        playCycle();
                    }
                } else {
                    Toast.makeText(this, "Select the Song ..", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_refresh:
                Toast.makeText(this, "Refreshing", Toast.LENGTH_SHORT).show();
                setPagerLayout();
                break;
            case R.id.img_btn_replay:
                if (repeatoneFlag) {
//                    Toast.makeText(this, "Replaying Removed..", Toast.LENGTH_SHORT).show();
                    imgbtnReplay.setImageResource(R.drawable.ic_repeat_black_less_24dp);
                    mediaPlayer.setLooping(false);
                    repeatoneFlag = false;
                } else {
                    Toast.makeText(this, "Replaying Added..", Toast.LENGTH_SHORT).show();
                    imgbtnReplay.setImageResource(R.drawable.ic_repeat_one_black_24dp);
                    mediaPlayer.setLooping(true);
                    repeatoneFlag = true;
                }
                break;
//                if (repeatFlag) {
//                    Toast.makeText(this, "Replaying Removed..", Toast.LENGTH_SHORT).show();
//                    mediaPlayer.setLooping(false);
//                    repeatFlag = false;
//                } else {
//                    Toast.makeText(this, "Replaying Added..", Toast.LENGTH_SHORT).show();
//                    mediaPlayer.setLooping(true);
//                    repeatFlag = true;
//                }
//                break;
            case R.id.img_btn_previous:
                if (checkFlag) {
                    if (mediaPlayer.getCurrentPosition() > 10) {
                        if (currentPosition - 1 > -1) {
                            attachMusic(songList.get(currentPosition - 1).getTitle(), songList.get(currentPosition - 1).getPath());
                            currentPosition = currentPosition - 1;
                        } else {
                            attachMusic(songList.get(currentPosition).getTitle(), songList.get(currentPosition).getPath());
                        }
                    } else {
                        attachMusic(songList.get(currentPosition).getTitle(), songList.get(currentPosition).getPath());
                    }
                } else {
                    Toast.makeText(this, "Select a Song . .", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.img_btn_next:

                if (checkFlag) {
                    if (currentPosition + 1 < songList.size()) {
                        attachMusic(songList.get(currentPosition + 1).getTitle(), songList.get(currentPosition + 1).getPath());
                        currentPosition += 1;
                    } else {
                        Toast.makeText(this, "Playlist Ended", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Select the Song ..", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.img_btn_repeat:
                if (!playContinueFlag) {
                    playContinueFlag = true;
                    imgBtnRepeat.setImageResource(R.drawable.ic_sync_black_full_24dp);
                    Toast.makeText(this, "Loop Added", Toast.LENGTH_SHORT).show();
                } else {
                    playContinueFlag = false;
                    imgBtnRepeat.setImageResource(R.drawable.ic_sync_black_less_24dp);
//                    Toast.makeText(this, "Loop Removed", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private int getRandom(int i) {
        Random random = new Random();
        return random.nextInt(i + 1);
    }

    /**
     * Function to attach the song to the music player
     *
     * @param name
     * @param path
     */

    private void attachMusic(String name, String path) {
        imgBtnPlayPause.setImageResource(R.drawable.ic_play_arrow_black_24dp);
        setTitle(name);
        menu.getItem(1).setIcon(R.drawable.ic_favorite_black_24dp);
        favFlag = true;

        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            setControls();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                imgBtnPlayPause.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                if (playContinueFlag) {
                    if (currentPosition + 1 < songList.size()) {
                        attachMusic(songList.get(currentPosition + 1).getTitle(), songList.get(currentPosition + 1).getPath());
                        currentPosition += 1;
                    } else {

                        Toast.makeText(MainActivity.this, "PlayList Ended", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    /**
     * Function to set the controls according to the song
     */

    private void setControls() {
        seekbarController.setMax(mediaPlayer.getDuration());
        mediaPlayer.start();
        playCycle();
        checkFlag = true;
        if (mediaPlayer.isPlaying()) {
            imgBtnPlayPause.setImageResource(R.drawable.ic_pause_black_24dp);
            tvTotalTime.setText(getTimeFormatted(mediaPlayer.getDuration()));
        }

        seekbarController.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                    tvCurrentTime.setText(getTimeFormatted(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    /**
     * Function to play the song using a thread
     */
    private void playCycle() {
        try {
            seekbarController.setProgress(mediaPlayer.getCurrentPosition());
            tvCurrentTime.setText(getTimeFormatted(mediaPlayer.getCurrentPosition()));
            if (mediaPlayer.isPlaying()) {
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        playCycle();

                    }
                };
                handler.postDelayed(runnable, 100);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getTimeFormatted(long milliSeconds) {
        String finalTimerString = "";
        String secondsString;

        //Converting total duration into time
        int hours = (int) (milliSeconds / 3600000);
        int minutes = (int) (milliSeconds % 3600000) / 60000;
        int seconds = (int) ((milliSeconds % 3600000) % 60000 / 1000);

        // Adding hours if any
        if (hours > 0)
            finalTimerString = hours + ":";

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10)
            secondsString = "0" + seconds;
        else
            secondsString = "" + seconds;

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // Return timer String;
        return finalTimerString;
    }


    /**
     * Function Overrided to receive the data from the fragment
     *
     * @param name
     * @param path
     */

    @Override
    public void onDataPass(String name, String path) {
        Toast.makeText(this, name, Toast.LENGTH_LONG).show();
        attachMusic(name, path);
    }

    @Override
    public void getLength(int length) {
        this.allSongLength = length;
    }

    @Override
    public void fullSongList(ArrayList<SongsList> songList, int position) {
        this.songList = songList;
        this.currentPosition = position;
        this.playlistFlag = songList.size() == allSongLength;
        this.playContinueFlag = !playlistFlag;
    }

    @Override
    public String queryText() {
        return searchText.toLowerCase();
    }

    @Override
    public SongsList getSong() {
        currentPosition = -1;
        return currSong;
    }

    @Override
    public boolean getPlaylistFlag() {
        return playlistFlag;
    }

    @Override
    public void currentSong(SongsList songsList) {
        this.currSong = songsList;
    }

    @Override
    public int getPosition() {
        return currentPosition;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int currentNightMode = newConfig.uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_NO:
                // Night mode is not active, we're using the light theme
                break;
            case Configuration.UI_MODE_NIGHT_YES:
                // Night mode is active, we're using dark theme
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        init();
        grantedPermission();
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        init();
        grantedPermission();
    }
    //    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        mediaPlayer.release();
//        handler.removeCallbacks(runnable);
//    }
}


