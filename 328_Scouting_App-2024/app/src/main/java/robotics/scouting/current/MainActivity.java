package robotics.scouting.current;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.internal.NavigationMenuItemView;
import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;

import robotics.scouting.current.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    ActivityMainBinding binding;
    FloatingActionButton fab;
    FloatingActionButton fab2;
    static String balance = "NA";
    static boolean blueAlliance = false;
    static boolean redAlliance =false;
    static int totalPoints = 0;
    static int totalCount = 0;
    static int autoPoints = 0;
    static int teleOpPoints = 0;
    static int autoHighCount = 0;
    static int autoLowCount = 0;
    static int teleOpHighCount = 0;
    static int teleOpLowCount = 0;
    static int endgamePoints = 0;
    static int missCount = 0;
    static int autoMissCount = 0;
    static int teleOpMissCount = 0;
    static String intakeOrder = "";
    static int match = -1;
    static int team = -1;
    static String autoC = "NA";
    static String teleC = "NA";
    public static int dimen;
    static int mins =0;
    static ArrayList<Integer> scoresAuto;
    static int secs =0;
    static int millis =0;
    static String time="0.00";
    static ArrayList<Integer> grid;
    static ArrayList<Integer> teleGrid;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private static NavigationView navigationView;
    private NavigationMenuItemView night;
    Menu menu;
    static View headerView;
    static TextView TeamText;
    static TextView MatchText;
    String imagesDir;
    static String event;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        fab = findViewById(R.id.fab);
        fab2 = findViewById(R.id.fab2);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + File.separator + "QR";
        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);
        night = drawerLayout.findViewById(R.id.NightMode);
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        if (faqActivityMain.sharedPreferences != null) {
            event = faqActivityMain.sharedPreferences.getString("event","");
        }
        WindowManager manager = (WindowManager) this.getSystemService(WINDOW_SERVICE);
        //initializing a variable for default display.
        Display display = manager.getDefaultDisplay();
        //creating a variable for point which is to be displayed in QR Code.
        Point point = new Point();
        display.getSize(point);
        //getting width and height of a point
        int width = point.x;
        int height = point.y;
        //generating dimension from width and height.
        dimen = Math.min(width, height);
        dimen = dimen * 3 / 4;

        fab.setOnClickListener(this);
        fab2.setOnClickListener(this);
        if (!checkPermission()) {
            requestPermission();
        }
        getSupportActionBar().setTitle("Before Match");
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.before:
                    replaceFragment(new BeforeMatchFragment(),"before");
                    getSupportActionBar().setTitle("Before Match");
                    break;
                case R.id.auto:
                    replaceFragment(new Auto(),"auto");
                    getSupportActionBar().setTitle("Auto");
                    break;
                case R.id.tele:
                    replaceFragment(new TeleOp(),"tele");
                    getSupportActionBar().setTitle("Tele-Operative");
                    break;
                case R.id.end:
                    replaceFragment(new EndGame(),"end");
                    getSupportActionBar().setTitle("Review");
                    break;
                case R.id.dock:
                    replaceFragment(new Docking(),"docking");
                    getSupportActionBar().setTitle("Docking/End Game");
                    break;
            }
            return true;
        });
        headerView = navigationView.inflateHeaderView(R.layout.nav_header_layout);
        navigationView.removeHeaderView(navigationView.getHeaderView(0));
        TeamText = headerView.findViewById(R.id.teamNum);
        MatchText = headerView.findViewById(R.id.matchNum);
        MatchText.setText("Match:\n NA");
        TeamText.setText("Team:\n NA");
        boolean locationGo = getIntent().getBooleanExtra("local",false);
        if (locationGo){
            replaceFragment(new EndGameAlliance(),"end");
            getSupportActionBar().setTitle("Review");
            binding.bottomNavigationView.setSelectedItemId(R.id.end);
        }else {
            replaceFragment(new BeforeMatchFragment(),"before");
        }
    }
    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);
        night = drawerLayout.findViewById(R.id.NightMode);
        if (night != null) {
            if (SplashActivity.isDarkModeOn) {
                night.setTitle("Disable Dark Mode");
            } else {
                night.setTitle("Enable Dark Mode");
            }
        }
        return true;
    }
    @Override
    @SuppressLint("RestrictedApi")
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.newTeam:
                Intent intent = new Intent(MainActivity.this,MainActivity.class);
                startActivity(intent);
                clearData();
                break;
            case R.id.newAlliance:
                Intent intent4 = new Intent(MainActivity.this,AllianceActivity.class);
                startActivity(intent4);
                clearData();
                break;
            case R.id.mainMenu:
                Intent intent3 = new Intent(MainActivity.this,SplashActivity.class);
                startActivity(intent3);
                break;
            case R.id.FileViewer:
                Intent intent1 = new Intent(MainActivity.this,FileListActivity.class);
                intent1.putExtra("path",imagesDir);
                startActivity(intent1);
                break;
            case R.id.share:
                rateApp();
                break;
            case R.id.faq:
                Intent intent2 = new Intent(MainActivity.this,faqActivityMain.class);
                startActivity(intent2);
                break;
            case R.id.NightMode:
                night = drawerLayout.findViewById(R.id.NightMode);
                if (night != null){
                    if (SplashActivity.isDarkModeOn) {
                        night.setTitle("Disable Dark Mode");
                        SplashActivity.editor.putBoolean(
                                "isDarkModeOn", false);
                        SplashActivity.editor.apply();
                        AppCompatDelegate
                                .setDefaultNightMode(
                                        AppCompatDelegate
                                                .MODE_NIGHT_NO);
                        night.setTitle("Disable Dark Mode");

                    }
                    else {
                        night.setTitle("Enable Dark Mode");
                        SplashActivity.editor.putBoolean(
                                "isDarkModeOn", true);
                        SplashActivity.editor.apply();
                        AppCompatDelegate
                                .setDefaultNightMode(
                                        AppCompatDelegate
                                                .MODE_NIGHT_YES);
                        night.setTitle("Enable Dark Mode");
                    }
                }
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }
    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.fab:

                BeforeMatchFragment beforeFrag = (BeforeMatchFragment)getSupportFragmentManager().findFragmentByTag("before");
                EndGame endFrag = (EndGame)getSupportFragmentManager().findFragmentByTag("end");
                Auto autoFrag = (Auto)getSupportFragmentManager().findFragmentByTag("auto");
                TeleOp teleFrag = (TeleOp)getSupportFragmentManager().findFragmentByTag("tele");
                Docking dockFrag = (Docking)getSupportFragmentManager().findFragmentByTag("docking");

                if (beforeFrag != null && beforeFrag.isVisible()) {
                    Intent intent = new Intent(MainActivity.this,SplashActivity.class);
                    startActivity(intent);
                }else if (autoFrag != null && autoFrag.isVisible()) {
                    binding.bottomNavigationView.setSelectedItemId(R.id.before);
                    replaceFragment(new BeforeMatchFragment(), "before");
                }else if (teleFrag != null && teleFrag.isVisible()) {
                    binding.bottomNavigationView.setSelectedItemId(R.id.auto);

                    replaceFragment(new Auto(), "auto");
                }else if (endFrag != null && endFrag.isVisible()) {
                    binding.bottomNavigationView.setSelectedItemId(R.id.dock);
                    replaceFragment(new Docking(), "docking");
                }else if (dockFrag != null && dockFrag.isVisible()) {
                    binding.bottomNavigationView.setSelectedItemId(R.id.tele);
                    replaceFragment(new TeleOp(), "tele");
                }

                break;
            case R.id.fab2:

                 beforeFrag = (BeforeMatchFragment)getSupportFragmentManager().findFragmentByTag("before");
                 autoFrag = (Auto)getSupportFragmentManager().findFragmentByTag("auto");
                 teleFrag = (TeleOp)getSupportFragmentManager().findFragmentByTag("tele");
                 dockFrag = (Docking)getSupportFragmentManager().findFragmentByTag("docking");

                if (beforeFrag != null && beforeFrag.isVisible()) {
                    binding.bottomNavigationView.setSelectedItemId(R.id.auto);
                    replaceFragment(new Auto(), "auto");
                }else if (autoFrag != null && autoFrag.isVisible()) {
                    binding.bottomNavigationView.setSelectedItemId(R.id.tele);
                    replaceFragment(new TeleOp(), "tele");
                }else if (teleFrag != null && teleFrag.isVisible()) {
                    binding.bottomNavigationView.setSelectedItemId(R.id.dock);
                    replaceFragment(new Docking(), "docking");
                }else if (dockFrag != null && dockFrag.isVisible()) {
                    binding.bottomNavigationView.setSelectedItemId(R.id.end);
                    replaceFragment(new EndGame(), "end");
                }

                break;
        }
    }

    private void replaceFragment(Fragment fragment,String title) {
    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in,
                            R.anim.fade_out,
                            R.anim.fade_in,
                            R.anim.slide_out)
            .setReorderingAllowed(true)
            .replace(R.id.frameLayout, fragment,title)
            .addToBackStack(null);
    fragmentTransaction.commit();
        if (title.equals("end")){
            fab2.setVisibility(View.GONE);
        }
        else{
            fab2.setVisibility(View.VISIBLE);
        }
    }
    public boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(result == PackageManager.PERMISSION_GRANTED) {
            return true;
        }else{
            return false;

        }
    }
    private void requestPermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(MainActivity.this,"Storage permission is required, please allow it from settings.",Toast.LENGTH_SHORT).show();
        }else{
            ActivityCompat.requestPermissions(MainActivity.this,new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},111);
        }
    }
    public static void setTeamName(String teamName) {
        TeamText = headerView.findViewById(R.id.teamNum);
        TeamText.setText("Team:\n" + teamName);
    }
    public void rateApp()
    {
        try
        {
            Intent rateIntent = rateIntentForUrl("market://details");
            startActivity(rateIntent);
        }
        catch (ActivityNotFoundException e)
        {
            Intent rateIntent = rateIntentForUrl("https://play.google.com/store/apps/details");
            startActivity(rateIntent);
        }
    }

    private Intent rateIntentForUrl(String url)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("%s?id=%s", url, getPackageName())));
        int flags = Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK;
        if (Build.VERSION.SDK_INT >= 21)
        {
            flags |= Intent.FLAG_ACTIVITY_NEW_DOCUMENT;
        }
        else
        {
            //noinspection deprecation
            flags |= Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET;
        }
        intent.addFlags(flags);
        return intent;
    }
    public static void setMatchNum(String matchNum) {
        MatchText = headerView.findViewById(R.id.matchNum);
        MatchText.setText("Match:\n" + matchNum);
    }
    public static void setBalance(String balance) {
        MainActivity.balance = balance;
    }
    public static void setMatch(int match) {
        MainActivity.match = match;
    }
    public static void setTeam(int team) {
        MainActivity.team = team;
    }
    public static void setAutoC(String autoC) {
        MainActivity.autoC = autoC;
    }
    public static void setTeleC(String teleC) {
        MainActivity.teleC = teleC;
    }
    public static void setGrid( ArrayList<Integer> grid) {
        MainActivity.grid = grid;
    }
    public static void setTeleGrid( ArrayList<Integer> teleGrid) {
        MainActivity.teleGrid = teleGrid;
    }
    public static void setTime(int mins,int secs,int millis) {
        MainActivity.mins = mins;
        MainActivity.secs = secs;
        MainActivity.millis = millis;
        MainActivity.time = String.valueOf(secs) + "." + String.valueOf(millis);
    }

    public static ArrayList<Integer> setAutoScores(int HighCubeCount, int MidCubeCount, int LowCubeCount, int HighConeCount, int MidConeCount, int LowConeCount, int MissCount){
        return null;
    }
    public static int getDimen() {
        return dimen;
    }
    public static int getMatch() { return match; }
    public static int getTotalPoints(){return totalPoints;}
    public static void setTotalPoints(int value) { totalPoints = value;}
    public static int getAutoPoints(){return autoPoints;}
    public static void setAutoPoints(int value) { autoPoints = value;}
    public static int getTeleOpPoints(){return teleOpPoints;}
    public static void setTeleOpPoints(int value) { teleOpPoints = value;}
    public static int getAutoHighCount(){return autoHighCount;}
    public static void setAutoHighCount(int value) {autoHighCount = value;}
    public static int getAutoLowCount(){return autoLowCount;}
    public static void setAutoLowCount(int value) {autoLowCount = value;}
    public static int getTeleOpHighCount(){return teleOpHighCount;}
    public static void setTeleOpHighCount(int value) {teleOpHighCount = value;}
    public static int getTeleOpLowCount(){return teleOpLowCount;}
    public static void setTeleOpLowCount(int value) {teleOpLowCount = value;}
    public static int getTotalCount(){return totalCount;}
    public static void setTotalCount(int value) {totalCount = value;}
    public static void setEndgamePoints(int value) {endgamePoints = value;}
    public static int getMissCount() {return missCount;}
    public static void setMissCount(int value) {missCount = value;}
    public static int getAutoMissCount() {return autoMissCount;}
    public static void setAutoMissCount(int value) {autoMissCount = value;}
    public static int getTeleOpMissCount() {return teleOpMissCount;}
    public static void setTeleOpMissCount(int value) {teleOpMissCount = value;}
    public static int getEndgamePoints() {return endgamePoints;}
    public static void setRedAlliance(boolean value) { redAlliance = value;}
    public static void setBlueAlliance(boolean value) { blueAlliance = value; }
    public static String getIntakeOrder() {return intakeOrder;}
    public static void setIntakeOrder(String value) {intakeOrder = value;}


    public static int getTeam() {
        return team;
    }
    public static boolean getRedAlliance() { return redAlliance;}
    public static boolean getBlueAlliance() {return blueAlliance;}

    public static String getAutoC() {
        return autoC;
    }
    public static String getTeleC() {
        return teleC;
    }

    public static String getBalance() {
        return balance;
    }
    public static ArrayList<Integer> getGrid() {
        return grid;
    }
    public static ArrayList<Integer> getTeleGrid() {
        return teleGrid;
    }
    public static int getTimeMin() {
        return mins;
    }
    public static int getTimeSec() {
        return secs;
    }
    public static int getTimeMillis() {
        return millis;
    }
    public static String getAllData(){
        if (getBlueAlliance() == true) {
            double percentageMissed = 100.0 * missCount / totalCount;
            double autoPercentageMissed = 100.0 * autoMissCount / (autoLowCount + autoHighCount);
            double teleOpPercentageMissed = 100.0 * teleOpMissCount / (teleOpLowCount + teleOpHighCount);
            String roundedPercentageMissed = String.format("%.2f", percentageMissed);
            String roundedAutoPercentageMissed = String.format("%.2f", autoPercentageMissed);
            String roundedTeleOpPercentageMissed = String.format("%.2f", teleOpPercentageMissed);
            return "Event: " + event + "\n" + "Match Number: " + String.valueOf(match) + "\n" + "Team color: Blue" + "\n" + "Team Number: " + String.valueOf(team) + "\n" + "Total Points: " + String.valueOf(totalPoints) + "\n" + "Auto Points: " + String.valueOf(autoPoints) + "\n" + "Tele Op Points: " + String.valueOf(teleOpPoints) + "\n" + "Endgame Points: " + String.valueOf(endgamePoints) + "\n" + "Total Shots Made: " + String.valueOf(totalCount) + "\n" + "Shots Made in Auto: " + String.valueOf(autoHighCount+autoLowCount) + "\n" + "Shots Made in TeleOp: " + String.valueOf(teleOpHighCount + teleOpLowCount) + "\n" + "Miss Count: " + String.valueOf(missCount) + "\n" + "Shots Missed in Auto: " + String.valueOf(autoMissCount) + "\n" + "Shots Missed in TeleOp: " + String.valueOf(teleOpMissCount) + "\n" + "% of Shots Missed: " + roundedPercentageMissed + "\n" + "% of Shots Missed in Auto: " + roundedAutoPercentageMissed + "\n" + "% of Shots Missed in TeleOp: " + roundedTeleOpPercentageMissed + "\n" + "Intake Order: " + intakeOrder;
        } else if (getRedAlliance() == true){
            double percentageMissed = 100.0 * missCount / totalCount;
            double autoPercentageMissed = 100.0 * autoMissCount / (autoLowCount + autoHighCount);
            double teleOpPercentageMissed = 100.0 * teleOpMissCount / (teleOpLowCount + teleOpHighCount);
            String roundedPercentageMissed = String.format("%.2f", percentageMissed);
            String roundedAutoPercentageMissed = String.format("%.2f", autoPercentageMissed);
            String roundedTeleOpPercentageMissed = String.format("%.2f", teleOpPercentageMissed);
            return "Event: "+ event + "\n" + "Match Number: " + String.valueOf(match) + "\n" + "Team color: Red" + "\n" + "Team Number: " + String.valueOf(team) +  "\n" + "Total Points: " + String.valueOf(totalPoints) + "\n" + "Auto Points: " + String.valueOf(autoPoints) + "\n" + "Tele Op Points: " + String.valueOf(teleOpPoints) + "\n" + "Endgame Points: " + String.valueOf(endgamePoints) + "\n" + "Total Shots Made: " + String.valueOf(totalCount) + "\n" + "Shots Made in Auto: " + String.valueOf(autoHighCount+autoLowCount) + "\n" + "Shots Made in TeleOp: " + String.valueOf(teleOpHighCount + teleOpLowCount) + "\n" + "Miss Count: " + String.valueOf(missCount) + "\n" + "Shots Missed in Auto: " + String.valueOf(autoMissCount) + "\n" + "Shots Missed in TeleOp: " + String.valueOf(teleOpMissCount) + "\n" + "% of Shots Missed: " + roundedPercentageMissed + "\n" + "% of Shots Missed in Auto: " + roundedAutoPercentageMissed + "\n" + "% of Shots Missed in TeleOp: " + roundedTeleOpPercentageMissed + "\n" + "Intake Order: " + intakeOrder;
        } else  {
            double percentageMissed = 100.0 * missCount / totalCount;
            double autoPercentageMissed = 100.0 * autoMissCount / (autoLowCount + autoHighCount);
            double teleOpPercentageMissed = 100.0 * teleOpMissCount / (teleOpLowCount + teleOpHighCount);
            String roundedPercentageMissed = String.format("%.2f", percentageMissed);
            String roundedAutoPercentageMissed = String.format("%.2f", autoPercentageMissed);
            String roundedTeleOpPercentageMissed = String.format("%.2f", teleOpPercentageMissed);
            return "Event: "+ event + "\n" + "Match Number: " + String.valueOf(match) + "\n" + "Team color: N/A" + "\n"  + "Team Number: " + String.valueOf(team) + "\n" + "Total Points: " + String.valueOf(totalPoints) + "\n" + "Auto Points: " + String.valueOf(autoPoints) + "\n" + "Tele Op Points: " + String.valueOf(teleOpPoints) + "\n" + "Endgame Points: " + String.valueOf(endgamePoints) + "\n" + "Total Shots Made: " + String.valueOf(totalCount) + "\n" + "Shots Made in Auto: " + String.valueOf(autoHighCount+autoLowCount) + "\n" + "Shots Made in TeleOp: " + String.valueOf(teleOpHighCount + teleOpLowCount) + "\n" + "Miss Count: " + String.valueOf(missCount) + "\n" + "Shots Missed in Auto: " + String.valueOf(autoMissCount) + "\n" + "Shots Missed in TeleOp: " + String.valueOf(teleOpMissCount) + "\n" + "% of Shots Missed: " + roundedPercentageMissed + "\n" + "% of Shots Missed in Auto: " + roundedAutoPercentageMissed + "\n" + "% of Shots Missed in TeleOp: " + roundedTeleOpPercentageMissed + "\n" + "Intake Order: " + intakeOrder;
        }
    }
    public static String getAllDataChangeable(){
        if (getBlueAlliance() == true) {
            double percentageMissed = 100.0 * missCount / totalCount;
            double autoPercentageMissed = 100.0 * autoMissCount / (autoLowCount + autoHighCount);
            double teleOpPercentageMissed = 100.0 * teleOpMissCount / (teleOpLowCount + teleOpHighCount);
            String roundedPercentageMissed = String.format("%.2f", percentageMissed);
            String roundedAutoPercentageMissed = String.format("%.2f", autoPercentageMissed);
            String roundedTeleOpPercentageMissed = String.format("%.2f", teleOpPercentageMissed);
            return "Event: " + event + "\n" + "Match Number: " + String.valueOf(match) + "\n" + "Team color: Blue" + "\n" + "Team Number: " + String.valueOf(team) + "\n" + "Total Points: " + String.valueOf(totalPoints) + "\n" + "Auto Points: " + String.valueOf(autoPoints) + "\n" + "Tele Op Points: " + String.valueOf(teleOpPoints) + "\n" + "Endgame Points: " + String.valueOf(endgamePoints) + "\n" + "Total Shots Made: " + String.valueOf(totalCount) + "\n" + "Shots Made in Auto: " + String.valueOf(autoHighCount+autoLowCount) + "\n" + "Shots Made in TeleOp: " + String.valueOf(teleOpHighCount + teleOpLowCount) + "\n" + "Miss Count: " + String.valueOf(missCount) + "\n" + "Shots Missed in Auto: " + String.valueOf(autoMissCount) + "\n" + "Shots Missed in TeleOp: " + String.valueOf(teleOpMissCount) + "\n" + "% of Shots Missed: " + roundedPercentageMissed + "\n" + "% of Shots Missed in Auto: " + roundedAutoPercentageMissed + "\n" + "% of Shots Missed in TeleOp: " + roundedTeleOpPercentageMissed + "\n" + "Intake Order: " + intakeOrder;
        } else if (getRedAlliance() == true){
            double percentageMissed = 100.0 * missCount / totalCount;
            double autoPercentageMissed = 100.0 * autoMissCount / (autoLowCount + autoHighCount);
            double teleOpPercentageMissed = 100.0 * teleOpMissCount / (teleOpLowCount + teleOpHighCount);
            String roundedPercentageMissed = String.format("%.2f", percentageMissed);
            String roundedAutoPercentageMissed = String.format("%.2f", autoPercentageMissed);
            String roundedTeleOpPercentageMissed = String.format("%.2f", teleOpPercentageMissed);
            return "Event: "+ event + "\n" + "Match Number: " + String.valueOf(match) + "\n" + "Team color: Red" + "\n" + "Team Number: " + String.valueOf(team) +  "\n" + "Total Points: " + String.valueOf(totalPoints) + "\n" + "Auto Points: " + String.valueOf(autoPoints) + "\n" + "Tele Op Points: " + String.valueOf(teleOpPoints) + "\n" + "Endgame Points: " + String.valueOf(endgamePoints) + "\n" + "Total Shots Made: " + String.valueOf(totalCount) + "\n" + "Shots Made in Auto: " + String.valueOf(autoHighCount+autoLowCount) + "\n" + "Shots Made in TeleOp: " + String.valueOf(teleOpHighCount + teleOpLowCount) + "\n" + "Miss Count: " + String.valueOf(missCount) + "\n" + "Shots Missed in Auto: " + String.valueOf(autoMissCount) + "\n" + "Shots Missed in TeleOp: " + String.valueOf(teleOpMissCount) + "\n" + "% of Shots Missed: " + roundedPercentageMissed + "\n" + "% of Shots Missed in Auto: " + roundedAutoPercentageMissed + "\n" + "% of Shots Missed in TeleOp: " + roundedTeleOpPercentageMissed + "\n" + "Intake Order: " + intakeOrder;
        } else  {
            double percentageMissed = 100.0 * missCount / totalCount;
            double autoPercentageMissed = 100.0 * autoMissCount / (autoLowCount + autoHighCount);
            double teleOpPercentageMissed = 100.0 * teleOpMissCount / (teleOpLowCount + teleOpHighCount);
            String roundedPercentageMissed = String.format("%.2f", percentageMissed);
            String roundedAutoPercentageMissed = String.format("%.2f", autoPercentageMissed);
            String roundedTeleOpPercentageMissed = String.format("%.2f", teleOpPercentageMissed);
            return "Event: "+ event + "\n" + "Match Number: " + String.valueOf(match) + "\n" + "Team color: N/A" + "\n"  + "Team Number: " + String.valueOf(team) + "\n" + "Total Points: " + String.valueOf(totalPoints) + "\n" + "Auto Points: " + String.valueOf(autoPoints) + "\n" + "Tele Op Points: " + String.valueOf(teleOpPoints) + "\n" + "Endgame Points: " + String.valueOf(endgamePoints) + "\n" + "Total Shots Made: " + String.valueOf(totalCount) + "\n" + "Shots Made in Auto: " + String.valueOf(autoHighCount+autoLowCount) + "\n" + "Shots Made in TeleOp: " + String.valueOf(teleOpHighCount + teleOpLowCount) + "\n" + "Miss Count: " + String.valueOf(missCount) + "\n" + "Shots Missed in Auto: " + String.valueOf(autoMissCount) + "\n" + "Shots Missed in TeleOp: " + String.valueOf(teleOpMissCount) + "\n" +"% of Shots Missed: " + roundedPercentageMissed + "\n" + "% of Shots Missed in Auto: " + roundedAutoPercentageMissed + "\n" + "% of Shots Missed in TeleOp: " + roundedTeleOpPercentageMissed + "\n" + "Intake Order: " + intakeOrder;
        }

        //return "Event: "+ event + "\n" + "Match Number: " + String.valueOf(match) + "\n" + "Team Number: " + String.valueOf(team) + "\n" + "Comment on Auto: " + autoC + "\n" + "Auto Grid Positions: " + grid.toString() + "\n" + "Comment on Tele: " + teleC + "\n" + "Tele Grid Positions: " + teleGrid.toString() + "\n" + "Balance Position: " + balance+"\n"+ "Time to Balance: " + time;
    }
    public static void clearData(){
        match = -1;
        team = -1;
        autoC = "NA";
        autoHighCount = 0;
        autoLowCount = 0;
        autoPoints = 0;
        teleOpHighCount = 0;
        teleOpLowCount = 0;
        teleOpPoints = 0;
        totalPoints = 0;
        endgamePoints = 0;
        redAlliance = false;
        blueAlliance = false;
        missCount = 0;
        balance = "NA";
        millis = 0;
        secs = 0;
        mins = 0;
        time = "0.00";
    }



}