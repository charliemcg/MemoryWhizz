package com.violenthoboenterprises.memorywhizz;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Random;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity {

    //Highest scores are saved in this array.
    public int[] highScoreArray;

    //Updates every time a new high score is achieved.
    public static int[] highestScore = {1};

    //The game starts with no high scores. There can only be a certain number of high scores. This
    //flag is true when the high score array is full.
    boolean isArrayFull = false;

    //Count is incremented each time a new high score is set. Count is used to determine if
    //array is full or not.
    int numOfHighScores = 0;

    //Flag for turning music on or off.
    static boolean playMusic = true;

    //App features double tap to exit. Flag is used to distinguish between first and second tap.
    boolean exitGame;

    //Flag used for returning to the main splash screen.
    boolean splashReturn = false;

    //Player's current score.
    int points = 0;

    //Multiplier for enhancing score.
    int multiplier = 1;

    //Is incremented with every game play. Interstitial shows when certain values are reached.
    int showInterstitial = 0;

    //High scores screen can only be accessed when at least one high score has been set.
    boolean[] highScoresEnabled = {true};

    MediaPlayer backgroundMusic;

    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        mSharedPreferences = getPreferences(MODE_PRIVATE);

        highScoreArray = new int[6];

        //All the high scores are saved in a manner so that they don't vanish when app closed.
        highScoreArray[0] = mSharedPreferences.getInt("myPrefsKey0", 0);
        highScoreArray[1] = mSharedPreferences.getInt("myPrefsKey1", 0);
        highScoreArray[2] = mSharedPreferences.getInt("myPrefsKey2", 0);
        highScoreArray[3] = mSharedPreferences.getInt("myPrefsKey3", 0);
        highScoreArray[4] = mSharedPreferences.getInt("myPrefsKey4", 0);
        highScoreArray[5] = mSharedPreferences.getInt("myPrefsKey5", 0);

        //Initialising strings.
        final String musicOn = getResources().getString(R.string.music_on);
        final String musicOff = getResources().getString(R.string.music_off);
        String playString = getResources().getString(R.string.play);
        String highScoresString = getResources().getString(R.string.high_scores);

        //Initializing splash screen buttons.
        final Button splash = (Button) findViewById(R.id.splash);
        final Button splashPlay = (Button) findViewById(R.id.splashPlay);
        final Button splashHighScores = (Button) findViewById(R.id.splashHighScores);
        final Button splashMusic = (Button) findViewById(R.id.splashMusic);

        //Assigning strings to buttons.
        splashPlay.setText(playString);
        splashHighScores.setText(highScoresString);

        //Initializing array of button sounds.
        final MediaPlayer[] buttonClick = new MediaPlayer[9];

        buttonClick[8] = MediaPlayer.create(this, R.raw.btn1);
        buttonClick[7] = MediaPlayer.create(this, R.raw.btn2);
        buttonClick[6] = MediaPlayer.create(this, R.raw.btn3);
        buttonClick[5] = MediaPlayer.create(this, R.raw.btn4);
        buttonClick[4] = MediaPlayer.create(this, R.raw.btn5);
        buttonClick[3] = MediaPlayer.create(this, R.raw.btn6);
        buttonClick[2] = MediaPlayer.create(this, R.raw.btn7);
        buttonClick[1] = MediaPlayer.create(this, R.raw.btn8);
        buttonClick[0] = MediaPlayer.create(this, R.raw.btn9);

        //Initializing fail sound.
        final MediaPlayer fail = MediaPlayer.create(this, R.raw.fail);

        //High score sound.
        final MediaPlayer highScoreJingle = MediaPlayer.create(this, R.raw.highscorejingle);
        highScoreJingle.setVolume(0.5f, 0.5f);

        //Initialising background music.
        backgroundMusic = MediaPlayer.create(this, R.raw.memorygametheme);
        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(0.3f, 0.3f);
        backgroundMusic.start();

        //Initializing vibrator.
        final Vibrator vibrate = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);

        //Each button is declared and stored in an array.
        final Button buttonA = (Button) this.findViewById(R.id.buttonA);
        final Button buttonB = (Button) this.findViewById(R.id.buttonB);
        final Button buttonC = (Button) this.findViewById(R.id.buttonC);
        final Button buttonD = (Button) this.findViewById(R.id.buttonD);
        final Button buttonE = (Button) this.findViewById(R.id.buttonE);
        final Button buttonF = (Button) this.findViewById(R.id.buttonF);
        final Button buttonG = (Button) this.findViewById(R.id.buttonG);
        final Button buttonH = (Button) this.findViewById(R.id.buttonH);
        final Button buttonI = (Button) this.findViewById(R.id.buttonI);

        final Button[] btn = new Button[]{buttonA, buttonB, buttonC, buttonD, buttonE,
                buttonF, buttonG, buttonH, buttonI};

        //Initializing interstitial ad.
        final InterstitialAd interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getResources().getString(R.string.interstitial_ad_unit_id));
        final AdRequest intRequest = new AdRequest.Builder().addTestDevice
                (AdRequest.DEVICE_ID_EMULATOR).build();

        //Initializing banner ad.
        final AdView bannerAd = (AdView)findViewById(R.id.adView);
        final AdRequest banRequest = new AdRequest.Builder().addTestDevice
                (AdRequest.DEVICE_ID_EMULATOR).build();
        bannerAd.loadAd(banRequest);

        //Goes to high scores when "High Scores" clicked.
        splashHighScores.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                highScores(splashPlay, splashHighScores, splashMusic, vibrate);
            }
        });

        //Turn music on or off.
        splashMusic.setText(musicOn);
        splashMusic.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                musicOnOff(splashMusic, musicOff, musicOn, backgroundMusic);
            }
        });

        //Clicking 'play' starts the sequence.
        splashPlay.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                play(splashPlay, splashHighScores, splashMusic, splash, btn, buttonClick,
                        fail, vibrate, bannerAd, interstitialAd, intRequest, highScoreJingle);
            }
        });
    }

    @Override
    protected void onPause(){
        //Music stops when app is exited.
        backgroundMusic.pause();
        super.onPause();
        //High scores are saved when app exited.
        mSharedPreferences.edit().putInt("myPrefsKey0", highScoreArray[0]).apply();
        mSharedPreferences.edit().putInt("myPrefsKey1", highScoreArray[1]).apply();
        mSharedPreferences.edit().putInt("myPrefsKey2", highScoreArray[2]).apply();
        mSharedPreferences.edit().putInt("myPrefsKey3", highScoreArray[3]).apply();
        mSharedPreferences.edit().putInt("myPrefsKey4", highScoreArray[4]).apply();
        mSharedPreferences.edit().putInt("myPrefsKey5", highScoreArray[5]).apply();
    }

    @Override
    protected void onResume(){
        //Music resumes when app is reopened.
        backgroundMusic.start();
        super.onResume();
    }

    //Removes splash screen and prepares the game screen.
    private void play(Button splashPlay, Button splashHighScores, Button splashMusic,
                      Button splash, Button[] btn, MediaPlayer[] buttonClick, MediaPlayer fail,
                      Vibrator vibrate, AdView bannerAd, InterstitialAd interstitialAd,
                      AdRequest intRequest, MediaPlayer highScoreJingle) {

        exitGame = false;

        splashPlay.setVisibility(GONE);
        splashHighScores.setVisibility(GONE);
        splashMusic.setVisibility(GONE);
        splash.setVisibility(GONE);
        sequence(btn, buttonClick, fail, vibrate, bannerAd, interstitialAd,
                intRequest, highScoreJingle);

    }

    //Clicking the music button turns the music on or off.
    private void musicOnOff(Button splashMusic, String musicOff, String musicOn,
                            MediaPlayer backgroundMusic) {

        //Volume is set to zero when button is clicked. Music is still playing but can't be heard.
        if(playMusic){
            splashMusic.setText(musicOff);
            backgroundMusic.setVolume(0.0f, 0.0f);
            playMusic = false;
        }
        else{
            splashMusic.setText(musicOn);
            backgroundMusic.setVolume(0.3f, 0.3f);
            playMusic = true;
        }

    }

    //Accesses high scores.
    private void highScores(Button splashPlay, Button splashHighScores, Button splashMusic, Vibrator vibrate) {

        //Initializing high score TextViews
        final TextView highScoreTextViewA = (TextView) findViewById(R.id.highScoreTextViewA);
        final TextView highScoreTextViewB = (TextView) findViewById(R.id.highScoreTextViewB);
        final TextView highScoreTextViewC = (TextView) findViewById(R.id.highScoreTextViewC);
        final TextView highScoreTextViewD = (TextView) findViewById(R.id.highScoreTextViewD);
        final TextView highScoreTextViewE = (TextView) findViewById(R.id.highScoreTextViewE);

//        final int tempA = MainActivity.getHighScoreA();
//        final int tempB = MainActivity.getHighScoreB();
//        final int tempC = MainActivity.getHighScoreC();
//        final int tempD = MainActivity.getHighScoreD();
//        final int tempE = MainActivity.getHighScoreE();
        final int tempA = highScoreArray[5];
        final int tempB = highScoreArray[4];
        final int tempC = highScoreArray[3];
        final int tempD = highScoreArray[2];
        final int tempE = highScoreArray[1];

        //TextViews are updated to the latest high scores.
        //High score TextViews only show up once player actually sets one.
        //If high scores can't be accessed the player is informed that there are no high scores.
        if(tempA == 0 && tempB == 0 && tempC == 0 && tempD == 0 && tempE == 0){
            vibrate.vibrate(100);
            Toast toast = Toast.makeText(MainActivity.this, "    There are no high scores yet    ",
                    Toast.LENGTH_SHORT);
            View toastView = toast.getView();
            TextView toastMessage = (TextView) toastView.findViewById(android.R.id.message);

            //Toast customization
            toastMessage.setTextColor(Color.BLACK);
            toastView.setBackgroundColor(Color.CYAN);
            toast.show();

            highScoresEnabled[0] = false;
        }

        //If player is viewing high scores then the double tap to exit feature is disabled. Back
        //button is instead used to return to the main splash screen.
        if(highScoresEnabled[0]){
            exitGame = false;
        }

        //High scores in array are converted to strings and assigned to textviews.
        if (tempA != 0){
            String tempAString = Integer.toString(tempA);
            highScoreTextViewA.setText(tempAString);
            highScoresEnabled[0] = true;
        }else{
            highScoreTextViewA.setText(" ");
        }
        if (tempB != 0){
            String tempBString = Integer.toString(tempB);
            highScoreTextViewB.setText(tempBString);
        }else{
            highScoreTextViewB.setText(" ");
        }
        if (tempC != 0){
            String tempCString = Integer.toString(tempC);
            highScoreTextViewC.setText(tempCString);
        }else{
            highScoreTextViewC.setText(" ");
        }
        if (tempD != 0){
            String tempDString = Integer.toString(tempD);
            highScoreTextViewD.setText(tempDString);
        }else{
            highScoreTextViewD.setText(" ");
        }
        if (tempE != 0){
            String tempEString = Integer.toString(tempE);
            highScoreTextViewE.setText(tempEString);
        }else{
            highScoreTextViewE.setText(" ");
        }

        //Removing main splash screen and setting up high scores screen.
        if(highScoresEnabled[0]){
            splashReturn = true;
            splashPlay.setVisibility(GONE);
            splashHighScores.setVisibility(GONE);
            splashMusic.setVisibility(GONE);
            highScoreTextViewA.setVisibility(VISIBLE);
            highScoreTextViewB.setVisibility(VISIBLE);
            highScoreTextViewC.setVisibility(VISIBLE);
            highScoreTextViewD.setVisibility(VISIBLE);
            highScoreTextViewE.setVisibility(VISIBLE);
        }

    }

    //When clicked, button turns white for a jiffy.
    public void clickAnimate(final Button btn){

        btn.setBackgroundColor(Color.WHITE);

        new Handler().postDelayed(new Runnable(){
            public void run(){
                btn.setBackgroundColor(Color.CYAN);
            }
        }, 50);
    }

    //The animation of the sequence of buttons that the player must remember.
    public void sequence(final Button[] btn, final MediaPlayer[] click, final MediaPlayer fail,
                         final Vibrator vibrate, final AdView bannerAd,
                         final InterstitialAd interstitialAd, final AdRequest intRequest,
                         final MediaPlayer highScoreJingle){

        //Banner ad is disabled during game play.
        bannerAd.setVisibility(GONE);

        //New interstitial ad is loaded.
        interstitialAd.loadAd(intRequest);

        //"New High Score" message is only shown once per game. It wouldn't be necessary to inform
        //the player of their new high score for every point above the previous high score. Flag
        // //determines whether or not to show message.
        final boolean[] showHighScoreMessageOnce = {true};

        //An array is filled with random buttons. The order of these buttons will be the sequence
        //the player must try to remember.
        //There are 1000 buttons in the sequence which is practically unreachable.
        final Button[] btnSeq = new Button[1000];
        for(int l = 0; l < 1000; l++){
            Random random = new Random();
            int n = random.nextInt(9);
            btnSeq[l] = btn[n];
//            Uncomment print statement to see the randomly generated sequence.
//            System.out.println(n);
        }

        //Sequence is played by lighting up one button at a time.
        final int[] i = {0};
        final Handler handler = new Handler();
        handler.post(new Runnable(){
            @Override
            public void run(){
                seqAnimate(btnSeq[i[0]], btn, click);
            }
        });

        gamePlay(btn, btnSeq, showHighScoreMessageOnce, highScoreJingle, vibrate, i,
                handler, click, fail, bannerAd, interstitialAd);

    }

    //The main game mechanics.
    private void gamePlay(final Button[] btn, final Button[] btnSeq,
                          final boolean[] showHighScoreMessageOnce,
                          final MediaPlayer highScoreJingle, final Vibrator vibrate, final int i[],
                          final Handler handler, final MediaPlayer[] click, final MediaPlayer fail,
                          final AdView bannerAd, final InterstitialAd interstitialAd) {


        final String buttons = getResources().getString(R.string.buttons);

        //Initializing TextView to display the buttons remembered.
        TextView score = (TextView) findViewById(R.id.score);
        String zeroOfOne = getResources().getString(R.string.zero_of_one);
        score.setText(buttons + " " + zeroOfOne);

        //Initializing TextView to display points
        TextView pointsEarned = (TextView) findViewById(R.id.points);
        String scoreOfZero = getResources().getString(R.string.score_of_zero);
        pointsEarned .setText(scoreOfZero);

        //Index number for sequence buttons.
        final int[] seqNum = {0};

        //Number of buttons which are so far available in the sequence.
        final int[] k = {1};

        //Initializing string.
        final String zero = getResources().getString(R.string.zero);

        //Iterates over the buttons to find which one was clicked.
        for (int j = 0; j < 9; j++){
            highestScore[0] = highScoreArray[5];
            final int finalJ = j;
            final TextView finalScore = score;
            final TextView finalPointsEarned = pointsEarned;
            btn[finalJ].setOnClickListener(new View.OnClickListener(){
                public void onClick(View v) {

                    //Whichever button was clicked turns white for a jiffy.
                    clickAnimate(btn[finalJ]);

                    //Checks if correct button was selected.
                    if (btnSeq[seqNum[0]] == v) {

                        //Player is awarded 9 points for every correct button selected.
                        points = points + (9 * multiplier);

                        //Checks if new high score is achieved.
                        if (points > highestScore[0]) {

                            //Records new high score.
                            highestScore[0] = points;

                            //Informs of high score.
                            if (showHighScoreMessageOnce[0]) {
                                highScoreJingle.start();
                                vibrate.vibrate(100);
                                Toast toast = Toast.makeText(MainActivity.this,
                                        "    New High Score!    ", Toast.LENGTH_SHORT);
                                View toastView = toast.getView();
                                TextView toastMessage = (TextView)
                                        toastView.findViewById(android.R.id.message);

                                //Customized toast.
                                toastMessage.setTextColor(Color.BLACK);
                                toastView.setBackgroundColor(Color.CYAN);
                                toast.show();

                                //Only informs of high score once per game.
                                showHighScoreMessageOnce[0] = false;
                            }
                        }
                        seqNum[0]++;

                        //Updates the buttons remembered as player progresses.
                        finalScore.setText(buttons + " " + seqNum[0] + "/" + k[0]);

                        //Updates points earned as player progresses.
                        String partialScore = getResources().getString(R.string.partial_score);
                        finalPointsEarned.setText(partialScore + " " + points);

                        //Code block entered once player has successfully selected all of the
                        // buttons in the sequence.
                        if (k[0] == seqNum[0]) {

                            //Player is awarded an additional 39 points for completing
                            // an entire sequence.
                            points = points + (39 * multiplier);
                            i[0] = 0;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {

                                    //Button sequence is replayed with a new button
                                    // appended to the end.
                                    seqAnimate(btnSeq[i[0]], btn, click);
                                    i[0]++;

                                    if (i[0] < k[0]) {

                                        //The index number for sequence buttons is reset. The
                                        // player must now start the sequence from the
                                        // beginning again.
                                        seqNum[0] = 0;
                                        handler.postDelayed(this, 500);
                                    }
                                }
                            });

                            //Multiplier is incremented every time the player memorises
                            //an entire sequence.
                            multiplier++;

                            //k[0] is incremented to allow the player to
                            //select one button more than previous.
                            k[0]++;

                            //Buttons correctly selected are reset to zero out of
                            //the total amount available.
                            String scoreReset = zero + k[0];
                            finalScore.setText(buttons + " " + scoreReset);
                            finalPointsEarned.setText(partialScore + " " + points);
                        }
                    }

                    //When incorrect button is selected the game is reset.
                    else{

                        //Fail sound plays.
                        fail.start();
                        vibrate.vibrate(100);

                        //Banner ad is enabled.
                        bannerAd.setVisibility(VISIBLE);

                        //Customized toast informs player of how many buttons they could remember.
                        if (k[0] != 2) {
                            Toast toast = Toast.makeText(MainActivity.this, "    You remembered "
                                    + (k[0] - 1) + " buttons    ", Toast.LENGTH_SHORT);
                            View toastView = toast.getView();
                            TextView toastMessage = (TextView)
                                    toastView.findViewById(android.R.id.message);
                            toastMessage.setTextColor(Color.BLACK);
                            toastView.setBackgroundColor(Color.CYAN);
                            toast.show();

                            //Plural is removed if highest number of buttons remembered was one.
                        } else {
                            Toast toast = Toast.makeText(MainActivity.this,
                                    "    You remembered " + (k[0] - 1) +
                                            " button    ", Toast.LENGTH_SHORT);
                            View toastView = toast.getView();
                            TextView toastMessage = (TextView)
                                    toastView.findViewById(android.R.id.message);
                            toastMessage.setTextColor(Color.BLACK);
                            toastView.setBackgroundColor(Color.CYAN);
                            toast.show();
                        }

                        //Buttons are not clickable until a new game is started.
                        for (Button aBtn : btn) {
                            aBtn.setEnabled(false);
                        }

                        //Number of high scores is incremented if a new high score is achieved.
                        for (int j = 0; j < 6; j++) {
                            if (highScoreArray[j] != 0) {
                                numOfHighScores++;
                            }
                        }

                        //Determines if the high score array is full.
                        if (numOfHighScores >= 5) {
                            isArrayFull = true;
                        }

                        //Determines if score should be added to the high scores array.
                        for (int i = 0; i < highScoreArray.length; i++) {

                            //Every score is added to array until array is full.
                            if (!isArrayFull) {
                                if (highScoreArray[i] == 0) {
                                    highScoreArray[i] = points;
                                    break;
                                }
                            }

                            //Only adds score to array if higher than any array value.
                            if (isArrayFull) {
                                if (highScoreArray[i] < highestScore[0] - 1) {
                                    highScoreArray[i] = points;
                                    break;
                                }
                            }
                        }

                        //Sorts array numerically.
                        Arrays.sort(highScoreArray);

//                        Uncomment to see high scores in console.
//                        for (int i = 0; i <= 5; i++) {
//                            System.out.println("Value: " + highScoreArray[i] +
//                              " and index: " + i);
//                        }

                        //Main splash screen is enabled.
                        Button splash = (Button) findViewById(R.id.splash);
                        splash.setVisibility(VISIBLE);

                        Button splashPlay = (Button) findViewById(R.id.splashPlay);
                        splashPlay.setVisibility(VISIBLE);

                        Button splashHighScores = (Button) findViewById(R.id.splashHighScores);
                        splashHighScores.setVisibility(VISIBLE);

                        Button splashMusic = (Button) findViewById(R.id.splashMusic);
                        splashMusic.setVisibility(VISIBLE);

                        //Resets the in-game score counters.
                        finalScore.setText("");
                        finalPointsEarned.setText("");

                        //In case back button was tapped during game play the double
                        //tap to exit feature is reset.
                        exitGame = false;

                        //Points are reset to zero.
                        points = 0;

                        //Multiplier is reset to one.
                        multiplier = 1;

                        //Interstitial is displayed after the 1st, 5th and 20th game.
                        if(interstitialAd.isLoaded() && (showInterstitial == 0 ||
                                showInterstitial == 6 || showInterstitial == 20)){
                            interstitialAd.show();
                        }

                        //Incremented after every game to determine when interstitials
                        //should be shown.
                        showInterstitial++;

                    }
                }
            });
        }
    }

    //Turns each button in sequence white for 400 milliseconds with click sound. Doing this to
    //each button in the sequence one after the other creates then sequence animation.
    private void seqAnimate(final Button btnSeq, final Button btn[], final MediaPlayer[] click){

        //Buttons are disabled while the sequence plays.
        for (Button aBtn : btn) {
            aBtn.setEnabled(false);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                btnSeq.setBackgroundColor(Color.WHITE);
                if(btnSeq.equals(btn[0])){
                    click[0].start();
                }else if(btnSeq.equals(btn[1])){
                    click[1].start();
                }else if(btnSeq.equals(btn[2])){
                    click[2].start();
                }else if(btnSeq.equals(btn[3])){
                    click[3].start();
                }else if(btnSeq.equals(btn[4])){
                    click[4].start();
                }else if(btnSeq.equals(btn[5])){
                    click[5].start();
                }else if(btnSeq.equals(btn[6])){
                    click[6].start();
                }else if(btnSeq.equals(btn[7])){
                    click[7].start();
                }else if(btnSeq.equals(btn[8])){
                    click[8].start();
                }

                new Handler().postDelayed(new Runnable(){
                    public void run(){
                        btnSeq.setBackgroundColor(Color.CYAN);
                    }
                }, 400);

                //Buttons are clickable again.
                for (Button aBtn : btn) {
                    aBtn.setEnabled(true);
                }

            }
        }, 500);

    }

    //Pressing back in high scores returns the player to the main menu. Pressing back anywhere
    //else invokes the double tap to exit feature.
    @Override
    public void onBackPressed(){

        //Returns player to main menu.
        if(splashReturn){

            Button splashPlay = (Button) findViewById(R.id.splashPlay);
            splashPlay.setVisibility(VISIBLE);

            Button splashHighScores = (Button) findViewById(R.id.splashHighScores);
            splashHighScores.setVisibility(VISIBLE);

            Button splashMusic = (Button) findViewById(R.id.splashMusic);
            splashMusic.setVisibility(VISIBLE);

            TextView highScoreTextViewA = (TextView) findViewById(R.id.highScoreTextViewA);
            highScoreTextViewA.setVisibility(GONE);

            TextView highScoreTextViewB = (TextView) findViewById(R.id.highScoreTextViewB);
            highScoreTextViewB.setVisibility(GONE);

            TextView highScoreTextViewC = (TextView) findViewById(R.id.highScoreTextViewC);
            highScoreTextViewC.setVisibility(GONE);

            TextView highScoreTextViewD = (TextView) findViewById(R.id.highScoreTextViewD);
            highScoreTextViewD.setVisibility(GONE);

            TextView highScoreTextViewE = (TextView) findViewById(R.id.highScoreTextViewE);
            highScoreTextViewE.setVisibility(GONE);

            splashReturn = false;

            //Informs player to press back a second time to exit.
        } else if (!exitGame){
            Toast toast = Toast.makeText(MainActivity.this,
                    "    Press 'back' again to exit    ", Toast.LENGTH_SHORT);
            View toastView = toast.getView();
            TextView toastMessage = (TextView) toastView.findViewById(android.R.id.message);

            //Customized toast.
            toastMessage.setTextColor(Color.BLACK);
            toastView.setBackgroundColor(Color.CYAN);
            toast.show();
            exitGame = true;

            //Exits game.
        } else {
            super.onBackPressed();
        }

    }
}
