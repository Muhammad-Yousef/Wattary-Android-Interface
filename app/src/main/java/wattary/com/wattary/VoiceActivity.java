package wattary.com.wattary;

/**
 * Editing the Errors .. functions is still buggy
 * amryar10 8/3/2018
 */

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.speech.tts.*;
import android.view.KeyEvent;
import android.view.View;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.comix.overwatch.HiveProgressView;
import com.github.clans.fab.FloatingActionMenu;
import com.github.clans.fab.FloatingActionButton;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static maes.tech.intentanim.CustomIntent.customType;

public class VoiceActivity extends AppCompatActivity implements RecognitionListener , TextToSpeech.OnInitListener {

    //private static String url = "http://104.196.121.39:5000/main";
    private static String url =   "https://wattary2.herokuapp.com/main";
    private String UserName_value_ID;
    //voice recognition
    static final int REQUEST_PERMISSION_KEY = 1;

    private Button Sign_out_btn;


    FloatingActionMenu floatingActionMenu ;
    FloatingActionButton Air,TV,Water, electricity,Chat,Lamp,on_the_door,Curtains,Elevator;
    private int menuBtn;

    private TextView returnedText;
    private TextView Status;
    private ImageButton recordbtn;
    private ImageButton stop_speak_btn;
    private HiveProgressView progressBar;
    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;
    //volley
    String toString;
    String sendString;
    JSONObject jsonObject = new JSONObject();
    //text to speech
    TextToSpeech tts;
    //chat list
    ListView chatListView;
    ChatArrayAdapter adapter;

    VideoView videoView;

