package vn.org.quantestyoutube2.prm391x_project_4_se00409x;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import vn.org.quantestyoutube2.prm391x_project_4_se00409x.CommandAndInterface.Command;
import vn.org.quantestyoutube2.prm391x_project_4_se00409x.CommandAndInterface.SetupRecyclerView;
import vn.org.quantestyoutube2.prm391x_project_4_se00409x.Database.VideoRoom;
import vn.org.quantestyoutube2.prm391x_project_4_se00409x.Entity.VideoEntity;


public class FirstVideoScreen extends AppCompatActivity {
    private static final String TAG = "FirstVideoScreen";
    public static final String REQUEST_HISTORY = "history , request";

    // List and Room for database management
    private VideoRoom videoRoom;
    private List<VideoEntity> videoHistoryList = new ArrayList<>();


    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);

        // setup reusable toolbar
        Command.setupToolbar(this);

        recyclerView = findViewById(R.id.firstScreenRecyclerView);

        // if received intent does not carry REQUEST_HISTORY setup RecyclerView as channel
        if (getIntent().getStringExtra(REQUEST_HISTORY) == null){

            SetupRecyclerView setupRecyclerView = new SetupRecyclerView();
            setupRecyclerView.execute(recyclerView , null);

            // if received intent does carry REQUEST_HISTORY setup RecyclerView as videos history
        }else if (getIntent().getStringExtra(REQUEST_HISTORY).equals(REQUEST_HISTORY)){

            // Room to initialize or acquire database
            videoRoom = VideoRoom.getInstance(this);


            // if database already exist transfer all to the List
            if (videoRoom.videoDao().countVideos() > 0){
                videoHistoryList = videoRoom.videoDao().getVideoDatabase();
            }

            // setup RecyclerView according to the List
            Command.setupVideoRecyclerViewDefault(this , videoHistoryList , recyclerView);

        }

    }

    // create the 3 dot Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Command.createMenu(this , menu);
        return super.onCreateOptionsMenu(menu);
    }

//when Menu Item is clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            // case click video history
            // if currently in channel switch to history mode else notify the user that they already at it
            case R.id.videoHistory:
                if (getIntent().getStringExtra(REQUEST_HISTORY) == null){
                    getIntent().putExtra(REQUEST_HISTORY , REQUEST_HISTORY);
                    this.recreate();
                } else {
                    Toast.makeText(this, "You are at it", Toast.LENGTH_LONG).show();
                }
            break;

                // case click logout go to SignIn activity
            case R.id.accountLogOut:
                Command.logOut(this);
                break;

                // case click search video will go to search video Activity
            case R.id.searchVideo:
                Intent toSearch = new Intent(this , SearchVideo.class);
                toSearch.putExtra(SignIn.USERNAME_KEY , getIntent().getStringExtra(SignIn.USERNAME_KEY));
                startActivity(toSearch);
                overridePendingTransition(R.animator.push_left_in , R.animator.push_left_out);
                break;

        }
        return super.onOptionsItemSelected(item);
    }


// avoid database leak
    @Override
    protected void onDestroy() {
        VideoRoom.destroyInstance();
        super.onDestroy();
    }

    // depend on channel or history mode to make the hard back button work
    @Override
    public void onBackPressed() {

        if (getIntent().getStringExtra(REQUEST_HISTORY) == null){
            Command.logOut(this);
        } else if (getIntent().getStringExtra(REQUEST_HISTORY).equals(REQUEST_HISTORY)){
            getIntent().removeExtra(REQUEST_HISTORY);
            this.recreate();
        }
    }
}