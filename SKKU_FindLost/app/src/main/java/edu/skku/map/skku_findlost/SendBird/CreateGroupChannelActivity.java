package edu.skku.map.skku_findlost.SendBird;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.sendbird.android.GroupChannel;
import com.sendbird.android.SendBirdException;
import edu.skku.map.skku_findlost.R;

import java.util.ArrayList;
import java.util.List;

/**
 * An Activity to create a new Group Channel.
 * First displays a selectable list of users,
 * then shows an option to create a Distinct channel.
 */

public class CreateGroupChannelActivity extends AppCompatActivity {
    String targetID;

    public static final String EXTRA_NEW_CHANNEL_URL = "EXTRA_NEW_CHANNEL_URL";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group_channel);
        Intent intent = getIntent();
        targetID = intent.getStringExtra("targetID");
        List<String> userIdList = new ArrayList<String>();
        userIdList.add(targetID);
        createGroupChannel(userIdList, true);
    }

    /**
     * Creates a new Group Channel.
     * <p>
     * Note that if you have not included empty channels in your GroupChannelListQuery,
     * the channel will not be shown in the user's channel list until at least one message
     * has been sent inside.
     *
     * @param userIds  The users to be members of the new channel.
     * @param distinct Whether the channel is unique for the selected members.
     *                 If you attempt to create another Distinct channel with the same members,
     *                 the existing channel instance will be returned.
     */
    private void createGroupChannel(List<String> userIds, boolean distinct) {
        GroupChannel.createChannelWithUserIds(userIds, distinct, new GroupChannel.GroupChannelCreateHandler() {
            @Override
            public void onResult(GroupChannel groupChannel, SendBirdException e) {
                if (e != null) {
                    // Error!
                    return;
                }

                Intent intent = new Intent();
                intent.putExtra(EXTRA_NEW_CHANNEL_URL, groupChannel.getUrl());

                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}