    private int Checkspeech;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice);
        //Activity Identity
        returnedText = (TextView) findViewById(R.id.textofSpeech);
        Status = (TextView) findViewById(R.id.status);
        Sign_out_btn=findViewById(R.id.Sign_out_btn);
        progressBar = (HiveProgressView) findViewById(R.id.progressBar1);
        progressBar.setRainbow(true);
        //progressBar.setColor(29695);
        recordbtn = (ImageButton) findViewById(R.id.btnSpeak);
        stop_speak_btn = (ImageButton) findViewById(R.id.stop_Speak_btn);
        //animation
        customType(VoiceActivity.this,"fadein-to-fadeout");

        //getting saved User name from Shared Preferences
        UserName_value_ID =null;
        String v=SharedPrefs.readSharedSettingUsername(VoiceActivity.this, "UserName", UserName_value_ID);
        UserName_value_ID =v;
       // Toast.makeText(VoiceActivity.this, UserName_value_ID, Toast.LENGTH_SHORT).show();
        Checkspeech=0;
        menuBtn=0;
        //Video Background /*created by amryar10*/
        videoView = (VideoView)findViewById(R.id.videoVoice);
        Uri video = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.voice);
        videoView.setVideoURI(video);

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });

        videoView.start();


        //Text to Speech

        tts = new TextToSpeech(this,this);

       //Floating action Menu Implmentation
       floatingActionMenu=(FloatingActionMenu)findViewById(R.id.floatingActionMenu);
        Air=(FloatingActionButton)findViewById(R.id.AirActivity);
        Water=(FloatingActionButton)findViewById(R.id.WaterActivity);
        TV=(FloatingActionButton)findViewById(R.id.TVActivity);
        Chat = (FloatingActionButton) findViewById(R.id.floatingActionButtonChat);
        electricity = (FloatingActionButton) findViewById(R.id.electricityActivity);
        Lamp=findViewById(R.id.LampActivity);
        on_the_door = findViewById(R.id.on_the_door);
        Curtains=findViewById(R.id.curitrns_btn);
        Elevator=findViewById(R.id.elevetor_btn);

      /* floatingActionMenu.setOnMenuButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(menuBtn==0)
                {
                 Status.setVisibility(View.INVISIBLE);
                    menuBtn=1;
                }else if(menuBtn==1)
                {
                    Status.setVisibility(View.VISIBLE);
                    menuBtn=0;
                }

            }
        });

*/
        //Activities Buttons
        Air.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
           //     Toast.makeText(VoiceActivity.this,"Air clicked",Toast.LENGTH_SHORT).show();
                Intent intent =new Intent(VoiceActivity.this,AirConditioner.class);
                startActivity(intent);
                finish();
            }
        });

        Water.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Toast.makeText(VoiceActivity.this,"Water",Toast.LENGTH_SHORT).show();
                Intent intent =new Intent(VoiceActivity.this,Water.class);
                startActivity(intent);
                finish();
            }
        });

        electricity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  Toast.makeText(VoiceActivity.this,"electricity",Toast.LENGTH_SHORT).show();
                Intent intent =new Intent(VoiceActivity.this,electricity.class);
                startActivity(intent);
                finish();
            }
        });

        TV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            //    Toast.makeText(VoiceActivity.this,"TV clicked",Toast.LENGTH_SHORT).show();
                Intent intent =new Intent(VoiceActivity.this,Remote.class);
                startActivity(intent);
                finish();
            }
        });

        Chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(VoiceActivity.this,ChatActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Lamp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent LampIntent=new Intent(VoiceActivity.this,Recommendation.class);
                startActivity(LampIntent);
                finish();

            }
        });

        on_the_door.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Toast.makeText(VoiceActivity.this,"Air clicked",Toast.LENGTH_SHORT).show();
                Intent intent =new Intent(VoiceActivity.this,on_The_Door.class);
                startActivity(intent);
                finish();
            }
        });

        Curtains.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Toast.makeText(VoiceActivity.this,"Air clicked",Toast.LENGTH_SHORT).show();
                Intent intent =new Intent(VoiceActivity.this,Curtines.class);
                startActivity(intent);
                finish();
            }
        });

        Elevator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Toast.makeText(VoiceActivity.this,"Air clicked",Toast.LENGTH_SHORT).show();
                Intent intent =new Intent(VoiceActivity.this,Elevator.class);
                startActivity(intent);
                finish();
            }
        });


        //for listview
        adapter = new ChatArrayAdapter(getApplicationContext(), R.layout.message_send);
        chatListView = (ListView) findViewById(R.id.listview);
        chatListView.setAdapter(adapter);
        chatListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        chatListView.setAdapter(adapter);
        //to scroll the list view to bottom on data change
        adapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                chatListView.setSelection(adapter.getCount() - 1);
            }
        });

        //Animated Background /*Created by amryar10*/
      //  voiceLayout = (RelativeLayout) findViewById(R.id.activity_voice);
       // animationDrawable = (AnimationDrawable) voiceLayout.getBackground();
       // animationDrawable.setEnterFadeDuration(3500);
      //  animationDrawable.setExitFadeDuration(3500);
       // animationDrawable.start();

        String[] PERMISSIONS = {Manifest.permission.RECORD_AUDIO};
        if(!Function.hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_PERMISSION_KEY);
        }


        progressBar.setVisibility(View.INVISIBLE);
        stop_speak_btn.setVisibility(View.INVISIBLE);
        stop_speak_btn.setEnabled(false);
        speech = SpeechRecognizer.createSpeechRecognizer(this);
        speech.setRecognitionListener(this);
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
                "en");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                this.getPackageName());
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);

        //Minimum time to listen in millis. Here 5 seconds

        recognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 5000);
        recognizerIntent.putExtra("android.speech.extra.DICTATION_MODE", true);




        recordbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                // listening
                recordbtn.setEnabled(false);
                recordbtn.setVisibility(View.INVISIBLE);

                progressBar.setVisibility(View.VISIBLE);
                stop_speak_btn.setVisibility(View.VISIBLE);
                stop_speak_btn.setEnabled(true);
                speech.startListening(recognizerIntent);
                Status.setText("Listening");
                Status.setTextColor(Color.parseColor("#FF007864"));
                // Toast.makeText(VoiceActivity.this,"Start",Toast.LENGTH_SHORT ).show();
                Checkspeech=1;

                }


        });
        stop_speak_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // To stop listening

                progressBar.setVisibility(View.INVISIBLE);

                /*speech.destroy();
                    Log.d("Log", "destroy");*/
                speech.cancel();
                Log.d("log", "stop_speak_btn Speech Cancel");
                recordbtn.setEnabled(true);
                stop_speak_btn.setVisibility(View.INVISIBLE);
                stop_speak_btn.setEnabled(false);
                recordbtn.setVisibility(View.VISIBLE);
                Status.setText("Tap on Mic to Speak");
                returnedText.setText(null);
                Status.setTextColor(Color.parseColor("#000000"));
                Checkspeech=0;
               // Toast.makeText(VoiceActivity.this,"stop",Toast.LENGTH_SHORT ).show();
            }

        });

        Sign_out_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPrefs.saveSharedSetting(VoiceActivity.this, "Statues", "false");
                //And when you click on Logout button, You will set the value to True AGAIN
                Intent LogOut = new Intent(VoiceActivity.this, MainActivity.class);
                startActivity(LogOut);
                finish();
            }
        });

        //CekSession();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }



    private void CekSession() {

        Boolean Check = Boolean.valueOf(SharedPrefs.readSharedSetting(VoiceActivity.this, "Statues", "false"));

        Intent introIntent = new Intent(VoiceActivity.this,MainActivity.class);
        introIntent.putExtra("Statues", Check);

        //The Value if you click on Login Activity and Set the value is FALSE and whe false the activity will be visible
        if (Check) {
            startActivity(introIntent);
            finish();
        } //If no the Main Activity not Do Anything
    }

    @Override
    protected void onStart() {
        super.onStart();
        videoView.start();
    }

    @Override
    public void onStop(){
        super.onStop();
        speech.cancel();
        Log.d("log", "onStop: Speech Cancel");
    }

    @Override
    public void onResume() {

        super.onResume();
        videoView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (speech != null) {
            speech.destroy();
            Log.d("Log", "destroy");
        }
        speech.cancel();
        Log.d("log", "onStop: Speech Cancel");
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.d("Log", "onBeginningOfSpeech");
        progressBar.setVisibility(View.VISIBLE);
        stop_speak_btn.setVisibility(View.VISIBLE);
        stop_speak_btn.setEnabled(true);
    }

    @Override
    public void onBufferReceived(byte[] buffer) {

        Log.d("Log", "onBufferReceived: " + buffer);
    }

    @Override
    public void onEndOfSpeech() {
        Log.d("Log", "onEndOfSpeech");
        progressBar.setVisibility(View.INVISIBLE);
        stop_speak_btn.setVisibility(View.INVISIBLE);
        stop_speak_btn.setEnabled(false);
        recordbtn.setEnabled(true);
        recordbtn.setVisibility(View.VISIBLE);
        Status.setText("Tap on Mic to Speak");
        Status.setTextColor(Color.parseColor("#000000"));
        //for listview
        if (toString != null) {
            sendMessageBallon(toString); //--> here's the right place for arrayList.add //User Send Message Method
            sendString = toString;
            toString = null;
        }

        sendPost(url);
    }

    @Override
    public void onError(int errorCode) {
        String errorMessage = getErrorText(errorCode);
        Log.d("Log", "FAILED " + errorMessage);
        progressBar.setVisibility(View.INVISIBLE);
        stop_speak_btn.setVisibility(View.INVISIBLE);
        stop_speak_btn.setEnabled(false);
        recordbtn.setEnabled(true);
        recordbtn.setVisibility(View.VISIBLE);
        returnedText.setText(errorMessage);
        recordbtn.setEnabled(true);
    }

    @Override
    public void onEvent(int arg0, Bundle arg1) {
        Log.d("Log", "onEvent");
    }

    @Override
    public void onPartialResults(Bundle arg0) {
        Log.d("Log", "onPartialResults");

        ArrayList<String> matches = arg0.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String text = "";
        /* To get all close matchs
        for (String result : matches)
        {
            text += result + "\n";
        }
        */
        text = matches.get(0); //  Remove this line while uncommenting above    codes

        returnedText.setText(text);
        //for listview
        if (returnedText != null) {
            toString = text;
        }
    }

    @Override
    public void onReadyForSpeech(Bundle arg0) {
        Log.d("Log", "onReadyForSpeech");
    }

    @Override
    public void onResults(Bundle results) {
        Log.d("Log", "onResults");

    }

    @Override
    public void onRmsChanged(float rmsdB) {
        Log.d("Log", "onRmsChanged: " + rmsdB);
        //progressBar.setProgress((int) rmsdB);

    }

    public static String getErrorText(int errorCode) {
        String message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "error from server";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        return message;
    }

    //Set the Method of Text To Speech
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS){
            Locale locale = tts.getLanguage();
            int r = tts.setLanguage(locale);

            if (r == TextToSpeech.LANG_MISSING_DATA || r == TextToSpeech.LANG_NOT_SUPPORTED){
                Toast.makeText(VoiceActivity.this, "This App is only Support English Language, Please set your Phone language to English", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Set the Method of SendPost to Core
    private void sendPost(String URL) {

        Map<String, String> params = new HashMap<String, String>();
        params.put("message", sendString);
        params.put("userID", UserName_value_ID);

        JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //set what happend when you get the response
                            String fromOmar = (String) response.get("message");
                            receiveMessageBallon(fromOmar); //Server Receive Message Method
                            tts.speak(fromOmar,TextToSpeech.QUEUE_FLUSH,null); //Text to Speech Method
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //OLD VOLLEY ERROR EXCEPTION
                /*VolleyLog.e("Error: ", error.getMessage());
                receiveMessageBallon("Error .. Server is Offline, Please try again later");
                tts.speak("Connection error or server is Disconnected",TextToSpeech.QUEUE_FLUSH,null); //Text to Speech Method*/
                if (error instanceof NetworkError) {
                    receiveMessageBallon("Error .. Cannot connect to Internet, Please check your connection!");
                    tts.speak("Cannot connect to the Internet Please check your connection",TextToSpeech.QUEUE_FLUSH,null); //Text to Speech Method
                } else if (error instanceof ServerError) {
                    receiveMessageBallon("Error .. Server is Offline, Please try again later");
                    tts.speak("The server could not be found Please try again after some time",TextToSpeech.QUEUE_FLUSH,null); //Text to Speech Method
                } else if (error instanceof AuthFailureError) {
                    receiveMessageBallon("Error .. Cannot connect to Internet, Please check your connection!");
                    tts.speak("Cannot connect to the Internet Please check your connection",TextToSpeech.QUEUE_FLUSH,null); //Text to Speech Method
                } else if (error instanceof ParseError) {
                    receiveMessageBallon("Error .. Parsing error!, Please try again after some time!");
                    tts.speak("Parsing error Please try again",TextToSpeech.QUEUE_FLUSH,null); //Text to Speech Method
                } else if (error instanceof TimeoutError) {
                    receiveMessageBallon("Error .. Connection TimedOut!, Please check your internet connection!");
                    tts.speak("Connection TimedOut Please check your connection",TextToSpeech.QUEUE_FLUSH,null); //Text to Speech Method
                }
            }
        }


        );

        int socketTimeout = 10000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        req.setRetryPolicy(policy);



        // add the request object to the queue to be executed
        Controller.getInstance().addToRequestQueue(req);
    }

    //Methods of Ballon Message
    private boolean sendMessageBallon (String msg) {
        adapter.add(new ChatMessage(true, msg));
        return true;
    }


    private boolean receiveMessageBallon (String msg) {
        adapter.add(new ChatMessage(false, msg));
        return true;
    }
}

class Function {

    public static  boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}


